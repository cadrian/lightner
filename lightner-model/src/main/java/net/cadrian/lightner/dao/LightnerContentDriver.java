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

import net.cadrian.lightner.dao.content.file.FileContentDriver;

public interface LightnerContentDriver {

	static LightnerContentDriver getDriver(final File root) throws LightnerDataException {
		final String driverClassName = System.getProperty("lightner.content.driver");
		if (driverClassName != null) {
			try {
				final Class<? extends LightnerContentDriver> driverClass = Class.forName(driverClassName)
						.asSubclass(LightnerContentDriver.class);
				return driverClass.getConstructor(File.class).newInstance(root);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException
					| ClassNotFoundException e) {
				throw new LightnerDataException(e);
			}
		}
		return new FileContentDriver(root);
	}

	LightnerDataCard getCard(String name);

	LightnerDataCard createCard(String name) throws LightnerDataException;

}
