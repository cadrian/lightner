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
package net.cadrian.lightner.model.content;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.cadrian.lightner.dao.LightnerDataCard;
import net.cadrian.lightner.dao.LightnerDataContent;
import net.cadrian.lightner.model.LightnerCardContent;

/**
 * Common file manipulation methods
 *
 * @param <T>
 */
public abstract class AbstractLightnerCardContent implements LightnerCardContent {

	private static final byte[] empty = {};

	protected final LightnerDataCard data;

	protected AbstractLightnerCardContent(final LightnerDataCard data) {
		this.data = data;
	}

	protected LightnerDataContent getContent(final String name) {
		for (final String suffix : getFileSuffixes()) {
			final LightnerDataContent result = data.getContent(name + suffix);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	protected byte[] read(final String name) throws IOException {
		final LightnerDataContent content = getContent(name);
		if (content == null) {
			return empty;
		}
		final ByteArrayOutputStream result = new ByteArrayOutputStream();
		try (InputStream in = content.getInputStream()) {
			final byte[] buffer = new byte[4096];
			int n;
			while ((n = in.read(buffer)) >= 0) {
				result.write(buffer, 0, n);
			}
		}
		return result.toByteArray();
	}

	protected LightnerDataContent write(final String name, final byte[] content) throws IOException {
		final LightnerDataContent result = data.getContent(name, true);
		try (OutputStream o = result.getOutputStream()) {
			o.write(content);
		}
		return result;
	}

}
