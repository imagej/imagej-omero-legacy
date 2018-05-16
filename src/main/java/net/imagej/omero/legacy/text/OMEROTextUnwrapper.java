
package net.imagej.omero.legacy.text;

import net.imagej.omero.roi.AbstractOMERORealMaskUnwrapper;

import omero.gateway.model.TextData;

import org.scijava.Priority;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

/**
 * Unwraps an {@link OMEROText}.
 *
 * @author Alison Walter
 */
@Plugin(type = Converter.class, priority = Priority.HIGH)
public class OMEROTextUnwrapper extends
	AbstractOMERORealMaskUnwrapper<TextData, OMEROText>
{

	@Override
	public Class<TextData> getOutputType() {
		return TextData.class;
	}

	@Override
	public Class<OMEROText> getInputType() {
		return OMEROText.class;
	}

	@Override
	public void setBoundaryType(final TextData shape, final String textValue) {
		// Do nothing
	}

	@Override
	public String getTextValue(final TextData shape) {
		return shape.getText();
	}

}
