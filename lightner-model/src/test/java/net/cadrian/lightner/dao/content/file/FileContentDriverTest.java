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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.cadrian.lightner.dao.LightnerDataCard;
import net.cadrian.lightner.dao.LightnerDataException;

class FileContentDriverTest extends AbstractTest {

	FileContentDriver driver;

	@BeforeEach
	void prepareDriver() throws LightnerDataException {
		driver = new FileContentDriver(tmpdir);
	}

	@AfterEach
	void removeContent() throws LightnerDataException {
		deleteFiles(driver.getCardsDirectory());
	}

	@Test
	void checkDirectory() {
		assertTrue(driver.getCardsDirectory().exists());
	}

	@Test
	void testMissingCard() {
		assertNull(driver.getCard("unknown"));
	}

	@Test
	void testCreateCard() throws LightnerDataException {
		final String randomName = "card-" + UUID.randomUUID();
		final LightnerDataCard newCard = driver.createCard(randomName);
		assertNotNull(newCard);
		assertEquals(newCard, driver.getCard(randomName));
	}

}
