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
package net.cadrian.lightner.model.content.audio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.cadrian.lightner.dao.LightnerDataContent;

public class AudioContainer {
	private final AudioType type;
	private final LightnerDataContent content;

	public AudioContainer(final LightnerDataContent content) throws IOException {
		this.content = content;
		type = AudioType.get(content.getName());
		if (type == null) {
			throw new IOException("Unknown file type: " + content.getName());
		}
	}

	byte[] getAudioBytes() throws IOException {
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

	public LightnerDataContent getContent() {
		return content;
	}

	public AudioType getType() {
		return type;
	}

}
