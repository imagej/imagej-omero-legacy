
package net.imagej.omero.legacy;

import ij.gui.Overlay;

import org.scijava.convert.AbstractConverter;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

/**
 * Converts a {@link ij.gui.Roi} to a {@link Overlay}.
 *
 * @author Alison Walter
 */
@Plugin(type = Converter.class)
public class IJROIToOverlay extends AbstractConverter<ij.gui.Roi, Overlay> {

	@Override
	@SuppressWarnings("unchecked")
	public <T> T convert(final Object src, final Class<T> dest) {
		if (!getInputType().isInstance(src)) throw new IllegalArgumentException(
			"Expected " + getInputType() + " but received " + src.getClass());
		if (!dest.isAssignableFrom(getOutputType()))
			throw new IllegalArgumentException("Expected " + getOutputType() +
				" but received " + dest);

		final Overlay o = new Overlay();
		o.add((ij.gui.Roi) src);
		return (T) o;
	}

	@Override
	public Class<Overlay> getOutputType() {
		return Overlay.class;
	}

	@Override
	public Class<ij.gui.Roi> getInputType() {
		return ij.gui.Roi.class;
	}

}
