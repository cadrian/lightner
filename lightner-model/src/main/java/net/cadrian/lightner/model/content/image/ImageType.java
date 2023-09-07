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
package net.cadrian.lightner.model.content.image;

import java.util.HashMap;
import java.util.Map;

public enum ImageType {
	PNG("png"), GIF("gif"), JPG("jpg", "jpeg");

	private static final Map<String, ImageType> MAP = new HashMap<>();
	private String[] suffixes;

	ImageType(final String... suffixes) {
		this.suffixes = suffixes;
	}

	public String getSuffix() {
		return suffixes[0];
	}

	static {
		for (final ImageType t : values()) {
			for (final String s : t.suffixes) {
				MAP.put(s, t);
			}
		}
	}

	public static ImageType get(final String filename) {
		final int i = filename.lastIndexOf('.');
		final String suffix = i < 0 ? filename : filename.substring(i + 1);
		return MAP.get(suffix.toLowerCase());
	}
}
