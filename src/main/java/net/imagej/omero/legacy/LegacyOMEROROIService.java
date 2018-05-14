
package net.imagej.omero.legacy;

import java.util.List;

import ij.ImagePlus;
import ij.gui.Overlay;

import net.imagej.Dataset;
import net.imagej.ImgPlus;
import net.imagej.roi.DefaultROIService;
import net.imagej.roi.ROIService;
import net.imagej.roi.ROITree;
import net.imglib2.roi.MaskPredicate;

import org.scijava.Priority;
import org.scijava.convert.ConvertService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.Service;
import org.scijava.util.TreeNode;

/**
 * A {@link ROIService} for working with ROIs in OMERO and imagej-legacy.
 *
 * @author Alison Walter
 */
@Plugin(type = Service.class, priority = Priority.HIGH)
public class LegacyOMEROROIService extends DefaultROIService {

	@Parameter
	private ConvertService convert;

	private long legacyRoiId;

	public static final String LEGACY_OMERO_ROI =
		"net.imagej.omero.legacy.ROIDataID";

	public static final String OMERO_ROI_Z = "net.imagej.omero.legacy:ZPos";

	public static final String OMERO_ROI_T = "net.imagej.omero.legacy:TPos";

	public static final String OMERO_ROI_C = "net.imagej.omero.legacy:CPos";

	@Override
	public void add(final Object roi, final Object img) {
		if (img instanceof Dataset) {
			final ROITree rp = convert.convert(roi, ROITree.class);
			addRois(((Dataset) img).getImgPlus(), rp);
		}
		else if (img instanceof ImgPlus) {
			final ROITree rp = convert.convert(roi, ROITree.class);
			addRois((ImgPlus<?>) img, rp);
		}
		else if (img instanceof ImagePlus) {
			final Overlay o = convert.convert(roi, Overlay.class);
			addRois((ImagePlus) img, o);
		}
		else throw new IllegalArgumentException("Cannot add " + roi.getClass() +
			" to " + img.getClass());
	}

	@Override
	public boolean hasROIs(final Object o) {
		// prevent LazyROITree from loading rois, since it is also a TreeNode and
		// requesting children would cause potentially unloaded ROIs to be loaded
		if (o instanceof ROITree) return true;
		if (o instanceof TreeNode) return hasROIs((TreeNode<?>) o);
		return o instanceof Overlay || o instanceof MaskPredicate ||
			o instanceof ij.gui.Roi;
	}

	/** Return a unique ID for identify {@link ij.gui.Roi}s. */
	public long getLegacyRoiId() {
		return legacyRoiId++;
	}

	// -- Helper methods --

	private void addRois(final ImgPlus<?> img, final ROITree rp) {
		if (img.getProperties().get("rois") != null) {
			final ROITree currentROIs = (ROITree) img.getProperties().get("rois");
			final List<TreeNode<?>> currentChildren = currentROIs.children();
			for (final TreeNode<?> child : rp.children()) {
				child.setParent(currentROIs);
				currentChildren.add(child);
			}
		}
		else img.getProperties().put("rois", rp);
	}

	private void addRois(final ImagePlus i, final Overlay rois) {
		if (i.getOverlay() != null) {
			// HACK: if rois is a LazyOverlay, we need to force the ROIs to be loaded
			final ij.gui.Roi[] newROIs = rois.toArray();
			final Overlay currentROIs = i.getOverlay();
			for (int r = 0; r < newROIs.length; r++)
				currentROIs.add(newROIs[r]);
		}
		else i.setOverlay(rois);
	}

	private boolean hasROIs(final TreeNode<?> dn) {
		if (dn instanceof ROITree) return true;
		if (dn.data() instanceof MaskPredicate) return true;

		final List<TreeNode<?>> children = dn.children();
		if (children == null || children.isEmpty()) return false;

		for (final TreeNode<?> child : children) {
			final boolean foundRoi = hasROIs(child);
			if (foundRoi) return true;
		}
		return false;
	}
}
