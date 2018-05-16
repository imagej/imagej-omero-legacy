
package net.imagej.omero.legacy.text;

import java.util.function.Predicate;

import ij.gui.TextRoi;

import net.imagej.legacy.convert.roi.IJRealRoiWrapper;
import net.imglib2.RealLocalizable;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.RealTransform;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.KnownConstant;
import net.imglib2.roi.RealMask;
import net.imglib2.roi.RealMaskRealInterval;

/**
 * An {@link IJRealRoiWrapper} which wraps a {@link TextRoi} as a
 * {@link RealMaskRealInterval}.
 * <p>
 * Technically text is not considered a ROI, as such
 * {@link UnsupportedOperationException} is thrown for the majority of these
 * methods. This wrapper is only here to provide a path for getting text from
 * ImageJ 1.x and into OMERO. It should <strong>NOT</strong> be used for other
 * purposes.
 * </p>
 *
 * @author Alison Walter
 */
public class TextRoiWrapper implements IJRealRoiWrapper<TextRoi> {

	private final TextRoi roi;

	public TextRoiWrapper(final TextRoi roi) {
		this.roi = roi;
	}

	@Override
	public TextRoi getRoi() {
		return roi;
	}

	// -- RealMaskRealInterval methods --

	/**
	 * {@inheritDoc}
	 * <p>
	 * Always throws an {@link UnsupportedOperationException}. Text is not
	 * technically a ROI.
	 * </p>
	 */
	@Override
	public RealMaskRealInterval and(
		final Predicate<? super RealLocalizable> other)
	{
		throw new UnsupportedOperationException("and");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Always throws an {@link UnsupportedOperationException}. Text is not
	 * technically a ROI.
	 * </p>
	 */
	@Override
	public RealMaskRealInterval or(final RealMaskRealInterval other) {
		throw new UnsupportedOperationException("or");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Always throws an {@link UnsupportedOperationException}. Text is not
	 * technically a ROI.
	 * </p>
	 */
	@Override
	public RealMaskRealInterval negate() {
		throw new UnsupportedOperationException("negate");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Always throws an {@link UnsupportedOperationException}. Text is not
	 * technically a ROI.
	 * </p>
	 */
	@Override
	public RealMaskRealInterval minus(
		final Predicate<? super RealLocalizable> other)
	{
		throw new UnsupportedOperationException("minus");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Always throws an {@link UnsupportedOperationException}. Text is not
	 * technically a ROI.
	 * </p>
	 */
	@Override
	public RealMaskRealInterval xor(final RealMaskRealInterval other) {
		throw new UnsupportedOperationException("xor");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Always throws an {@link UnsupportedOperationException}. Text is not
	 * technically a ROI.
	 * </p>
	 */
	@Override
	public RealMaskRealInterval transform(final AffineGet transformToSource) {
		throw new UnsupportedOperationException("transform");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Always throws an {@link UnsupportedOperationException}. Text is not
	 * technically a ROI.
	 * </p>
	 */
	@Override
	public double realMin(final int d) {
		throw new UnsupportedOperationException("realMin");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Always throws an {@link UnsupportedOperationException}. Text is not
	 * technically a ROI.
	 * </p>
	 */
	@Override
	public double realMax(final int d) {
		throw new UnsupportedOperationException("realMax");
	}

	// -- RealMask methods --

	/**
	 * {@inheritDoc}
	 * <p>
	 * Always throws an {@link UnsupportedOperationException}. Text is not
	 * technically a ROI.
	 * </p>
	 */
	@Override
	public RealMask or(final Predicate<? super RealLocalizable> other) {
		throw new UnsupportedOperationException("or");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Always throws an {@link UnsupportedOperationException}. Text is not
	 * technically a ROI.
	 * </p>
	 */
	@Override
	public RealMask xor(final Predicate<? super RealLocalizable> other) {
		throw new UnsupportedOperationException("xor");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Always throws an {@link UnsupportedOperationException}. Text is not
	 * technically a ROI.
	 * </p>
	 */
	@Override
	public RealMask transform(final RealTransform transformToSource) {
		throw new UnsupportedOperationException("transform");
	}

	// -- MaskPredicate methods --

	/**
	 * {@inheritDoc}
	 * <p>
	 * This does not throw an {@link UnsupportedOperationException} as it is
	 * needed for the conversions.
	 * </p>
	 */
	@Override
	public BoundaryType boundaryType() {
		return BoundaryType.UNSPECIFIED;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Always throws an {@link UnsupportedOperationException}. Text is not
	 * technically a ROI.
	 * </p>
	 */
	@Override
	public KnownConstant knownConstant() {
		throw new UnsupportedOperationException("knownConstant");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Always throws an {@link UnsupportedOperationException}. Text is not
	 * technically a ROI.
	 * </p>
	 */
	@Override
	public boolean test(final RealLocalizable t) {
		throw new UnsupportedOperationException("test");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This does not throw an {@link UnsupportedOperationException} as it is
	 * needed for the conversions.
	 * </p>
	 */
	@Override
	public int numDimensions() {
		return 2;
	}

}
