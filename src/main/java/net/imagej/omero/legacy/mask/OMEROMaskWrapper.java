
package net.imagej.omero.legacy.mask;

import ij.gui.ImageRoi;

import net.imagej.legacy.convert.roi.MaskPredicateWrapper;
import net.imagej.omero.roi.mask.OMEROMask;

import omero.gateway.model.MaskData;

/**
 * Wraps an {@link OMEROMask} as a {@link ImageRoi}.
 *
 * @author Alison Walter
 */
public class OMEROMaskWrapper extends ImageRoi implements
	MaskPredicateWrapper<OMEROMask>
{

	private final OMEROMask source;

	public OMEROMaskWrapper(final OMEROMask source) {
		super((int) Math.floor(source.getShape().getX()), (int) Math.floor(source
			.getShape().getY()), source.getShape().getMaskAsBufferedImage());
		this.source = source;
	}

	@Override
	public OMEROMask getSource() {
		return source;
	}

	@Override
	public void synchronize() {
		final MaskData md = source.getShape();

		// Only set X and Y if they changed, since ImageRoi only has integer
		// coordinates setting without checking would be destructive
		if ((long) Math.floor(md.getX()) != getXBase()) md.setX(getXBase());
		if ((long) Math.floor(md.getY()) != getYBase()) md.setY(getYBase());

		md.setMask(getProcessor().getBufferedImage());
	}

}
