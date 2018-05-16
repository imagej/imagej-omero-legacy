
package net.imagej.omero.legacy.convert;

import ij.gui.OvalRoi;
import ij.gui.PointRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;

import net.imagej.legacy.convert.roi.RoiToMaskIntervalConverter;
import net.imagej.legacy.convert.roi.ShapeRoiToMaskRealIntervalConverter;
import net.imagej.legacy.convert.roi.box.RoiToBoxConverter;
import net.imagej.legacy.convert.roi.ellipsoid.OvalRoiToEllipsoidConverter;
import net.imagej.legacy.convert.roi.line.IJLineToLineConverter;
import net.imagej.legacy.convert.roi.point.PointRoiToRealPointCollectionConverter;
import net.imagej.legacy.convert.roi.polygon2d.PolygonRoiToPolygon2DConverter;
import net.imagej.legacy.convert.roi.polyline.PolylineRoiToPolylineConverter;
import net.imagej.legacy.convert.roi.polyline.PolylineRoiToRealMaskRealIntervalConverter;
import net.imagej.omero.OMEROService;
import net.imglib2.RealLocalizable;
import net.imglib2.roi.MaskInterval;
import net.imglib2.roi.MaskPredicate;
import net.imglib2.roi.RealMaskRealInterval;
import net.imglib2.roi.geom.real.Box;
import net.imglib2.roi.geom.real.Line;
import net.imglib2.roi.geom.real.Polygon2D;
import net.imglib2.roi.geom.real.Polyline;
import net.imglib2.roi.geom.real.RealPointCollection;
import net.imglib2.roi.geom.real.WritableEllipsoid;

import org.scijava.Priority;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

/**
 * {@link Converter}s for converting {@link ij.gui.Roi} to
 * {@link MaskPredicate}. These converters also ensure that the ROI mappings in
 * {@link OMEROService} are updated.
 *
 * @author Alison Walter
 */
public class IJROIToMaskPredicateWrappers {

	private IJROIToMaskPredicateWrappers() {
		// NB: Prevent instantiation of base class
	}

	/** Converts a {@link Roi} to {@link Box}. */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public static final class RoiToBoxOMEROConverter extends
		AbstractIJRoiToMaskPredicate<Roi, Box, RoiToBoxConverter>
	{

		private RoiToBoxConverter converter;

		@Override
		public RoiToBoxConverter getConverter() {
			if (converter == null) createConverter();
			return converter;
		}

		private synchronized void createConverter() {
			if (converter != null) return;
			converter = new RoiToBoxConverter();
		}

	}

	/** Converts a {@link PointRoi} to {@link RealPointCollection} */
	@Plugin(type = Converter.class, priority = Priority.EXTREMELY_HIGH)
	public static final class PointRoiToRealPointCollectionOMEROConverter extends
		AbstractIJRoiToMaskPredicate<PointRoi, RealPointCollection<RealLocalizable>, PointRoiToRealPointCollectionConverter>
	{

		private PointRoiToRealPointCollectionConverter converter;

		@Override
		public PointRoiToRealPointCollectionConverter getConverter() {
			if (converter == null) createConverter();
			return converter;
		}

		private synchronized void createConverter() {
			if (converter != null) return;
			converter = new PointRoiToRealPointCollectionConverter();
		}
	}

	/** Converts a {@link PolygonRoi} to {@link Polygon2D} */
	@Plugin(type = Converter.class, priority = Priority.EXTREMELY_HIGH)
	public static final class PolygonRoiToPolygon2DOMEROConverter extends
		AbstractIJRoiToMaskPredicate<PolygonRoi, Polygon2D, PolygonRoiToPolygon2DConverter>
	{

		private PolygonRoiToPolygon2DConverter converter;

		@Override
		public PolygonRoiToPolygon2DConverter getConverter() {
			if (converter == null) createConverter();
			return converter;
		}

		private synchronized void createConverter() {
			if (converter != null) return;
			converter = new PolygonRoiToPolygon2DConverter();
		}
	}

	/** Converts a {@link PolygonRoi} to {@link Polyline} */
	@Plugin(type = Converter.class, priority = Priority.EXTREMELY_HIGH)
	public static final class PolylineRoiToPolylineOMEROConverter extends
		AbstractIJRoiToMaskPredicate<PolygonRoi, Polyline, PolylineRoiToPolylineConverter>
	{

		private PolylineRoiToPolylineConverter converter;

		@Override
		public PolylineRoiToPolylineConverter getConverter() {
			if (converter == null) createConverter();
			return converter;
		}

		private synchronized void createConverter() {
			if (converter != null) return;
			converter = new PolylineRoiToPolylineConverter();
		}
	}

	/** Converts a {@link PolygonRoi} to {@link RealMaskRealInterval} */
	@Plugin(type = Converter.class, priority = Priority.EXTREMELY_HIGH)
	public static final class PolylineRoiToRealMaskRealIntervalOMEROConverter
		extends
		AbstractIJRoiToMaskPredicate<PolygonRoi, RealMaskRealInterval, PolylineRoiToRealMaskRealIntervalConverter>
	{

		private PolylineRoiToRealMaskRealIntervalConverter converter;

		@Override
		public PolylineRoiToRealMaskRealIntervalConverter getConverter() {
			if (converter == null) createConverter();
			return converter;
		}

		private synchronized void createConverter() {
			if (converter != null) return;
			converter = new PolylineRoiToRealMaskRealIntervalConverter();
		}
	}

	/** Converts a {@link OvalRoi} to {@link WritableEllipsoid} */
	@Plugin(type = Converter.class, priority = Priority.EXTREMELY_HIGH)
	public static final class OvalRoiToEllipsoidOMEROConverter extends
		AbstractIJRoiToMaskPredicate<OvalRoi, WritableEllipsoid, OvalRoiToEllipsoidConverter>
	{

		private OvalRoiToEllipsoidConverter converter;

		@Override
		public OvalRoiToEllipsoidConverter getConverter() {
			if (converter == null) createConverter();
			return converter;
		}

		private synchronized void createConverter() {
			if (converter != null) return;
			converter = new OvalRoiToEllipsoidConverter();
		}
	}

	/** Converts a {@link Roi} to {@link MaskInterval} */
	@Plugin(type = Converter.class, priority = Priority.HIGH)
	public static final class RoiToMaskIntervalOMEROConverter extends
		AbstractIJRoiToMaskPredicate<Roi, MaskInterval, RoiToMaskIntervalConverter>
	{

		private RoiToMaskIntervalConverter converter;

		@Override
		public RoiToMaskIntervalConverter getConverter() {
			if (converter == null) createConverter();
			return converter;
		}

		private synchronized void createConverter() {
			if (converter != null) return;
			converter = new RoiToMaskIntervalConverter();
		}
	}

	/** Converts a {@link ij.gui.Line} to {@link Line} */
	@Plugin(type = Converter.class, priority = Priority.EXTREMELY_HIGH)
	public static final class IJLineToLineOMEROConverter extends
		AbstractIJRoiToMaskPredicate<ij.gui.Line, Line, IJLineToLineConverter>
	{

		private IJLineToLineConverter converter;

		@Override
		public IJLineToLineConverter getConverter() {
			if (converter == null) createConverter();
			return converter;
		}

		private synchronized void createConverter() {
			if (converter != null) return;
			converter = new IJLineToLineConverter();
		}
	}

	/** Converts a {@link ShapeRoi} to {@link RealMaskRealInterval} */
	@Plugin(type = Converter.class, priority = Priority.EXTREMELY_HIGH)
	public static final class ShapeRoiToMaskRealIntervalOMEROConverter extends
		AbstractIJRoiToMaskPredicate<ShapeRoi, RealMaskRealInterval, ShapeRoiToMaskRealIntervalConverter>
	{

		private ShapeRoiToMaskRealIntervalConverter converter;

		@Override
		public ShapeRoiToMaskRealIntervalConverter getConverter() {
			if (converter == null) createConverter();
			return converter;
		}

		private synchronized void createConverter() {
			if (converter != null) return;
			converter = new ShapeRoiToMaskRealIntervalConverter();
		}
	}

}
