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

import java.io.File;
import java.util.logging.Logger;

import net.cadrian.lightner.dao.LightnerContentDriver;
import net.cadrian.lightner.dao.LightnerDataCard;
import net.cadrian.lightner.dao.LightnerDataContent;
import net.cadrian.lightner.dao.LightnerDataException;

public class FileContentDriver implements LightnerContentDriver {

	private static final Logger logger = Logger.getLogger(FileContentDriver.class.getName());

	private final File cards;

	public static LightnerDataContent toContent(final File file) {
		return new ContentFile(file, null);
	}

	public FileContentDriver(final File root) throws LightnerDataException {
		if (root.exists() && !root.isDirectory()) {
			throw new LightnerDataException(root + " already exists and is not a directory");
		}
		cards = new File(root, "cards");
		cards.mkdirs();
	}

	@Override
	public LightnerDataCard getCard(final String name) {
		final File cardFile = new File(cards, name);
		if (cardFile.isDirectory()) {
			return new CardFile(this, cardFile);
		}
		return null;
	}

	@Override
	public LightnerDataCard createCard(final String name) throws LightnerDataException {
		final File f = new File(cards, name);
		if (!f.mkdir()) {
			final String msg = "Could not create " + f.getPath();
			logger.severe(msg);
			throw new LightnerDataException(msg);
		}
		return new CardFile(this, f);
	}

	void delete(final File file) throws LightnerDataException {
		if (file.isDirectory()) {
			for (final File f : file.listFiles()) {
				delete(f);
			}
		}
		if (!file.delete()) {
			throw new LightnerDataException("Could not delete " + file.getPath());
		}
	}

}
