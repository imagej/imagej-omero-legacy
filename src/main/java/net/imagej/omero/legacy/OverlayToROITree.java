
package net.imagej.omero.legacy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ij.gui.Overlay;
import ij.gui.Roi;

import net.imagej.omero.roi.DefaultOMEROROICollection;
import net.imagej.omero.roi.DefaultOMEROROIElement;
import net.imagej.omero.roi.OMEROROICollection;
import net.imagej.omero.roi.OMERORealMask;
import net.imagej.roi.DefaultROITree;
import net.imagej.roi.ROITree;
import net.imglib2.roi.MaskPredicate;

import omero.gateway.model.ROIData;
import omero.model.RoiI;
import omero.model.Shape;

import org.scijava.Priority;
import org.scijava.convert.AbstractConverter;
import org.scijava.convert.ConvertService;
import org.scijava.convert.Converter;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.util.DefaultTreeNode;
import org.scijava.util.TreeNode;

/**
 * Converts an {@link Overlay} to a {@link ROITree}. If any of the ROIs are
 * OMERO ROIs, the {@link OMEROROICollection} object is re-created.
 *
 * @author Alison Walter
 */
@Plugin(type = Converter.class, priority = Priority.HIGH)
public class OverlayToROITree extends AbstractConverter<Overlay, ROITree> {

	@Parameter
	private ConvertService convert;

	@Override
	public Class<Overlay> getInputType() {
		return Overlay.class;
	}

	@Override
	public Class<ROITree> getOutputType() {
		return ROITree.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T convert(final Object src, final Class<T> dest) {
		if (!(src instanceof Overlay)) throw new IllegalArgumentException(
			"Expected " + getInputType() + " but received " + src.getClass());
		if (!dest.isAssignableFrom(getOutputType()))
			throw new IllegalArgumentException("Expected " + getOutputType() +
				" but received " + dest);

		// Do not load ROIs if they haven't been loaded yet!
		if (src instanceof LazyOverlay && !((LazyOverlay) src).areROIsLoaded())
			return (T) ((LazyOverlay) src).getSource();

		final Map<Long, List<MaskPredicate<?>>> map = convertRois(((Overlay) src)
			.toArray());

		// Handle non-omero rois
		final ROITree rp = new DefaultROITree();
		if (map.containsKey(-1l)) {
			for (final MaskPredicate<?> mp : map.get(-1l))
				rp.children().add(new DefaultTreeNode<>(mp, rp));
		}

		// Handle omero rois
		for (final Long id : map.keySet()) {
			if (id < 0) continue;
			rp.children().add(reassemble(rp, id, map.get(id)));
		}

		return (T) rp;
	}

	// -- Helper methods --

	private Map<Long, List<MaskPredicate<?>>> convertRois(final Roi[] rois) {
		final Map<Long, List<MaskPredicate<?>>> map = new HashMap<>();

		for (final Roi roi : rois) {
			final MaskPredicate<?> mp = convert.convert(roi, MaskPredicate.class);
			if (roi.getProperty(LegacyOMEROROIService.LEGACY_OMERO_ROI) != null &&
				!roi.getProperty(LegacyOMEROROIService.LEGACY_OMERO_ROI).isEmpty())
			{
				final long id = Long.parseLong(roi.getProperty(
					LegacyOMEROROIService.LEGACY_OMERO_ROI));
				addToMap(id, mp, map);
			}
			else addToMap(-1, mp, map);
		}
		return map;
	}

	private void addToMap(final long id, final MaskPredicate<?> mp,
		final Map<Long, List<MaskPredicate<?>>> map)
	{
		if (!map.containsKey(id)) map.put(id, new ArrayList<>());
		map.get(id).add(mp);
	}

	private OMEROROICollection reassemble(final ROITree parent, final long id,
		final List<MaskPredicate<?>> shapes)
	{
		omero.model.Roi omero = null;
		for (final MaskPredicate<?> mp : shapes) {
			if (mp instanceof OMERORealMask) {
				omero = ((Shape) ((OMERORealMask<?>) mp).getShape().asIObject())
					.getRoi();
				break;
			}
		}
		if (omero == null) omero = new RoiI(id, true);

		// NB: Do this before creating ROIData! Otherwise a TreeMap will be created,
		// and if the ZTC position of any of the shapes changed clearing the
		// children will cause an Exception
		omero.clearShapes();

		// Re-create OMEROROICollection
		final OMEROROICollection orc = new DefaultOMEROROICollection(parent,
			new ROIData(omero), convert);

		// Create children (based on ROIData), and remove all children (update
		// ROIData)
		final List<TreeNode<?>> children = orc.children();

		// Add children back
		for (final MaskPredicate<?> shape : shapes) {
			if (shape instanceof OMERORealMask) children.add(
				new DefaultOMEROROIElement((OMERORealMask<?>) shape, orc, null));
			else {
				children.add(new DefaultTreeNode<>(shape, orc));
			}
		}

		return orc;
	}
}
