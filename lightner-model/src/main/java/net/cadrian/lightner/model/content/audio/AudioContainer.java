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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioContainer {
	private final AudioType type;
	private final File file;

	public AudioContainer(final File file) throws IOException {
		this.file = file;
		type = AudioType.get(file.getName());
		if (type == null) {
			throw new IOException("Unknown file type: " + file.getName());
		}
	}

	byte[] getAudioBytes() throws IOException {
		final ByteArrayOutputStream o = new ByteArrayOutputStream();
		try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
			final byte[] buffer = new byte[4096];
			int n;
			while ((n = in.read(buffer)) >= 0) {
				o.write(buffer, 0, n);
			}
		}
		return o.toByteArray();
	}

	public File getFile() {
		return file;
	}

	public AudioType getType() {
		return type;
	}

}