/*-
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2018 - 2023 Open Microscopy Environment:
 * 	- Board of Regents of the University of Wisconsin-Madison
 * 	- Glencoe Software, Inc.
 * 	- University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imagej.omero.legacy;

import java.util.List;

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

import ij.ImagePlus;
import ij.gui.Overlay;

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
			addROIs(((Dataset) img).getImgPlus(), rp);
		}
		else if (img instanceof ImgPlus) {
			final ROITree rp = convert.convert(roi, ROITree.class);
			addROIs((ImgPlus<?>) img, rp);
		}
		else if (img instanceof ImagePlus) {
			final Overlay o = convert.convert(roi, Overlay.class);
			addROIs(img, o);
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

	private void addROIs(final ImgPlus<?> img, final ROITree rp) {
		if (img.getProperties().get(ROIService.ROI_PROPERTY) != null) {
			final ROITree currentROIs = (ROITree) img.getProperties().get(ROIService.ROI_PROPERTY);
			if (!currentROIs.equals(rp)) {
				final List<TreeNode<?>> currentChildren = currentROIs.children();
				for (final TreeNode<?> child : rp.children()) {
					child.setParent(currentROIs);
					currentChildren.add(child);
				}
			}
		}
		else img.getProperties().put(ROIService.ROI_PROPERTY, rp);
	}

	// NB: We cannot type this method on ij.* classes.
	// Otherwise, the ij1-patcher may fail to patch ImageJ1.
	private void addROIs(final Object image, final Object rois) {
		if (!(image instanceof ImagePlus)) {
			throw new IllegalStateException("Non-ImagePlus image: " + image.getClass()
				.getName());
		}
		if (!(rois instanceof Overlay)) {
			throw new IllegalStateException("Non-Overlay rois: " + rois.getClass()
				.getName());
		}
		final ImagePlus imp = (ImagePlus) image;
		final Overlay overlay = (Overlay) rois;

		if (imp.getOverlay() != null) {
			// HACK: if rois is a LazyOverlay, we need to force the ROIs to be loaded
			final ij.gui.Roi[] newROIs = overlay.toArray();
			final Overlay currentROIs = imp.getOverlay();
			for (int r = 0; r < newROIs.length; r++)
				currentROIs.add(newROIs[r]);
		}
		else imp.setOverlay(overlay);
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
