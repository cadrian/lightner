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
package net.cadrian.lightner.dao.content.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.cadrian.lightner.dao.LightnerDataContent;
import net.cadrian.lightner.dao.LightnerDataException;

class ContentFile implements LightnerDataContent {

	private static final Logger logger = Logger.getLogger(ContentFile.class.getName());

	private final File file;
	private final CardFile card;

	public ContentFile(final File file, final CardFile card) throws LightnerDataException {
		this.file = file;
		this.card = card;
		if (!file.exists()) {
			try {
				if (!file.createNewFile()) {
					throw new LightnerDataException("Could not create file: " + file.getPath());
				}
			} catch (final IOException e) {
				throw new LightnerDataException(e);
			}
		}
	}

	@Override
	public CardFile getCard() {
		return card;
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public URI getURI() {
		return file.toURI();
	}

	@Override
	public OutputStream getOutputStream(final boolean append) {
		try {
			return new BufferedOutputStream(new FileOutputStream(file, append));
		} catch (final FileNotFoundException e) {
			logger.log(Level.SEVERE, e, () -> "Could not get output stream for " + getName());
			return null;
		}
	}

	@Override
	public int length() {
		return (int) file.length();
	}

	@Override
	public InputStream getInputStream() {
		try {
			return new BufferedInputStream(new FileInputStream(file));
		} catch (final FileNotFoundException e) {
			logger.log(Level.INFO, e, () -> "File not found: " + file.getPath());
			return new ByteArrayInputStream(new byte[0]);
		}
	}

}
