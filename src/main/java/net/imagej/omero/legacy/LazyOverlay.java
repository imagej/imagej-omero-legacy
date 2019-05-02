/*-
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2018 - 2019 Open Microscopy Environment:
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

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.measure.ResultsTable;

import net.imagej.omero.roi.LazyROITree;
import net.imglib2.roi.MaskPredicate;

import org.scijava.convert.ConvertService;
import org.scijava.util.TreeNode;

/**
 * An {@link ij.gui.Overlay} which is backed by a {@link LazyROITree}. This
 * causes the ROIs to be loaded from the OMERO server only when requested.
 *
 * @author Alison Walter
 */
public class LazyOverlay extends ij.gui.Overlay {

	private final LazyROITree source;
	private final ConvertService convert;
	private boolean roisLoaded;

	public LazyOverlay(final LazyROITree source, final ConvertService convert) {
		this.source = source;
		this.convert = convert;
		roisLoaded = source.areROIsLoaded();
		if (roisLoaded) loadROIs();
	}

	/**
	 * Check if the ROIs associated with this Overlay have been downloaded from
	 * the OMERO server.
	 *
	 * @return true if the ROIs have been downloaded, false otherwise.
	 */
	public boolean areROIsLoaded() {
		return roisLoaded;
	}

	/**
	 * If the ROIs have not already been downloaded, this downloads the ROIs from
	 * the OMERO server.
	 */
	public void loadROIs() {
		if (roisLoaded) return;
		loadROIsAndAddToOverlay();
	}

	/**
	 * Returns the source {@link LazyROITree}.
	 *
	 * @return {@link LazyROITree} associated with this Overlay
	 */
	public LazyROITree getSource() {
		return source;
	}

	@Override
	public void add(final Roi roi) {
		if (!roisLoaded) loadROIs();
		super.add(roi);
	}

	@Override
	public void add(final Roi roi, final String name) {
		if (!roisLoaded) loadROIs();
		super.add(roi, name);
	}

	@Override
	public void addElement(final Roi roi) {
		if (!roisLoaded) loadROIs();
		super.addElement(roi);
	}

	@Override
	public void remove(final int index) {
		if (!roisLoaded) loadROIs();
		super.remove(index);
	}

	@Override
	public void remove(final Roi roi) {
		if (!roisLoaded) loadROIs();
		super.remove(roi);
	}

	@Override
	public void remove(final String name) {
		if (!roisLoaded) loadROIs();
		super.remove(name);
	}

	@Override
	public void clear() {
		if (!roisLoaded) loadROIs();
		super.clear();
	}

	@Override
	public Roi get(final int index) {
		if (!roisLoaded) loadROIs();
		return super.get(index);
	}

	@Override
	public int getIndex(final String name) {
		if (!roisLoaded) loadROIs();
		return super.getIndex(name);
	}

	@Override
	public boolean contains(final Roi roi) {
		if (!roisLoaded) loadROIs();
		return super.contains(roi);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This returns -1 if the ROIs have not been downloaded from the OMERO server
	 * yet.
	 * </p>
	 */
	@Override
	public int size() {
		// Only state the number of ROIs when they've been loaded
		// NB: Prevent ROIs from being loading during Dataset/ImagePlus conversions
		if (!roisLoaded) return -1;
		return super.size();
	}

	@Override
	public Roi[] toArray() {
		if (!roisLoaded) loadROIs();
		return super.toArray();
	}

	@Override
	public void setStrokeColor(final Color color) {
		if (!roisLoaded) loadROIs();
		super.setStrokeColor(color);
	}

	@Override
	public void setFillColor(final Color color) {
		if (!roisLoaded) loadROIs();
		super.setFillColor(color);
	}

	@Override
	public void translate(final int dx, final int dy) {
		if (!roisLoaded) loadROIs();
		super.translate(dx, dy);
	}

	@Override
	public void translate(final double dx, final double dy) {
		if (!roisLoaded) loadROIs();
		super.translate(dx, dy);
	}

	@Override
	public ResultsTable measure(final ImagePlus imp) {
		if (!roisLoaded) loadROIs();
		return super.measure(imp);
	}

	@Override
	public Overlay crop(final Rectangle bounds) {
		if (!roisLoaded) loadROIs();
		return super.crop(bounds);
	}

	@Override
	public void crop(final int firstSlice, final int lastSlice) {
		if (!roisLoaded) loadROIs();
		super.crop(firstSlice, lastSlice);
	}

	@Override
	public void crop(final int firstC, final int lastC, final int firstZ,
		final int lastZ, final int firstT, final int lastT)
	{
		if (!roisLoaded) loadROIs();
		super.crop(firstC, lastC, firstZ, lastZ, firstT, lastT);
	}

	@Override
	public Overlay duplicate() {
		if (!roisLoaded) loadROIs();
		return super.duplicate();
	}

	@Override
	public String toString() {
		if (!roisLoaded) loadROIs();
		return super.toString();
	}

	@Override
	public void drawNames(final boolean b) {
		// NB: If the ROIs aren't loaded there's nothing to draw names on
		if (!roisLoaded) return;
		super.drawNames(b);
	}

	// -- Helper methods --

	private synchronized void loadROIsAndAddToOverlay() {
		if (roisLoaded) return;
		final List<TreeNode<?>> children = source.children();
		for (final TreeNode<?> child : children)
			addROIs(child);
		roisLoaded = true;
	}

	private void addROIs(final TreeNode<?> dn) {
		if (dn.data() instanceof MaskPredicate) {
			final Roi ijRoi = convert.convert(dn.data(), Roi.class);
			if (ijRoi == null) throw new IllegalArgumentException("Cannot convert " +
				dn.data() + " to ij.gui.Roi");
			super.add(ijRoi);
		}
		if (dn.children() == null || dn.children().isEmpty()) return;
		for (final TreeNode<?> child : dn.children())
			addROIs(child);
	}

}
