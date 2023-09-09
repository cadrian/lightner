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
package net.cadrian.lightner.dao.content.memory;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import net.cadrian.lightner.dao.LightnerDataCard;
import net.cadrian.lightner.dao.LightnerDataException;
import net.cadrian.lightner.dao.content.AbstractContentDriver;

public class MemoryContentDriver extends AbstractContentDriver {

	private final Map<String, CardMemory> cards = new ConcurrentHashMap<>();

	public MemoryContentDriver(final File root) throws LightnerDataException {
		super(root);
	}

	@Override
	public LightnerDataCard getCard(final String name) {
		return cards.get(name);
	}

	@Override
	public LightnerDataCard createCard(final String name) throws LightnerDataException {
		final CardMemory newcard = new CardMemory(this, name);
		final CardMemory result = cards.computeIfAbsent(name, k -> newcard);
		if (result != newcard) {
			throw new LightnerDataException("Duplicate card: " + name);
		}
		return result;
	}

	void delete(final String name) {
		cards.remove(name);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(cards);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj) || (getClass() != obj.getClass())) {
			return false;
		}
		final MemoryContentDriver other = (MemoryContentDriver) obj;
		return Objects.equals(cards, other.cards);
	}

}
