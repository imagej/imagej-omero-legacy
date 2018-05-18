
package net.imagej.omero.legacy.convert;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import net.imagej.legacy.convert.roi.point.PointRoiWrapper;
import net.imagej.omero.roi.point.TreeNodeRPCToROIData;

import org.scijava.Priority;
import org.scijava.convert.AbstractConverter;
import org.scijava.convert.ConvertService;
import org.scijava.convert.Converter;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.util.TreeNode;

import ij.gui.PointRoi;
import ome.formats.model.UnitsFactory;
import omero.gateway.model.PointData;
import omero.gateway.model.ROIData;
import omero.gateway.model.ShapeData;
import omero.model.LengthI;
import omero.model.Point;

/**
 * Converts a {@link PointRoiWrapper} to a {@link ROIData}. Since
 * {@link PointRoi}s can have a single or many points, this cannot be converted
 * to {@link ShapeData}. This converter also preserves settings shared between
 * ImageJ 1.x ROIs and OMERO ROIs, but not MaskPredicates.
 * 
 * @author Alison Walter
 */
@Plugin(type = Converter.class, priority = Priority.EXTREMELY_HIGH)
public class TreeNodePointRoiWrapperToROIData extends
	AbstractConverter<TreeNode<PointRoiWrapper>, ROIData>
{

	@Parameter
	private ConvertService convertService;

	@Override
	public boolean canConvert(final Object src, final Type dest) {
		return super.canConvert(src, dest) && ((TreeNode<?>) src)
			.data() instanceof PointRoiWrapper;
	}

	@Override
	public boolean canConvert(final Object src, final Class<?> dest) {
		return super.canConvert(src, dest) && ((TreeNode<?>) src)
			.data() instanceof PointRoiWrapper;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class<TreeNode<PointRoiWrapper>> getInputType() {
		return (Class) TreeNode.class;
	}

	@Override
	public Class<ROIData> getOutputType() {
		return ROIData.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T convert(final Object src, final Class<T> dest) {
		if (!getInputType().isInstance(src)) throw new IllegalArgumentException(
			"Expected: " + getInputType() + " Received: " + src.getClass());
		if (!(((TreeNode<?>) src).data() instanceof PointRoiWrapper))
			throw new IllegalArgumentException(
				"TreeNode data must be PointRoiWrapper, not " + ((TreeNode<?>) src)
					.data());
		if (!dest.isAssignableFrom(getOutputType()))
			throw new IllegalArgumentException("Expected: " + getOutputType() +
				" Received: " + dest);

		final ROIData roiData = convertService.getInstance(
			TreeNodeRPCToROIData.class).convert(src, ROIData.class);
		final PointRoi ijRoi = ((PointRoiWrapper) ((TreeNode<?>) src).data())
			.getRoi();

		final Iterator<List<ShapeData>> itr = roiData.getIterator();
		while (itr.hasNext()) {
			for (final ShapeData shape : itr.next()) {
				if (shape instanceof PointData) setAdditionalSettings((PointData) shape,
					ijRoi);
				else throw new IllegalArgumentException("Not PointData!?");
			}
		}

		return (T) roiData;
	}

	// -- Helper methods --

	private void setAdditionalSettings(final PointData omeroPoint,
		final PointRoi ijPoint)
	{
		final Point omeroPointI = (Point) omeroPoint.asIObject();

		if (ijPoint.getZPosition() != 0) omeroPointI.setTheZ(omero.rtypes.rint(
			ijPoint.getZPosition() - 1));
		if (ijPoint.getTPosition() != 0) omeroPointI.setTheT(omero.rtypes.rint(
			ijPoint.getTPosition() - 1));
		if (ijPoint.getCPosition() != 0) omeroPointI.setTheC(omero.rtypes.rint(
			ijPoint.getCPosition() - 1));

		if (ijPoint.getStrokeWidth() > 0) omeroPoint.getShapeSettings()
			.setStrokeWidth(new LengthI(ijPoint.getStrokeWidth(),
				UnitsFactory.Shape_StrokeWidth));
		if (ijPoint.getStrokeColor() != null) omeroPoint.getShapeSettings()
			.setStroke(ijPoint.getStrokeColor());
		if (ijPoint.getFillColor() != null) omeroPoint.getShapeSettings().setFill(
			ijPoint.getFillColor());
	}

}
