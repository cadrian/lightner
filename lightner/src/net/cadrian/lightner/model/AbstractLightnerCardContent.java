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
package net.cadrian.lightner.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Common file manipulation methods
 *
 * @param <T>
 */
abstract class AbstractLightnerCardContent implements LightnerCardContent {

	protected AbstractLightnerCardContent() {
		// empty
	}

	protected static String read(final File file, final String name) throws IOException {
		final File f = new File(file, name);
		if (!f.exists()) {
			return "";
		}
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try (InputStream in = new BufferedInputStream(new FileInputStream(f))) {
			final byte[] buffer = new byte[4096];
			int n;
			while ((n = in.read(buffer)) >= 0) {
				bytes.write(buffer, 0, n);
			}
		}
		return new String(bytes.toByteArray());
	}

	protected static void write(final File file, final String name, final String content) throws IOException {
		final File f = new File(file, name);
		try (OutputStream o = new BufferedOutputStream(new FileOutputStream(f))) {
			o.write(content.getBytes());
		}
	}

}
