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
package net.cadrian.lightner.dao;

import java.io.IOException;

public class LightnerDataException extends IOException {

	private static final long serialVersionUID = 1401622248736643734L;

	public LightnerDataException() {
		super();
	}

	public LightnerDataException(final String msg) {
		super(msg);
	}

	public LightnerDataException(final Throwable t) {
		super(t);
	}

	public LightnerDataException(final String msg, final Throwable t) {
		super(msg, t);
	}

}
