
package net.imagej.omero.legacy.convert;

import ij.gui.Roi;

import net.imagej.legacy.convert.roi.DefaultRoiWrapper;
import net.imagej.legacy.convert.roi.IJRoiWrapper;
import net.imagej.legacy.convert.roi.ShapeRoiWrapper;
import net.imagej.legacy.convert.roi.box.RoiWrapper;
import net.imagej.legacy.convert.roi.ellipsoid.OvalRoiWrapper;
import net.imagej.legacy.convert.roi.line.IJLineWrapper;
import net.imagej.legacy.convert.roi.polygon2d.PolygonRoiWrapper;
import net.imagej.legacy.convert.roi.polyline.IrregularPolylineRoiWrapper;
import net.imagej.legacy.convert.roi.polyline.PolylineRoiWrapper;
import net.imagej.legacy.convert.roi.polyline.UnmodifiablePolylineRoiWrapper;
import net.imagej.omero.legacy.text.TextRoiWrapper;
import net.imagej.omero.roi.ellipse.ImageJToOMEROEllipse;
import net.imagej.omero.roi.line.ImageJToOMEROLine;
import net.imagej.omero.roi.mask.MaskIntervalToMaskData;
import net.imagej.omero.roi.mask.RealMaskRealIntervalToMaskData;
import net.imagej.omero.roi.polygon.ImageJToOMEROPolygon;
import net.imagej.omero.roi.polyline.ImageJToOMEROPolyline;
import net.imagej.omero.roi.rectangle.ImageJToOMERORectangle;

import ome.formats.model.UnitsFactory;
import omero.gateway.model.EllipseData;
import omero.gateway.model.LineData;
import omero.gateway.model.MaskData;
import omero.gateway.model.PolygonData;
import omero.gateway.model.PolylineData;
import omero.gateway.model.RectangleData;
import omero.gateway.model.ShapeData;
import omero.gateway.model.TextData;
import omero.model.LengthI;
import omero.model.Shape;

import org.scijava.Priority;
import org.scijava.convert.AbstractConverter;
import org.scijava.convert.ConvertService;
import org.scijava.convert.Converter;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Converters which convert {@link IJRoiWrapper}s to {@link ShapeData}, and
 * preserve corresponding settings not represent in MaskPredicate.
 *
 * @author Alison Walter
 */
public class IJRoiWrapperToShapeData {

	/**
	 * Abstract base class for converting {@link IJRoiWrapper} to
	 * {@link ShapeData}
	 */
	public static abstract class AbstractIJRoiWrapperToShapeData<I extends IJRoiWrapper<?, ?>, S extends ShapeData>
		extends AbstractConverter<I, S>
	{

		@Override
		@SuppressWarnings("unchecked")
		public <T> T convert(final Object src, final Class<T> dest) {
			if (!getInputType().isInstance(src)) throw new IllegalArgumentException(
				"Expected: " + getInputType() + " Received: " + src.getClass());
			if (!dest.isAssignableFrom(getOutputType()))
				throw new IllegalArgumentException("Expected: " + getOutputType() +
					" Received: " + dest);

			final S shape = convert((I) src);
			final Shape shapeI = (Shape) shape.asIObject();
			final Roi roi = ((I) src).getRoi();

			if (roi.getZPosition() != 0) shapeI.setTheZ(omero.rtypes.rint(roi
				.getZPosition() - 1));
			if (roi.getTPosition() != 0) shapeI.setTheT(omero.rtypes.rint(roi
				.getTPosition() - 1));
			if (roi.getCPosition() != 0) shapeI.setTheC(omero.rtypes.rint(roi
				.getCPosition() - 1));

			if (roi.getStrokeWidth() > 0) shape.getShapeSettings().setStrokeWidth(
				new LengthI(roi.getStrokeWidth(), UnitsFactory.Shape_StrokeWidth));
			if (roi.getStrokeColor() != null) shape.getShapeSettings().setStroke(roi
				.getStrokeColor());
			if (roi.getFillColor() != null) shape.getShapeSettings().setFill(roi
				.getFillColor());

			return (T) shape;
		}

		public abstract S convert(final I wrapper);
	}

	/**
	 * Converts {@link DefaultRoiWrapper} to {@link MaskData}, and preserves
	 * settings not represented in MaskPredicate.
	 */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public final static class DefaultRoiWrapperToMaskData extends
		AbstractIJRoiWrapperToShapeData<DefaultRoiWrapper<?>, MaskData>
	{

		@Parameter
		private ConvertService convertService;

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class<DefaultRoiWrapper<?>> getInputType() {
			return (Class) DefaultRoiWrapper.class;
		}

		@Override
		public Class<MaskData> getOutputType() {
			return MaskData.class;
		}

		@Override
		public MaskData convert(final DefaultRoiWrapper<?> wrapper) {
			return convertService.getInstance(MaskIntervalToMaskData.class).convert(
				wrapper, MaskData.class);
		}

	}

	/**
	 * Converts {@link IJLineWrapper} to {@link LineData}, and preserves settings
	 * not represented in MaskPredicate.
	 */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public final static class IJLineWrapperToLineData extends
		AbstractIJRoiWrapperToShapeData<IJLineWrapper, LineData>
	{

		@Parameter
		private ConvertService convertService;

		@Override
		public Class<IJLineWrapper> getInputType() {
			return IJLineWrapper.class;
		}

		@Override
		public Class<LineData> getOutputType() {
			return LineData.class;
		}

		@Override
		public LineData convert(final IJLineWrapper wrapper) {
			return convertService.getInstance(ImageJToOMEROLine.class).convert(
				wrapper, LineData.class);
		}

	}

	/**
	 * Converts {@link IrregularPolylineRoiWrapper} to {@link MaskData}, and
	 * preserves settings not represented in MaskPredicate.
	 */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public final static class IrregularPolylineRoiWrapperToMaskData extends
		AbstractIJRoiWrapperToShapeData<IrregularPolylineRoiWrapper, MaskData>
	{

		@Parameter
		private ConvertService convertService;

		@Override
		public Class<IrregularPolylineRoiWrapper> getInputType() {
			return IrregularPolylineRoiWrapper.class;
		}

		@Override
		public Class<MaskData> getOutputType() {
			return MaskData.class;
		}

		@Override
		public MaskData convert(final IrregularPolylineRoiWrapper wrapper) {
			return convertService.getInstance(RealMaskRealIntervalToMaskData.class)
				.convert(wrapper, MaskData.class);
		}

	}

	/**
	 * Converts {@link OvalRoiWrapper} to {@link EllipseData}, and preserves
	 * settings not represented in MaskPredicate.
	 */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public final static class OvalRoiWrapperToEllipseData extends
		AbstractIJRoiWrapperToShapeData<OvalRoiWrapper, EllipseData>
	{

		@Parameter
		private ConvertService convertService;

		@Override
		public Class<OvalRoiWrapper> getInputType() {
			return OvalRoiWrapper.class;
		}

		@Override
		public Class<EllipseData> getOutputType() {
			return EllipseData.class;
		}

		@Override
		public EllipseData convert(final OvalRoiWrapper wrapper) {
			return convertService.getInstance(ImageJToOMEROEllipse.class).convert(
				wrapper, EllipseData.class);
		}

	}

	/**
	 * Converts {@link PolygonRoiWrapper} to {@link PolygonData}, and preserves
	 * settings not represented in MaskPredicate.
	 */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public final static class PolygonRoiWrapperToPolygonData extends
		AbstractIJRoiWrapperToShapeData<PolygonRoiWrapper, PolygonData>
	{

		@Parameter
		private ConvertService convertService;

		@Override
		public Class<PolygonRoiWrapper> getInputType() {
			return PolygonRoiWrapper.class;
		}

		@Override
		public Class<PolygonData> getOutputType() {
			return PolygonData.class;
		}

		@Override
		public PolygonData convert(final PolygonRoiWrapper wrapper) {
			return convertService.getInstance(ImageJToOMEROPolygon.class).convert(
				wrapper, PolygonData.class);
		}

	}

	/**
	 * Converts {@link PolylineRoiWrapper} to {@link PolylineData}, and preserves
	 * settings not represented in MaskPredicate.
	 */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public final static class PolylineRoiWrapperToPolylineData extends
		AbstractIJRoiWrapperToShapeData<PolylineRoiWrapper, PolylineData>
	{

		@Parameter
		private ConvertService convertService;

		@Override
		public Class<PolylineRoiWrapper> getInputType() {
			return PolylineRoiWrapper.class;
		}

		@Override
		public Class<PolylineData> getOutputType() {
			return PolylineData.class;
		}

		@Override
		public PolylineData convert(final PolylineRoiWrapper wrapper) {
			return convertService.getInstance(ImageJToOMEROPolyline.class).convert(
				wrapper, PolylineData.class);
		}

	}

	/**
	 * Converts {@link RoiWrapper} to {@link RectangleData}, and preserves
	 * settings not represented in MaskPredicate.
	 */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public final static class RoiWrapperToRectangleData extends
		AbstractIJRoiWrapperToShapeData<RoiWrapper, RectangleData>
	{

		@Parameter
		private ConvertService convertService;

		@Override
		public Class<RoiWrapper> getInputType() {
			return RoiWrapper.class;
		}

		@Override
		public Class<RectangleData> getOutputType() {
			return RectangleData.class;
		}

		@Override
		public RectangleData convert(final RoiWrapper wrapper) {
			return convertService.getInstance(ImageJToOMERORectangle.class).convert(
				wrapper, RectangleData.class);
		}

	}

	/**
	 * Converts {@link ShapeRoiWrapper} to {@link MaskData}, and preserves
	 * settings not represented in MaskPredicate.
	 */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public final static class ShapeRoiWrapperToMaskData extends
		AbstractIJRoiWrapperToShapeData<ShapeRoiWrapper, MaskData>
	{

		@Parameter
		private ConvertService convertService;

		@Override
		public Class<ShapeRoiWrapper> getInputType() {
			return ShapeRoiWrapper.class;
		}

		@Override
		public Class<MaskData> getOutputType() {
			return MaskData.class;
		}

		@Override
		public MaskData convert(final ShapeRoiWrapper wrapper) {
			return convertService.getInstance(RealMaskRealIntervalToMaskData.class)
				.convert(wrapper, MaskData.class);
		}

	}

	/**
	 * Converts {@link TextRoiWrapper} to {@link TextData}, and preserves settings
	 * not represented in MaskPredicate.
	 */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public final static class TextRoiWrapperToTextData extends
		AbstractIJRoiWrapperToShapeData<TextRoiWrapper, TextData>
	{

		@Override
		public Class<TextRoiWrapper> getInputType() {
			return TextRoiWrapper.class;
		}

		@Override
		public Class<TextData> getOutputType() {
			return TextData.class;
		}

		@Override
		public TextData convert(final TextRoiWrapper wrapper) {
			return new TextData(wrapper.getRoi().getText(), wrapper.getRoi()
				.getXBase(), wrapper.getRoi().getYBase());
		}

	}

	/**
	 * Converts {@link UnmodifiablePolylineRoiWrapper} to {@link PolylineData},
	 * and preserves settings not represented in MaskPredicate.
	 */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public final static class UnmodifiablePolylineRoiWrapperToPolylineData extends
		AbstractIJRoiWrapperToShapeData<UnmodifiablePolylineRoiWrapper, PolylineData>
	{

		@Parameter
		private ConvertService convertService;

		@Override
		public Class<UnmodifiablePolylineRoiWrapper> getInputType() {
			return UnmodifiablePolylineRoiWrapper.class;
		}

		@Override
		public Class<PolylineData> getOutputType() {
			return PolylineData.class;
		}

		@Override
		public PolylineData convert(final UnmodifiablePolylineRoiWrapper wrapper) {
			return convertService.getInstance(ImageJToOMEROPolyline.class).convert(
				wrapper, PolylineData.class);
		}

	}

}
