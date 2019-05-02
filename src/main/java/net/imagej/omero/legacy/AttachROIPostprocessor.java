/*
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

import java.util.ArrayList;
import java.util.List;

import net.imagej.roi.ROIService;

import org.scijava.Priority;
import org.scijava.cache.CacheService;
import org.scijava.display.DisplayPostprocessor;
import org.scijava.log.LogService;
import org.scijava.module.Module;
import org.scijava.module.ModuleItem;
import org.scijava.module.process.AbstractPostprocessorPlugin;
import org.scijava.module.process.PostprocessorPlugin;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * A post-processor which checks if there are ROIs in the {@link CacheService}
 * (put there by {@link CacheROIPostprocessor}), and checks if they have
 * specific attachment directives. If a given ROI has directions for where it
 * should be attached, this attaches them and removes them from the cache.
 * <p>
 * This needs to run after the {@link DisplayPostprocessor} as the image that
 * the ROI is attached to needs to be resolved.
 * </p>
 *
 * @author Alison Walter
 */
@Plugin(type = PostprocessorPlugin.class, priority = Priority.VERY_LOW - 1)
public class AttachROIPostprocessor extends AbstractPostprocessorPlugin {

	@Parameter
	private CacheService cache;

	@Parameter
	private LogService log;

	@Parameter
	private ROIService roiService;

	private final static String ROI_KEY = "outputROIs";
	private static final String ATTACH_IMAGE = "attachToImages";

	@Override
	@SuppressWarnings("unchecked")
	public void process(final Module module) {
		if (cache.get(ROI_KEY) == null) return;
		final List<ModuleItem<?>> roiItems =
			((ThreadLocal<List<ModuleItem<?>>>) cache.get(ROI_KEY)).get();
		final List<ModuleItem<?>> resolved = new ArrayList<>();

		for (final ModuleItem<?> item : roiItems) {
			if (item.get(ATTACH_IMAGE) == null) continue;
			final List<Object> attach = getImagesToAttachTo(module, item);
			attachROIs(attach, item.getValue(module));
			resolved.add(item);
		}

		for (final ModuleItem<?> item : resolved)
			roiItems.remove(item);
		if (roiItems.isEmpty()) cache.put(ROI_KEY, null);
	}

	// -- Helper methods --

	private List<Object> getImagesToAttachTo(final Module module,
		final ModuleItem<?> item)
	{
		final String[] names = item.get(ATTACH_IMAGE).split(",[ ]*");
		final List<Object> images = new ArrayList<>();

		for (final String name : names) {
			if (module.getInput(name) != null) images.add(module.getInput(name));
			else if (module.getOutput(name) != null) images.add(module.getOutput(
				name));
			else log.error("No item named " + name + " to attach ROI to!");
		}
		return images;
	}

	private void attachROIs(final List<Object> images, final Object roi) {
		for (final Object image : images)
			roiService.add(roi, image);
	}

}
