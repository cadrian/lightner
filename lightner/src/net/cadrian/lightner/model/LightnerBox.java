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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

public class LightnerBox {

	private static final Logger logger = Logger.getLogger(LightnerBox.class.getName());

	private final LightnerDay day = new LightnerDay();
	private final File root;
	private final File cards;

	public LightnerBox(final File root) throws IOException {
		this.root = root;
		cards = new File(root, "cards");
		if (!root.isDirectory()) {
			if (root.exists()) {
				throw new IOException(root + " already exists and is not a directory");
			}
			cards.mkdirs();
			for (int i = 1; i <= 7; i++) {
				new File(root, Integer.toString(i)).mkdir();
			}
		}
	}

	public LightnerDay getDay() {
		return day;
	}

	public Collection<LightnerCard> getCards() throws IOException {
		final List<LightnerCard> result = new ArrayList<>();
		for (final int box : day.getBoxes()) {
			final File cardbox = new File(root, Integer.toString(box));
			logger.info(() -> "Getting cards for box " + box + ": " + cardbox.getPath());
			for (final File f : cardbox.listFiles(File::isFile)) {
				logger.info(() -> "Adding card: " + f.getName());
				result.add(new LightnerCard(this, box, new File(cards, f.getName())));
			}
		}
		logger.info(() -> "Cards: " + result);
		return result;
	}

	public LightnerCard newCard(final String name, final LightnerCardContent.Type type) throws IOException {
		final File f = new File(cards, name);
		f.mkdir();
		final LightnerCard result = new LightnerCard(this, type, f);
		final File b = new File(new File(root, "1"), name);
		if (!b.createNewFile()) {
			// TODO delete f and its content
			throw new IOException("Could not create " + b.getPath());
		}
		return result;
	}

	boolean move(final LightnerCard card, final int fromBox, final int toBox) {
		final File from = new File(new File(root, Integer.toString(fromBox)), card.getName());
		final File to = new File(new File(root, Integer.toString(toBox)), card.getName());
		return from.renameTo(to);
	}

}
