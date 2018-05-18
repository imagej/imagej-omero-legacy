
package net.imagej.omero.legacy.text;

import ij.gui.TextRoi;

import net.imagej.legacy.convert.roi.MaskPredicateWrapper;

import ome.model.enums.UnitsLength;
import omero.gateway.model.ShapeSettingsData;
import omero.model.LengthI;

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
		super(text.getX(), text.getY(), text.getText(), text.getShape()
			.getShapeSettings().getFont());
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
		text.getShape().getShapeSettings().setFontFamily(getCurrentFont()
			.getFamily());
		text.getShape().getShapeSettings().setFontSize(new LengthI(getCurrentFont()
			.getSize(), UnitsLength.POINT));
		text.getShape().getShapeSettings().setFontStyle(computeStyle());
	}

	// -- Helper methods --

	private String computeStyle() {
		if (getCurrentFont().isBold()) {
			if (getCurrentFont().isItalic())
				return ShapeSettingsData.FONT_BOLD_ITALIC;
			return ShapeSettingsData.FONT_BOLD;
		}
		else if (getCurrentFont().isItalic()) return ShapeSettingsData.FONT_ITALIC;
		return ShapeSettingsData.FONT_REGULAR;
	}
}
