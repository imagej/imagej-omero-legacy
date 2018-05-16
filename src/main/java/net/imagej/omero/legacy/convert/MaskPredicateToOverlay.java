
package net.imagej.omero.legacy.convert;

import ij.gui.Overlay;

import net.imglib2.roi.MaskPredicate;

import org.scijava.convert.AbstractConverter;
import org.scijava.convert.ConvertService;
import org.scijava.convert.Converter;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Converts a {@link MaskPredicate} to a {@link Overlay}.
 *
 * @author Alison Walter
 */
@Plugin(type = Converter.class)
public class MaskPredicateToOverlay extends
	AbstractConverter<MaskPredicate<?>, Overlay>
{

	@Parameter
	private ConvertService convert;

	@Override
	@SuppressWarnings("unchecked")
	public <T> T convert(final Object src, final Class<T> dest) {
		if (!getInputType().isInstance(src)) throw new IllegalArgumentException(
			"Expected " + getInputType() + " but received " + src.getClass());
		if (!dest.isAssignableFrom(getOutputType()))
			throw new IllegalArgumentException("Expected " + getOutputType() +
				" but received " + dest);

		final ij.gui.Roi ijRoi = convert.convert(src, ij.gui.Roi.class);
		final Overlay o = new Overlay();
		o.add(ijRoi);
		return (T) o;
	}

	@Override
	public Class<Overlay> getOutputType() {
		return Overlay.class;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class<MaskPredicate<?>> getInputType() {
		return (Class) MaskPredicate.class;
	}

}
