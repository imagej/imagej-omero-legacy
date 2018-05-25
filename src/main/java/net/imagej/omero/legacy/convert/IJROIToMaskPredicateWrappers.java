
package net.imagej.omero.legacy.convert;

import ij.gui.OvalRoi;
import ij.gui.PointRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.gui.TextRoi;

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
import net.imagej.omero.legacy.text.TextRoiToTextRoiWrapper;
import net.imagej.omero.legacy.text.TextRoiWrapper;
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
import org.scijava.convert.ConvertService;
import org.scijava.convert.Converter;
import org.scijava.plugin.Parameter;
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

		@Parameter
		private ConvertService convertService;

		@Override
		public RoiToBoxConverter getConverter() {
			return convertService.getInstance(RoiToBoxConverter.class);
		}

	}

	/** Converts a {@link PointRoi} to {@link RealPointCollection} */
	@Plugin(type = Converter.class, priority = Priority.EXTREMELY_HIGH)
	public static final class PointRoiToRealPointCollectionOMEROConverter extends
		AbstractIJRoiToMaskPredicate<PointRoi, RealPointCollection<RealLocalizable>, PointRoiToRealPointCollectionConverter>
	{

		@Parameter
		private ConvertService convertService;

		@Override
		public PointRoiToRealPointCollectionConverter getConverter() {
			return convertService.getInstance(
				PointRoiToRealPointCollectionConverter.class);
		}

	}

	/** Converts a {@link PolygonRoi} to {@link Polygon2D} */
	@Plugin(type = Converter.class, priority = Priority.EXTREMELY_HIGH)
	public static final class PolygonRoiToPolygon2DOMEROConverter extends
		AbstractIJRoiToMaskPredicate<PolygonRoi, Polygon2D, PolygonRoiToPolygon2DConverter>
	{

		@Parameter
		private ConvertService convertService;

		@Override
		public PolygonRoiToPolygon2DConverter getConverter() {
			return convertService.getInstance(PolygonRoiToPolygon2DConverter.class);
		}

	}

	/** Converts a {@link PolygonRoi} to {@link Polyline} */
	@Plugin(type = Converter.class, priority = Priority.EXTREMELY_HIGH)
	public static final class PolylineRoiToPolylineOMEROConverter extends
		AbstractIJRoiToMaskPredicate<PolygonRoi, Polyline, PolylineRoiToPolylineConverter>
	{

		@Parameter
		private ConvertService convertService;

		@Override
		public PolylineRoiToPolylineConverter getConverter() {
			return convertService.getInstance(PolylineRoiToPolylineConverter.class);
		}

	}

	/** Converts a {@link PolygonRoi} to {@link RealMaskRealInterval} */
	@Plugin(type = Converter.class, priority = Priority.EXTREMELY_HIGH)
	public static final class PolylineRoiToRealMaskRealIntervalOMEROConverter
		extends
		AbstractIJRoiToMaskPredicate<PolygonRoi, RealMaskRealInterval, PolylineRoiToRealMaskRealIntervalConverter>
	{

		@Parameter
		private ConvertService convertService;

		@Override
		public PolylineRoiToRealMaskRealIntervalConverter getConverter() {
			return convertService.getInstance(
				PolylineRoiToRealMaskRealIntervalConverter.class);
		}

	}

	/** Converts a {@link OvalRoi} to {@link WritableEllipsoid} */
	@Plugin(type = Converter.class, priority = Priority.EXTREMELY_HIGH)
	public static final class OvalRoiToEllipsoidOMEROConverter extends
		AbstractIJRoiToMaskPredicate<OvalRoi, WritableEllipsoid, OvalRoiToEllipsoidConverter>
	{

		@Parameter
		private ConvertService convertService;

		@Override
		public OvalRoiToEllipsoidConverter getConverter() {
			return convertService.getInstance(OvalRoiToEllipsoidConverter.class);
		}

	}

	/** Converts a {@link Roi} to {@link MaskInterval} */
	@Plugin(type = Converter.class, priority = Priority.HIGH)
	public static final class RoiToMaskIntervalOMEROConverter extends
		AbstractIJRoiToMaskPredicate<Roi, MaskInterval, RoiToMaskIntervalConverter>
	{

		@Parameter
		private ConvertService convertService;

		@Override
		public RoiToMaskIntervalConverter getConverter() {
			return convertService.getInstance(RoiToMaskIntervalConverter.class);
		}

	}

	/** Converts a {@link ij.gui.Line} to {@link Line} */
	@Plugin(type = Converter.class, priority = Priority.EXTREMELY_HIGH)
	public static final class IJLineToLineOMEROConverter extends
		AbstractIJRoiToMaskPredicate<ij.gui.Line, Line, IJLineToLineConverter>
	{

		@Parameter
		private ConvertService convertService;

		@Override
		public IJLineToLineConverter getConverter() {
			return convertService.getInstance(IJLineToLineConverter.class);
		}

	}

	/** Converts a {@link ShapeRoi} to {@link RealMaskRealInterval} */
	@Plugin(type = Converter.class, priority = Priority.EXTREMELY_HIGH)
	public static final class ShapeRoiToMaskRealIntervalOMEROConverter extends
		AbstractIJRoiToMaskPredicate<ShapeRoi, RealMaskRealInterval, ShapeRoiToMaskRealIntervalConverter>
	{

		@Parameter
		private ConvertService convertService;

		@Override
		public ShapeRoiToMaskRealIntervalConverter getConverter() {
			return convertService.getInstance(
				ShapeRoiToMaskRealIntervalConverter.class);
		}

	}

	/** Converts a {@link TextRoi} to {@link TextRoiWrapper} */
	@Plugin(type = Converter.class, priority = Priority.EXTREMELY_HIGH)
	public static final class TextRoiToTextRoiWrapperOMEROConverter extends
		AbstractIJRoiToMaskPredicate<TextRoi, TextRoiWrapper, TextRoiToTextRoiWrapper>
	{

		@Parameter
		private ConvertService convertService;

		@Override
		public TextRoiToTextRoiWrapper getConverter() {
			return convertService.getInstance(TextRoiToTextRoiWrapper.class);
		}

	}

}