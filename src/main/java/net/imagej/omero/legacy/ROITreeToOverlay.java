
package net.imagej.omero.legacy;

import java.lang.reflect.Type;
import java.util.List;

import ij.gui.Overlay;
import ij.gui.Roi;

import net.imagej.omero.roi.LazyROITree;
import net.imagej.omero.roi.OMEROROICollection;
import net.imagej.roi.ROIService;
import net.imagej.roi.ROITree;
import net.imglib2.roi.MaskPredicate;

import org.scijava.Priority;
import org.scijava.convert.AbstractConverter;
import org.scijava.convert.ConvertService;
import org.scijava.convert.Converter;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.util.TreeNode;

/**
 * Converts a {@link ROITree} containing ROIs to an {@link Overlay} containing
 * equivalent ROIs.
 *
 * @author Alison Walter
 */
@Plugin(type = Converter.class, priority = Priority.HIGH)
public class ROITreeToOverlay extends AbstractConverter<ROITree, Overlay> {

	@Parameter
	private ConvertService convert;

	@Parameter
	private ROIService roi;

	@Override
	public boolean canConvert(final Object src, final Type dest) {
		return super.canConvert(src, dest) && roi.hasROIs(src);
	}

	@Override
	public boolean canConvert(final Object src, final Class<?> dest) {
		return super.canConvert(src, dest) && roi.hasROIs(src);
	}

	@Override
	public Class<ROITree> getInputType() {
		return ROITree.class;
	}

	@Override
	public Class<Overlay> getOutputType() {
		return Overlay.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T convert(final Object src, final Class<T> dest) {
		if (!(src instanceof ROITree)) throw new IllegalArgumentException(
			"Expected " + getInputType() + " but received " + src.getClass());
		if (!dest.isAssignableFrom(getOutputType()))
			throw new IllegalArgumentException("Expected " + getOutputType() +
				" but received " + dest);

		// Do not load the ROIs if they haven't been loaded yet!
		if (src instanceof LazyROITree && !((LazyROITree) src).areROIsLoaded())
			return (T) new LazyOverlay((LazyROITree) src, convert);

		final Overlay overlay = new Overlay();
		addROIs(overlay, (ROITree) src);
		return (T) overlay;
	}

	// -- Helper methods --

	private void addROIs(final Overlay overlay, final TreeNode<?> dn) {
		if (dn instanceof OMEROROICollection) {
			addOMEROROICollection(overlay, (OMEROROICollection) dn);
			return;
		}
		if (dn.data() instanceof MaskPredicate) overlay.add(convert.convert(dn
			.data(), Roi.class));
		final List<TreeNode<?>> children = dn.children();
		if (children == null || children.isEmpty()) return;
		for (final TreeNode<?> child : children)
			addROIs(overlay, child);
	}

	private void addOMEROROICollection(final Overlay overlay,
		final OMEROROICollection orc)
	{
		final long id = orc.data().getId();
		final List<TreeNode<?>> children = orc.children();
		for (final TreeNode<?> child : children) {
			final Roi ijRoi = convert.convert(child.data(), Roi.class);
			if (ijRoi == null) throw new IllegalArgumentException("Cannot convert " +
				child.data().getClass() + " to ij.gui.Roi");
			ijRoi.setProperty(LegacyOMEROROIService.LEGACY_OMERO_ROI, Long.toString(
				id));
			overlay.add(ijRoi);
		}
	}
}
