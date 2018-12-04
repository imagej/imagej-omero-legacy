
package net.imagej.omero.legacy.convert;

import java.lang.reflect.Type;

import ij.IJ;
import ij.Prefs;
import ij.gui.Roi;

import net.imagej.legacy.convert.roi.MaskPredicateWrapper;
import net.imagej.legacy.convert.roi.box.BoxWrapper;
import net.imagej.legacy.convert.roi.ellipsoid.EllipsoidWrapper;
import net.imagej.legacy.convert.roi.line.LineWrapper;
import net.imagej.legacy.convert.roi.point.PointMaskWrapper;
import net.imagej.legacy.convert.roi.polygon2d.Polygon2DWrapper;
import net.imagej.legacy.convert.roi.polyline.PolylineWrapper;
import net.imagej.omero.legacy.LegacyOMEROROIService;
import net.imagej.omero.legacy.mask.OMEROMaskWrapper;
import net.imagej.omero.legacy.text.OMEROText;
import net.imagej.omero.legacy.text.OMEROTextWrapper;
import net.imagej.omero.roi.OMERORealMask;
import net.imagej.omero.roi.ellipse.OMEROEllipse;
import net.imagej.omero.roi.line.OMEROLine;
import net.imagej.omero.roi.mask.OMEROMask;
import net.imagej.omero.roi.point.OMEROPoint;
import net.imagej.omero.roi.polyshape.OMEROPolygon;
import net.imagej.omero.roi.polyshape.OMEROPolyline;
import net.imagej.omero.roi.rectangle.OMERORectangle;
import net.imglib2.roi.MaskPredicate;

import ome.formats.model.UnitsFactory;
import omero.RInt;
import omero.gateway.model.EllipseData;
import omero.gateway.model.LineData;
import omero.gateway.model.MaskData;
import omero.gateway.model.PointData;
import omero.gateway.model.PolygonData;
import omero.gateway.model.PolylineData;
import omero.gateway.model.RectangleData;
import omero.gateway.model.ShapeData;
import omero.model.LengthI;
import omero.model.Shape;

import org.scijava.Priority;
import org.scijava.convert.AbstractConverter;
import org.scijava.convert.Converter;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Converts a {@link MaskPredicateWrapper} which wraps an {@link OMERORealMask}
 * to a {@link MaskPredicate}. These conversions preserve some of the meta-data
 * presented in {@link ShapeData} and {@link Roi}, but not in
 * {@link MaskPredicate}.
 *
 * @author Alison Walter
 */
public final class WrappedOMERORealMaskToMaskPredicate {

	private WrappedOMERORealMaskToMaskPredicate() {
		// NB: prevent instantiation of base class
	}

	/**
	 * Abstract base class for converting wrapped {@link OMERORealMask}s to
	 * {@link OMERORealMask}.
	 *
	 * @param <W> type of {@link MaskPredicateWrapper}, the type must also be a
	 *          {@link Roi}.
	 * @param <O> type of {@link OMERORealMask} output.
	 */
	public static abstract class AbstractWrappedOMERORealMaskToMaskPredicate<W extends MaskPredicateWrapper<?>, O extends OMERORealMask<?>>
		extends AbstractConverter<W, O>
	{

		@Parameter
		private LogService log;

		@Override
		public boolean canConvert(final Object src, final Type dest) {
			return super.canConvert(src, dest) && getOutputType().isInstance(
				((MaskPredicateWrapper<?>) src).getSource()) && src instanceof Roi;
		}

		@Override
		public boolean canConvert(final Object src, final Class<?> dest) {
			return super.canConvert(src, dest) && getOutputType().isInstance(
				((MaskPredicateWrapper<?>) src).getSource()) && src instanceof Roi;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T convert(final Object src, final Class<T> dest) {
			if (src == null || dest == null) throw new NullPointerException();
			if (!getInputType().isInstance(src)) {
				throw new IllegalArgumentException("Expected: " + getInputType()
					.getSimpleName() + " Received: " + src.getClass().getSimpleName());
			}
			if (!dest.isAssignableFrom(getOutputType())) {
				throw new IllegalArgumentException("Expected: " + getOutputType()
					.getSimpleName() + " Received: " + dest.getSimpleName());
			}
			if (!(src instanceof Roi)) throw new IllegalArgumentException(
				"MaskPredicateWrapper must also be instance of ij.gui.Roi!");

			final Roi ijRoi = (Roi) src;
			final ShapeData omeroRoi =
				((OMERORealMask<?>) ((MaskPredicateWrapper<?>) src).getSource())
					.getShape();

			// Set position
			// NB: If showAllSliceOnly and isMacro are both false, ImageJ overwrites
			// the position
			boolean ignoreIJPosition = false;
			if (!Prefs.showAllSliceOnly && !IJ.isMacro()) ignoreIJPosition = true;

			final Shape s = (Shape) omeroRoi.asIObject();
			s.setTheZ(computePosition(ignoreIJPosition, ijRoi.getProperty(
				LegacyOMEROROIService.OMERO_ROI_Z), ijRoi.getZPosition()));
			s.setTheT(computePosition(ignoreIJPosition, ijRoi.getProperty(
				LegacyOMEROROIService.OMERO_ROI_T), ijRoi.getTPosition()));
			s.setTheC(computePosition(ignoreIJPosition, ijRoi.getProperty(
				LegacyOMEROROIService.OMERO_ROI_C), ijRoi.getCPosition()));

			if (ijRoi.getStrokeWidth() > 0) omeroRoi.getShapeSettings()
				.setStrokeWidth(new LengthI(ijRoi.getStrokeWidth(),
					UnitsFactory.Shape_StrokeWidth));
			if (ijRoi.getStrokeColor() != null) omeroRoi.getShapeSettings().setStroke(
				ijRoi.getStrokeColor());
			if (ijRoi.getFillColor() != null) omeroRoi.getShapeSettings().setFill(
				ijRoi.getFillColor());
			setText((W) src);

			return (T) ((W) src).getUpdatedSource();
		}

		public abstract void setText(W wrapper);

		private RInt computePosition(final boolean ignoreIJPos,
			final String omeroPos, final int ijPos)
		{
			if (ignoreIJPos && omeroPos != null && !omeroPos.isEmpty()) {
				final int pos = Integer.parseInt(omeroPos);
				// NB: Do NOT set this to n -1, null always!
				if (pos < 0) return null;
				return omero.rtypes.rint(pos);
			}
			if (ijPos == 0) return null;
			return omero.rtypes.rint(ijPos - 1);
		}
	}

	/**
	 * Converts {@link EllipsoidWrapper} which wraps {@link OMEROEllipse} to
	 * {@link OMEROEllipse}.
	 */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public static class WrappedOMEROEllipseToOMEROEllipse extends
		AbstractWrappedOMERORealMaskToMaskPredicate<EllipsoidWrapper, OMEROEllipse>
	{

		@Override
		public Class<OMEROEllipse> getOutputType() {
			return OMEROEllipse.class;
		}

		@Override
		public Class<EllipsoidWrapper> getInputType() {
			return EllipsoidWrapper.class;
		}

		@Override
		public void setText(final EllipsoidWrapper wrapper) {
			if (wrapper.getName() != null && !wrapper.getName().isEmpty()) {
				final EllipseData e = ((OMEROEllipse) wrapper.getSource()).getShape();
				e.setText(wrapper.getName());
			}
		}
	}

	/**
	 * Converts {@link LineWrapper} which wraps {@link OMEROLine} to
	 * {@link OMEROLine}.
	 */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public static class WrappedOMEROLineToOMEROLine extends
		AbstractWrappedOMERORealMaskToMaskPredicate<LineWrapper, OMEROLine>
	{

		@Override
		public Class<OMEROLine> getOutputType() {
			return OMEROLine.class;
		}

		@Override
		public Class<LineWrapper> getInputType() {
			return LineWrapper.class;
		}

		@Override
		public void setText(final LineWrapper wrapper) {
			if (wrapper.getName() != null && !wrapper.getName().isEmpty()) {
				final LineData d = ((OMEROLine) wrapper.getSource()).getShape();
				d.setText(wrapper.getName());
			}
		}
	}

	/** Converts {@link OMEROMaskWrapper} to {@link OMEROMask}. */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public static class OMEROMaskWrapperToOMEROMask extends
		AbstractWrappedOMERORealMaskToMaskPredicate<OMEROMaskWrapper, OMEROMask>
	{

		@Override
		public Class<OMEROMask> getOutputType() {
			return OMEROMask.class;
		}

		@Override
		public Class<OMEROMaskWrapper> getInputType() {
			return OMEROMaskWrapper.class;
		}

		@Override
		public void setText(final OMEROMaskWrapper wrapper) {
			if (wrapper.getName() != null && !wrapper.getName().isEmpty()) {
				final MaskData d = wrapper.getSource().getShape();
				d.setText(wrapper.getName());
			}
		}
	}

	/**
	 * Converts {@link PointMaskWrapper} which wraps {@link OMEROPoint} to
	 * {@link OMEROPoint}.
	 */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public static class WrappedOMEROPointToOMEROPoint extends
		AbstractWrappedOMERORealMaskToMaskPredicate<PointMaskWrapper, OMEROPoint>
	{

		@Override
		public Class<OMEROPoint> getOutputType() {
			return OMEROPoint.class;
		}

		@Override
		public Class<PointMaskWrapper> getInputType() {
			return PointMaskWrapper.class;
		}

		@Override
		public void setText(final PointMaskWrapper wrapper) {
			if (wrapper.getName() != null && !wrapper.getName().isEmpty()) {
				final PointData d = ((OMEROPoint) wrapper.getSource()).getShape();
				d.setText(wrapper.getName());
			}
		}
	}

	/**
	 * Converts {@link PolylineWrapper} which wraps {@link OMEROPolyline} to
	 * {@link OMEROPolyline}.
	 */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public static class WrappedOMEROPolylineToOMEROPolyline extends
		AbstractWrappedOMERORealMaskToMaskPredicate<PolylineWrapper, OMEROPolyline>
	{

		@Override
		public Class<OMEROPolyline> getOutputType() {
			return OMEROPolyline.class;
		}

		@Override
		public Class<PolylineWrapper> getInputType() {
			return PolylineWrapper.class;
		}

		@Override
		public void setText(final PolylineWrapper wrapper) {
			if (wrapper.getName() != null && !wrapper.getName().isEmpty()) {
				final PolylineData d = ((OMEROPolyline) wrapper.getSource()).getShape();
				d.setText(wrapper.getName());
			}
		}
	}

	/**
	 * Converts {@link Polygon2DWrapper} which wraps {@link OMEROPolygon} to
	 * {@link OMEROPolygon}.
	 */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public static class WrappedOMEROPolygonToOMEROPolygon extends
		AbstractWrappedOMERORealMaskToMaskPredicate<Polygon2DWrapper, OMEROPolygon>
	{

		@Override
		public Class<OMEROPolygon> getOutputType() {
			return OMEROPolygon.class;
		}

		@Override
		public Class<Polygon2DWrapper> getInputType() {
			return Polygon2DWrapper.class;
		}

		@Override
		public void setText(final Polygon2DWrapper wrapper) {
			if (wrapper.getName() != null && !wrapper.getName().isEmpty()) {
				final PolygonData d = ((OMEROPolygon) wrapper.getSource()).getShape();
				d.setText(wrapper.getName());
			}
		}
	}

	/**
	 * Converts {@link BoxWrapper} which wraps {@link OMERORectangle} to
	 * {@link OMERORectangle}.
	 */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public static class WrappedOMERORectangleToOMERORectangle extends
		AbstractWrappedOMERORealMaskToMaskPredicate<BoxWrapper, OMERORectangle>
	{

		@Override
		public Class<OMERORectangle> getOutputType() {
			return OMERORectangle.class;
		}

		@Override
		public Class<BoxWrapper> getInputType() {
			return BoxWrapper.class;
		}

		@Override
		public void setText(final BoxWrapper wrapper) {
			if (wrapper.getName() != null && !wrapper.getName().isEmpty()) {
				final RectangleData d = ((OMERORectangle) wrapper.getSource())
					.getShape();
				d.setText(wrapper.getName());
			}
		}
	}

	/**
	 * Converts {@link OMEROTextWrapper} which wraps {@link OMEROText} to
	 * {@link OMEROText}.
	 */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public static class WrappedOMEROTextToOMEROText extends
		AbstractWrappedOMERORealMaskToMaskPredicate<OMEROTextWrapper, OMEROText>
	{

		@Override
		public Class<OMEROText> getOutputType() {
			return OMEROText.class;
		}

		@Override
		public Class<OMEROTextWrapper> getInputType() {
			return OMEROTextWrapper.class;
		}

		@Override
		public void setText(final OMEROTextWrapper wrapper) {
			// Do nothing
		}
	}
}
