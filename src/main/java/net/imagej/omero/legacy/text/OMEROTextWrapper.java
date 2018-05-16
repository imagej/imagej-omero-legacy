
package net.imagej.omero.legacy.text;

import ij.gui.TextRoi;

import net.imagej.legacy.convert.roi.MaskPredicateWrapper;

/**
 * Wraps an {@link OMEROText} as an ImageJ 1.x ROI.
 *
 * @author Alison Walter
 */
public class OMEROTextWrapper extends TextRoi implements
	MaskPredicateWrapper<OMEROText>
{

	private final OMEROText text;

	public OMEROTextWrapper(final OMEROText text) {
		super(text.getX(), text.getY(), text.getText());
		this.text = text;
	}

	@Override
	public OMEROText getSource() {
		return text;
	}

	@Override
	public void synchronize() {
		text.setText(getText());
		text.setX(getXBase());
		text.setY(getYBase());
	}
}
