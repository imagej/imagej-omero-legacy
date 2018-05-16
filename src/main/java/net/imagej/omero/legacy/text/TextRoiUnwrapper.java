
package net.imagej.omero.legacy.text;

import net.imagej.legacy.convert.roi.AbstractRoiUnwrapConverter;

import ij.gui.TextRoi;

import org.scijava.Priority;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

/**
 * Unwraps {@link TextRoiWrapper}.
 *
 * @author Alison Walter
 */
@Plugin(type = Converter.class, priority = Priority.HIGH)
public class TextRoiUnwrapper extends AbstractRoiUnwrapConverter<TextRoi> {

	@Override
	public Class<TextRoi> getOutputType() {
		return TextRoi.class;
	}

}
