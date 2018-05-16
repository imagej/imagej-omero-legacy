package net.imagej.omero.legacy.text;

import net.imagej.legacy.convert.roi.AbstractRoiToMaskPredicateConverter;

import ij.gui.TextRoi;

import org.scijava.Priority;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

/**
 * Wraps a {@link TextRoi} to a {@link TextRoiWrapper}.
 * <p>
 * This converter only exists for getting {@link TextRoi}s from ImageJ 1.x into
 * OMERO. It should <strong>NOT</strong> be used for other purposes.
 * </p>
 *
 * @author Alison Walter
 */
@Plugin(type = Converter.class, priority = Priority.HIGH)
public class TextRoiToTextRoiWrapper extends
AbstractRoiToMaskPredicateConverter<TextRoi, TextRoiWrapper>
{

	@Override
	public Class<TextRoi> getInputType() {
		return TextRoi.class;
	}

	@Override
	public Class<TextRoiWrapper> getOutputType() {
		return TextRoiWrapper.class;
	}

	@Override
	public TextRoiWrapper convert(TextRoi src) {
		return new TextRoiWrapper(src);
	}

	@Override
	public boolean supportedType(TextRoi src) {
		return !(src instanceof OMEROTextWrapper);
	}

}
