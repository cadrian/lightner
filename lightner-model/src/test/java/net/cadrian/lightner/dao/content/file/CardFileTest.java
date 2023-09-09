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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.cadrian.lightner.dao.LightnerDataContent;
import net.cadrian.lightner.dao.LightnerDataException;

class CardFileTest extends AbstractTest {

	private FileContentDriver driver;
	private String randomName;
	private File cardFile;
	private CardFile card;
	private final List<File> deleted = new ArrayList<>();

	@BeforeEach
	void prepareContent() throws LightnerDataException {
		deleted.clear();
		driver = new FileContentDriver(tmpdir) {
			@Override
			void deleteFile(final File file) {
				deleted.add(file);
				deleteFiles(file);
			}
		};
		randomName = "card-" + UUID.randomUUID();
		cardFile = new File(tmpdir, randomName);
		assertTrue(cardFile.mkdir());
		card = new CardFile(driver, cardFile);
	}

	@AfterEach
	void removeContent() throws LightnerDataException {
		if (cardFile.exists()) {
			deleteFiles(cardFile);
		}
	}

	@Test
	void testMetadata() {
		assertEquals(cardFile.getName(), card.getName());
	}

	@Test
	void testMissingContent() throws LightnerDataException {
		assertNull(card.getContent("missing"));
	}

	@Test
	void testCreateContent() throws LightnerDataException {
		final File newcontent = new File(cardFile, "newcontent");
		assertFalse(newcontent.exists());
		final LightnerDataContent content = card.getContent(newcontent.getName(), true);
		assertNotNull(content);
		assertTrue(newcontent.exists());
		assertEquals(content, card.getContent(newcontent.getName()));
	}

	@Test
	void testDeleteCard() throws LightnerDataException {
		final File contenttodelete = new File(cardFile, "contentodelete");
		assertFalse(contenttodelete.exists());
		final LightnerDataContent content = card.getContent(contenttodelete.getName(), true);
		assertNotNull(content);
		assertTrue(contenttodelete.exists());
		card.delete();
		assertFalse(contenttodelete.exists());
		assertFalse(cardFile.exists());
		assertTrue(deleted.contains(contenttodelete));
		assertTrue(deleted.contains(cardFile));
	}

}
