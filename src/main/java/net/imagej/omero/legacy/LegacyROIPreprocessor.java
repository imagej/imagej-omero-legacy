/*
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

import java.util.ArrayList;
import java.util.List;

import ij.ImagePlus;
import ij.WindowManager;

import net.imagej.display.process.SingleInputPreprocessor;
import net.imagej.roi.DefaultROITree;
import net.imagej.roi.ROITree;
import net.imglib2.roi.MaskPredicate;

import org.scijava.Priority;
import org.scijava.convert.ConvertService;
import org.scijava.log.LogService;
import org.scijava.module.process.PreprocessorPlugin;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * If a {@link ROITree} is required, this preprocessor takes the active
 * {@link ImagePlus}'s overlay and converts it into a {@link ROITree}. If the
 * {@link ij.gui.Overlay Overlay} is {@code null} an error is thrown.
 *
 * @author Alison Walter
 */
@Plugin(type = PreprocessorPlugin.class, priority = Priority.VERY_HIGH + 1)
public class LegacyROIPreprocessor extends SingleInputPreprocessor<ROITree> {

	@Parameter
	private LogService log;

	@Parameter
	private ConvertService convert;

	public LegacyROIPreprocessor() {
		super(ROITree.class);
	}

	@Override
	public ROITree getValue() {
		final ImagePlus imp = WindowManager.getCurrentImage();
		if (imp == null) return null;

		if (imp.getOverlay() == null) throw new IllegalArgumentException(
			"Active ImagePlus has no overlay!");

		final ij.gui.Overlay overlay = imp.getOverlay();
		final List<MaskPredicate<?>> rois = new ArrayList<>(overlay.size());
		for (int i = 0; i < overlay.size(); i++)
			rois.add(convert.convert(overlay.get(i), MaskPredicate.class));
		final ROITree tree = new DefaultROITree();
		tree.addROIs(rois);
		return tree;
	}

}
