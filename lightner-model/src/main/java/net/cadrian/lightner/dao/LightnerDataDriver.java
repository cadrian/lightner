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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import net.cadrian.lightner.dao.file.FileDriver;

public interface LightnerDataDriver {

	public static LightnerDataDriver getDriver(final File root) throws LightnerDataException {
		final String driverClassName = System.getProperty("lightner.data.driver");
		if (driverClassName != null) {
			try {
				final Class<? extends LightnerDataDriver> driverClass = Class.forName(driverClassName)
						.asSubclass(LightnerDataDriver.class);
				return driverClass.getConstructor(File.class).newInstance(root);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException
					| ClassNotFoundException e) {
				throw new LightnerDataException(e);
			}
		}
		return new FileDriver(root);
	}

	public Collection<LightnerDataCard> listCards(int box) throws LightnerDataException;

	public LightnerDataCard createCard(String name, int box) throws LightnerDataException;

	public boolean moveCard(LightnerDataCard card, int fromBox, int toBox);

}
