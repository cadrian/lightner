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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import net.cadrian.lightner.dao.DataDrivers;
import net.cadrian.lightner.dao.LightnerDataCard;
import net.cadrian.lightner.dao.LightnerDataException;
import net.cadrian.lightner.dao.LightnerMetadataDriver;

public class LightnerBox {

	private static final Logger logger = Logger.getLogger(LightnerBox.class.getName());

	private final LightnerMetadataDriver dao;
	private final LightnerDay day = new LightnerDay();

	public LightnerBox(final File root) throws LightnerModelException {
		logger.info(() -> "Lightner Box root: " + root.getAbsolutePath());
		try {
			dao = DataDrivers.getMetadataDriver(root);
		} catch (final LightnerDataException e) {
			throw new LightnerModelException(e);
		}
	}

	public LightnerDay getDay() {
		return day;
	}

	public Collection<LightnerCard> getCards() throws LightnerModelException {
		final List<LightnerCard> result = new ArrayList<>();
		final LightnerDate now = new LightnerDate();
		for (final int box : day.getBoxes()) {
			try {
				final Collection<LightnerDataCard> cards = dao.listCards(box);
				for (final LightnerDataCard cardFile : cards) {
					logger.info(() -> "Adding card: " + cardFile.getName());
					final LightnerCard card = new LightnerCard(this, box, cardFile);
					if (now.compareTo(card.getLastChange()) > 0) {
						result.add(card);
					} else {
						logger.info(() -> "Not displaying card, already done today: " + card.getName() + " ("
								+ card.getLastChange().toPrettyString() + " / " + now.toPrettyString() + ")");
					}
				}
			} catch (final LightnerDataException e) {
				throw new LightnerModelException(e);
			}
		}
		logger.info(() -> "Cards: " + result);
		return result;
	}

	public LightnerCard newCard(final String name, final LightnerCardType type, final String title)
			throws LightnerModelException {
		try {
			return new LightnerCard(this, type, title, dao.createCard(name, 1));
		} catch (final LightnerDataException e) {
			throw new LightnerModelException(e);
		}
	}

	void move(final LightnerCard card, final int fromBox, final int toBox) throws LightnerModelException {
		try {
			dao.moveCard(card.getData(), fromBox, toBox);
		} catch (final LightnerDataException e) {
			throw new LightnerModelException(e);
		}
	}

}
