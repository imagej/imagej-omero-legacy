
package net.imagej.omero.legacy.text;

import net.imagej.omero.roi.AbstractShapeDataToRealMaskRealInterval;
import net.imglib2.roi.BoundaryType;

import omero.gateway.model.TextData;

import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

/**
 * Converts an OMERO {@link TextData} to {@link OMEROText}.
 * <p>
 * This converter only exists for getting {@link TextData} into ImageJ 1.x. It
 * should <strong>NOT</strong> be used for other purposes!
 * </p>
 *
 * @author Alison Walter
 */
@Plugin(type = Converter.class)
public class OMEROToImageJText extends
	AbstractShapeDataToRealMaskRealInterval<TextData, OMEROText>
{

	@Override
	public Class<TextData> getInputType() {
		return TextData.class;
	}

	@Override
	public Class<OMEROText> getOutputType() {
		return OMEROText.class;
	}

	@Override
	public OMEROText convert(final TextData shape, final BoundaryType bt) {
		return new OMEROText(shape);
	}

	@Override
	public String getTextValue(final TextData shape) {
		return shape.getText();
	}

}
