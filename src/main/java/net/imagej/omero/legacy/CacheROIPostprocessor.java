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

import ij.gui.Overlay;

import net.imagej.roi.ROITree;

import org.scijava.Priority;
import org.scijava.cache.CacheService;
import org.scijava.display.DisplayPostprocessor;
import org.scijava.module.Module;
import org.scijava.module.ModuleItem;
import org.scijava.module.process.AbstractPostprocessorPlugin;
import org.scijava.module.process.PostprocessorPlugin;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * A Postprocessor which checks if there are ROIs as outputs. If so, it stashes
 * them in the {@link CacheService} and resolves them.
 * <p>
 * This prevents the {@link DisplayPostprocessor} from trying to display these
 * ROIs.
 * </p>
 *
 * @author Alison Walter
 */
@Plugin(type = PostprocessorPlugin.class, priority = Priority.VERY_LOW + 1)
public class CacheROIPostprocessor extends AbstractPostprocessorPlugin {

	@Parameter
	private CacheService cache;

	@Override
	public void process(final Module module) {
		final List<ModuleItem<?>> rois = new ArrayList<>();

		for (final ModuleItem<?> outputItem : module.getInfo().outputs()) {
			if (module.isOutputResolved(outputItem.getName())) continue;

			final Object o = outputItem.getValue(module);
			if (o instanceof Overlay || o instanceof ROITree) {
				rois.add(outputItem);
				module.resolveOutput(outputItem.getName());
			}
		}

		if (cache.get(ROIConstants.OUTPUT_CACHE_KEY) != null) throw new IllegalArgumentException(
			"Unexpected cached ROIs!");
		if (!rois.isEmpty()) {
			cache.put(ROIConstants.OUTPUT_CACHE_KEY, ThreadLocal.withInitial(() -> rois));
		}
	}

}
