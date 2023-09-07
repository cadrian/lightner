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
package net.cadrian.lightner.dao.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import net.cadrian.lightner.dao.LightnerDataCard;
import net.cadrian.lightner.dao.LightnerDataContent;
import net.cadrian.lightner.dao.LightnerDataDriver;
import net.cadrian.lightner.dao.LightnerDataException;

public class FileDriver implements LightnerDataDriver {

	private static final Logger logger = Logger.getLogger(FileDriver.class.getName());

	public static LightnerDataContent toContent(final File file) {
		return new ContentFile(file, null);
	}

	private final File root;
	private final File cards;

	public FileDriver(final File root) throws LightnerDataException {
		this.root = root;
		cards = new File(root, "cards");
		if (!root.isDirectory()) {
			if (root.exists()) {
				throw new LightnerDataException(root + " already exists and is not a directory");
			}
			cards.mkdirs();
			for (int i = 1; i <= 7; i++) {
				new File(root, Integer.toString(i)).mkdir();
			}
		}
	}

	@Override
	public Collection<LightnerDataCard> listCards(final int box) {
		final List<LightnerDataCard> result = new ArrayList<>();
		final File cardbox = new File(root, Integer.toString(box));
		logger.info(() -> "Getting cards for box " + box + ": " + cardbox.getPath());
		for (final File f : cardbox.listFiles(File::isFile)) {
			final File cardFile = new File(cards, f.getName());
			if (cardFile.isDirectory()) {
				result.add(new CardFile(cardFile));
			} else if (!f.delete()) {
				logger.severe(() -> "Could not delete stale reference to card: " + f.getPath());
			}
		}
		return result;
	}

	@Override
	public LightnerDataCard createCard(final String name, final int box) throws LightnerDataException {
		final File f = new File(cards, name);
		if (!f.mkdir()) {
			final String msg = "Could not create " + f.getPath();
			logger.severe(msg);
			throw new LightnerDataException(msg);
		}
		final File b = new File(new File(root, "1"), name);
		try {
			if (!b.createNewFile()) {
				final String msg = "Could not create " + b.getPath();
				logger.severe(msg);
				if (!f.delete()) {
					logger.severe(() -> "Could not delete stale reference to card: " + f.getPath());
				}
				throw new LightnerDataException(msg);
			}
		} catch (final IOException e) {
			throw new LightnerDataException(e);
		}
		return new CardFile(f);
	}

	@Override
	public boolean moveCard(final LightnerDataCard card, final int fromBox, final int toBox) {
		final File from = new File(new File(root, Integer.toString(fromBox)), card.getName());
		final File to = new File(new File(root, Integer.toString(toBox)), card.getName());
		return from.renameTo(to);
	}

}
