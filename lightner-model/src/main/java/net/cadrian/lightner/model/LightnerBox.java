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

import net.cadrian.lightner.dao.LightnerDataCard;
import net.cadrian.lightner.dao.LightnerDataDriver;

public class LightnerBox {

	private static final Logger logger = Logger.getLogger(LightnerBox.class.getName());

	private final LightnerDataDriver dao;
	private final LightnerDay day = new LightnerDay();

	public LightnerBox(final File root) throws IOException {
		logger.info(() -> "Lightner Box root: " + root.getAbsolutePath());
		dao = LightnerDataDriver.getDriver(root);
	}

	public LightnerDay getDay() {
		return day;
	}

	public Collection<LightnerCard> getCards() throws IOException {
		final List<LightnerCard> result = new ArrayList<>();
		final LightnerDate now = new LightnerDate();
		for (final int box : day.getBoxes()) {
			for (final LightnerDataCard cardFile : dao.listCards(box)) {
				logger.info(() -> "Adding card: " + cardFile.getName());
				final LightnerCard card = new LightnerCard(this, box, cardFile);
				if (now.compareTo(card.getLastChange()) > 0) {
					result.add(card);
				} else {
					logger.info(() -> "Not displaying card, already done today: " + card.getName() + " ("
							+ card.getLastChange().toPrettyString() + " / " + now.toPrettyString() + ")");
				}
			}
		}
		logger.info(() -> "Cards: " + result);
		return result;
	}

	public LightnerCard newCard(final String name, final LightnerCardType type, final String title) throws IOException {
		return new LightnerCard(this, type, title, dao.createCard(name, 1));
	}

	boolean move(final LightnerCard card, final int fromBox, final int toBox) {
		return dao.moveCard(card.getData(), fromBox, toBox);
	}

}
