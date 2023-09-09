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
package net.cadrian.lightner.dao.content.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.cadrian.lightner.dao.LightnerDataContent;
import net.cadrian.lightner.dao.LightnerDataException;

class CardMemoryTest extends AbstractTest {

	private MemoryContentDriver driver;
	private String randomName;
	private CardMemory card;
	private final List<String> deleted = new ArrayList<>();

	private class ExtendedMemoryContentDriver extends MemoryContentDriver {
		ExtendedMemoryContentDriver() throws LightnerDataException {
			super(null);
		}

		@Override
		void delete(final String name) {
			super.delete(name);
			deleted.add(name);
		}
	}

	@BeforeEach
	void prepareContent() throws LightnerDataException {
		deleted.clear();
		driver = new ExtendedMemoryContentDriver();
		randomName = "card-" + UUID.randomUUID();
		card = new CardMemory(driver, randomName);
	}

	@Test
	void testMetadata() {
		assertEquals(randomName, card.getName());
	}

	@Test
	void testMissingContent() throws LightnerDataException {
		assertNull(card.getContent("missing"));
	}

	@Test
	void testCreateContent() throws LightnerDataException {
		final LightnerDataContent content = card.getContent("newcontent", true);
		assertNotNull(content);
		assertEquals(content, card.getContent("newcontent"));
	}

	@Test
	void testDeleteCard() throws LightnerDataException {
		final LightnerDataContent content = card.getContent("contenttodelete", true);
		assertNotNull(content);
		card.delete();
		assertTrue(deleted.contains(randomName));
	}

}
