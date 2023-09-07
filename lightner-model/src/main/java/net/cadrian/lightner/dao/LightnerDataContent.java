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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public interface LightnerDataContent {

	String getName();

	LightnerDataCard getCard();

	URI getURI();

	default OutputStream getOutputStream() {
		return getOutputStream(false);
	}

	OutputStream getOutputStream(boolean append);

	int length();

	InputStream getInputStream();

}
