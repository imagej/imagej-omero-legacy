
package net.imagej.omero.legacy;

import java.util.Collections;

import net.imagej.roi.DefaultROITree;
import net.imagej.roi.ROITree;
import net.imglib2.roi.MaskPredicate;

import org.scijava.convert.AbstractConverter;
import org.scijava.convert.ConvertService;
import org.scijava.convert.Converter;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Converts a {@link ij.gui.Roi} to a {@link ROITree}.
 *
 * @author Alison Walter
 */
@Plugin(type = Converter.class)
public class IJROIToROITree extends AbstractConverter<ij.gui.Roi, ROITree> {

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

		final MaskPredicate<?> mp = convert.convert(src, MaskPredicate.class);
		final ROITree tree = new DefaultROITree();
		tree.addROIs(Collections.singletonList(mp));
		return (T) tree;
	}

	@Override
	public Class<ROITree> getOutputType() {
		return ROITree.class;
	}

	@Override
	public Class<ij.gui.Roi> getInputType() {
		return ij.gui.Roi.class;
	}

}
