/*
 * Copyright (C) 2023-2023 Cyril Adrian <cyril.adrian@gmail.com>
 *
 * This file is part of Lightner.
 *
 * Lightner is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * Lightner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Lightner.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.cadrian.lightner.model.content.video;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.cadrian.lightner.dao.LightnerDataCard;
import net.cadrian.lightner.model.LightnerModelException;
import net.cadrian.lightner.model.content.AbstractLightnerCardContent;

public class ContentVideo extends AbstractLightnerCardContent {

	private static final Set<String> SUFFIXES = Collections
			.unmodifiableSet(new HashSet<>(Arrays.asList(".avi", ".mp4", ".mov")));

	public ContentVideo(final LightnerDataCard data, final String title) throws LightnerModelException {
		super(data, title);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void accept(final Visitor v) {
		v.visitVideo(this);
	}

	@Override
	public Collection<String> getFileSuffixes() {
		return SUFFIXES;
	}

}
