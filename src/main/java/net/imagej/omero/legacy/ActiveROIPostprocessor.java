/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2018 - 2022 Open Microscopy Environment:
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

import ij.ImagePlus;
import ij.WindowManager;

import net.imagej.roi.ROIService;

import org.scijava.Priority;
import org.scijava.cache.CacheService;
import org.scijava.module.Module;
import org.scijava.module.ModuleItem;
import org.scijava.module.process.AbstractPostprocessorPlugin;
import org.scijava.module.process.PostprocessorPlugin;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Checks if there are any remaining ROIs in the {@link CacheService} (put there
 * by {@link CacheROIPostprocessor}) and if so attaches them to the active
 * {@link ImagePlus}.
 * <p>
 * This is the default behavior if no attachment location was specified, see
 * {@link AttachROIPostprocessor} for details.
 * </p>
 *
 * @author Alison Walter
 */
@Plugin(type = PostprocessorPlugin.class, priority = Priority.EXTREMELY_LOW)
public class ActiveROIPostprocessor extends AbstractPostprocessorPlugin {

	@Parameter
	private CacheService cache;

	@Parameter
	private ROIService roiService;

	@Override
	@SuppressWarnings("unchecked")
	public void process(final Module module) {
		if (cache.get(ROIConstants.OUTPUT_CACHE_KEY) == null) return;
		final Object retrieve = cache.get(ROIConstants.OUTPUT_CACHE_KEY);
		cache.put(ROIConstants.OUTPUT_CACHE_KEY, null);
		final List<ModuleItem<?>> rois =
			((ThreadLocal<List<ModuleItem<?>>>) retrieve).get();

		final ImagePlus ip = WindowManager.getCurrentImage();
		if (ip == null) throw new IllegalArgumentException("No active ImagePlus!");

		for (final ModuleItem<?> roi : rois)
			roiService.add(roi.getValue(module), ip);

		ip.setHideOverlay(false);
	}

}
