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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.cadrian.lightner.dao.LightnerDataCard;
import net.cadrian.lightner.dao.LightnerDataContent;
import net.cadrian.lightner.dao.LightnerDataException;

class CardMemory implements LightnerDataCard {

	private final MemoryContentDriver contentDriver;
	private final String name;

	private final Map<String, ContentMemory> content = new ConcurrentHashMap<>();

	CardMemory(final MemoryContentDriver contentDriver, final String name) {
		this.contentDriver = contentDriver;
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public LightnerDataContent getContent(final String name, final boolean create) {
		return content.computeIfAbsent(name, k -> create ? new ContentMemory(this, name) : null);
	}

	@Override
	public void delete() throws LightnerDataException {
		contentDriver.delete(name);
	}

}
