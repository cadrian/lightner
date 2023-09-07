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
import net.cadrian.lightner.dao.metadata.file.FileMetadataDriver;

public final class DataDrivers {

	private static final Object lock = new Object();
	private static LightnerMetadataDriver metadataDriver;
	private static LightnerContentDriver contentDriver;

	private DataDrivers() {
		// no instance
	}

	public static LightnerMetadataDriver getMetadataDriver(final File root) throws LightnerDataException {
		synchronized (lock) {
			if (metadataDriver == null) {
				final LightnerContentDriver contentDriver = getContentDriver(root);
				final String driverClassName = System.getProperty("lightner.data.driver");
				if (driverClassName == null) {
					return new FileMetadataDriver(root, contentDriver);
				}
				try {
					final Class<? extends LightnerMetadataDriver> driverClass = Class.forName(driverClassName)
							.asSubclass(LightnerMetadataDriver.class);
					metadataDriver = driverClass.getConstructor(File.class, LightnerContentDriver.class)
							.newInstance(root, contentDriver);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException
						| ClassNotFoundException e) {
					throw new LightnerDataException(e);
				}
			}
		}
		return metadataDriver;
	}

	public static LightnerContentDriver getContentDriver(final File root) throws LightnerDataException {
		synchronized (lock) {
			if (contentDriver == null) {
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
				contentDriver = new FileContentDriver(root);
			}
			return contentDriver;
		}
	}

}
