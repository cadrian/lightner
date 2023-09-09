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
package net.cadrian.lightner.dao.metadata.memory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.cadrian.lightner.dao.LightnerContentDriver;
import net.cadrian.lightner.dao.LightnerDataCard;
import net.cadrian.lightner.dao.LightnerDataException;
import net.cadrian.lightner.dao.metadata.AbstractMetadataDriver;

public class MemoryMetadataDriver extends AbstractMetadataDriver {

	private final Map<Integer, Set<String>> metadata = new ConcurrentHashMap<>();

	public MemoryMetadataDriver(final File root, final LightnerContentDriver contentDriver)
			throws LightnerDataException {
		super(contentDriver);
	}

	@Override
	public Collection<LightnerDataCard> listCards(final int box) throws LightnerDataException {
		final List<LightnerDataCard> result = new ArrayList<>();
		for (final String name : getBox(box)) {
			final LightnerDataCard card = contentDriver.getCard(name);
			if (card != null) {
				result.add(card);
			}
		}
		return result;
	}

	private Set<String> getBox(final int box) {
		return metadata.computeIfAbsent(box, k -> new HashSet<>());
	}

	@Override
	public LightnerDataCard createCard(final String name, final int box) throws LightnerDataException {
		final LightnerDataCard result = contentDriver.createCard(name);
		getBox(box).add(name);
		return result;
	}

	@Override
	public void moveCard(final LightnerDataCard card, final int fromBox, final int toBox) throws LightnerDataException {
		final String name = card.getName();
		if (!getBox(fromBox).remove(name)) {
			throw new LightnerDataException("Could not move card");
		}
		getBox(toBox).add(name);
	}

}
