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
package net.cadrian.lightner.dao.content.file;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

abstract class AbstractTest {

	private static final Logger logger = Logger.getLogger(AbstractTest.class.getName());

	protected static File tmpdir;

	@BeforeAll
	static void setup() throws IOException {
		tmpdir = Files.createTempDirectory("ContentFileTest").toFile();
		logger.info(() -> "Temp dir: " + tmpdir.getPath());
	}

	@AfterAll
	static void teardown() {
		deleteFiles(tmpdir);
	}

	private static void deleteFiles(final File file) {
		logger.info(() -> "Deleting: " + file.getPath());
		if (file.isDirectory()) {
			for (final File f : file.listFiles()) {
				deleteFiles(f);
			}
		}
		file.delete();
	}

	@Test
	void autotest() {
		assertTrue(tmpdir.isDirectory());
	}

}
