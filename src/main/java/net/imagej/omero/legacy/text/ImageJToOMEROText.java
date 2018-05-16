
package net.imagej.omero.legacy.text;

import net.imagej.omero.roi.AbstractMaskPredicateToShapeData;
import net.imglib2.RealLocalizable;

import omero.gateway.model.TextData;

import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

/**
 * Converts an {@link TextRoiWrapper} to an OMERO {@link TextData}.
 *
 * @author Alison Walter
 */
@Plugin(type = Converter.class)
public class ImageJToOMEROText extends
	AbstractMaskPredicateToShapeData<RealLocalizable, TextRoiWrapper, TextData>
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
	public TextData convert(final TextRoiWrapper mask,
		final String boundaryType)
	{
		// NB: Do not set BoundaryType! That will override the text value, plus text
		// isn't a ROI so it technically doesn't have boundary behavior
		return new TextData(mask.getRoi().getText(), mask.getRoi().getXBase(), mask
			.getRoi().getYBase());
	}

}
