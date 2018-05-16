
package net.imagej.omero.legacy.convert;

import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.PointRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;

import net.imagej.legacy.convert.roi.box.BoxWrapper;
import net.imagej.legacy.convert.roi.ellipsoid.EllipsoidWrapper;
import net.imagej.legacy.convert.roi.line.LineWrapper;
import net.imagej.legacy.convert.roi.point.PointMaskWrapper;
import net.imagej.legacy.convert.roi.polygon2d.Polygon2DWrapper;
import net.imagej.legacy.convert.roi.polyline.PolylineWrapper;
import net.imagej.omero.legacy.LegacyOMEROROIService;
import net.imagej.omero.roi.OMERORealMask;
import net.imagej.omero.roi.ellipse.OMEROEllipse;
import net.imagej.omero.roi.line.OMEROLine;
import net.imagej.omero.roi.point.OMEROPoint;
import net.imagej.omero.roi.polygon.OMEROPolygon;
import net.imagej.omero.roi.polyline.OMEROPolyline;
import net.imagej.omero.roi.rectangle.OMERORectangle;
import net.imglib2.roi.MaskPredicate;

import ome.model.units.BigResult;
import omero.gateway.model.ShapeData;
import omero.model.enums.UnitsLength;

import org.scijava.Priority;
import org.scijava.convert.AbstractConverter;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

/**
 * Converters for {@link OMERORealMask} to {@link Roi}. These converters attempt
 * to preserve some of the additional information represented in
 * {@link ShapeData} but not in {@link MaskPredicate}.
 *
 * @author Alison Walter
 */
public final class OMERORealMaskToIJROI {

	private OMERORealMaskToIJROI() {
		// NB: prevent instantiation of base class
	}

	/**
	 * Abstract base class for converting {@link OMERORealMask} to {@link Roi}.
	 *
	 * @param <O> the type of {@link OMERORealMask}
	 * @param <R> the type of {@link Roi}
	 */
	public static abstract class AbstractOMERORealMaskToIJRoi<O extends OMERORealMask<?>, R extends Roi>
		extends AbstractConverter<O, R>
	{

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

			final O omeroRoi = (O) src;
			final ShapeData shape = omeroRoi.getShape();
			final R ijRoi = wrap(omeroRoi);

			// set position
			ijRoi.setPosition(shape.getC() + 1, shape.getZ() + 1, shape.getT() + 1);

			// store position in case un-intentionally overwritten
			ijRoi.setProperty(LegacyOMEROROIService.OMERO_ROI_Z, Long.toString(shape
				.getZ()));
			ijRoi.setProperty(LegacyOMEROROIService.OMERO_ROI_T, Long.toString(shape
				.getT()));
			ijRoi.setProperty(LegacyOMEROROIService.OMERO_ROI_C, Long.toString(shape
				.getC()));

			// set style
			try {
				if (shape.getShapeSettings().getStrokeWidth(UnitsLength.PIXEL) != null)
					ijRoi.setStrokeWidth(shape.getShapeSettings().getStrokeWidth(
						UnitsLength.PIXEL).getValue());
			}
			catch (final BigResult exc) {
				// Do nothing. Stroke width won't be preserved
			}
			if (shape.getShapeSettings().getStroke() != null) ijRoi.setStrokeColor(
				shape.getShapeSettings().getStroke());
			if (shape.getShapeSettings().getFill() != null) ijRoi.setFillColor(shape
				.getShapeSettings().getFill());

			// set name
			if (getText(omeroRoi) != null && !getText(omeroRoi).isEmpty()) ijRoi
				.setName(getText(omeroRoi));

			return (T) ijRoi;
		}

		public abstract R wrap(final O omeroRoi);

		public abstract String getText(final O omeroRoi);
	}

	/** Converts {@link OMEROEllipse} to {@link OvalRoi}. */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public static class OMEROEllipseToOvalRoi extends
		AbstractOMERORealMaskToIJRoi<OMEROEllipse, OvalRoi>
	{

		@Override
		public Class<OvalRoi> getOutputType() {
			return OvalRoi.class;
		}

		@Override
		public Class<OMEROEllipse> getInputType() {
			return OMEROEllipse.class;
		}

		@Override
		public OvalRoi wrap(final OMEROEllipse omeroRoi) {
			return new EllipsoidWrapper(omeroRoi);
		}

		@Override
		public String getText(final OMEROEllipse omeroRoi) {
			return omeroRoi.getShape().getText();
		}
	}

	/** Converts {@link OMEROLine} to {@link ij.gui.Line}. */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public static class OMEROLineToIJLine extends
		AbstractOMERORealMaskToIJRoi<OMEROLine, ij.gui.Line>
	{

		@Override
		public Class<ij.gui.Line> getOutputType() {
			return ij.gui.Line.class;
		}

		@Override
		public Class<OMEROLine> getInputType() {
			return OMEROLine.class;
		}

		@Override
		public Line wrap(final OMEROLine omeroRoi) {
			return new LineWrapper(omeroRoi);
		}

		@Override
		public String getText(final OMEROLine omeroRoi) {
			return omeroRoi.getShape().getText();
		}
	}

	/** Converts {@link OMEROPoint} to {@link PointRoi}. */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public static class OMEROPointToPointRoi extends
		AbstractOMERORealMaskToIJRoi<OMEROPoint, PointRoi>
	{

		@Override
		public Class<PointRoi> getOutputType() {
			return PointRoi.class;
		}

		@Override
		public Class<OMEROPoint> getInputType() {
			return OMEROPoint.class;
		}

		@Override
		public PointRoi wrap(final OMEROPoint omeroRoi) {
			return new PointMaskWrapper(omeroRoi);
		}

		@Override
		public String getText(final OMEROPoint omeroRoi) {
			return omeroRoi.getShape().getText();
		}
	}

	/** Converts {@link OMEROPolygon} to {@link PolygonRoi}. */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public static class OMEROPolygonToPolygonRoi extends
		AbstractOMERORealMaskToIJRoi<OMEROPolygon, PolygonRoi>
	{

		@Override
		public Class<PolygonRoi> getOutputType() {
			return PolygonRoi.class;
		}

		@Override
		public Class<OMEROPolygon> getInputType() {
			return OMEROPolygon.class;
		}

		@Override
		public PolygonRoi wrap(final OMEROPolygon omeroRoi) {
			return new Polygon2DWrapper(omeroRoi);
		}

		@Override
		public String getText(final OMEROPolygon omeroRoi) {
			return omeroRoi.getShape().getText();
		}
	}

	/** Converts {@link OMEROPolyline} to {@link PolygonRoi}. */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public static class OMEROPolylineToPolygonRoi extends
		AbstractOMERORealMaskToIJRoi<OMEROPolyline, PolygonRoi>
	{

		@Override
		public Class<PolygonRoi> getOutputType() {
			return PolygonRoi.class;
		}

		@Override
		public Class<OMEROPolyline> getInputType() {
			return OMEROPolyline.class;
		}

		@Override
		public PolygonRoi wrap(final OMEROPolyline omeroRoi) {
			return new PolylineWrapper(omeroRoi);
		}

		@Override
		public String getText(final OMEROPolyline omeroRoi) {
			return omeroRoi.getShape().getText();
		}
	}

	/** Converts {@link OMERORectangle} to {@link Roi}. */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public static class OMERORectangleToRoi extends
		AbstractOMERORealMaskToIJRoi<OMERORectangle, Roi>
	{

		@Override
		public Class<OMERORectangle> getInputType() {
			return OMERORectangle.class;
		}

		@Override
		public Class<Roi> getOutputType() {
			return Roi.class;
		}

		@Override
		public Roi wrap(final OMERORectangle omeroRoi) {
			return new BoxWrapper(omeroRoi);
		}

		@Override
		public String getText(final OMERORectangle omeroRoi) {
			return omeroRoi.getShape().getText();
		}
	}
}
