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
package net.cadrian.lightner.dao.sqlite;

import java.io.File;
import java.util.Objects;

import net.cadrian.lightner.dao.LightnerDataCard;
import net.cadrian.lightner.dao.LightnerDataContent;

class CardSqlite implements LightnerDataCard {

	private final File file;

	CardSqlite(final File file) {
		this.file = Objects.requireNonNull(file);
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public LightnerDataContent getContent(final String name, final boolean create) {
		final File result = new File(file, name);
		if (!result.exists() && !create) {
			return null;
		}
		return new ContentSqlite(result);
	}

}
