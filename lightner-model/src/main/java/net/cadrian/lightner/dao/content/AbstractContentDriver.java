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
package net.cadrian.lightner.dao.content;

import java.io.File;
import java.util.Objects;

import net.cadrian.lightner.dao.LightnerContentDriver;
import net.cadrian.lightner.dao.LightnerDataException;

public abstract class AbstractContentDriver implements LightnerContentDriver {

	protected final File root;

	protected AbstractContentDriver(final File root) throws LightnerDataException {
		if (root != null && root.exists() && !root.isDirectory()) {
			throw new LightnerDataException(root + " already exists and is not a directory");
		}
		this.root = root;
	}

	@Override
	public int hashCode() {
		return Objects.hash(root);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		final AbstractContentDriver other = (AbstractContentDriver) obj;
		return Objects.equals(root, other.root);
	}

}
