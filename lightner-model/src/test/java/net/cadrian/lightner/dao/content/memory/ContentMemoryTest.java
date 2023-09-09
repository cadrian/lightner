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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.cadrian.lightner.dao.LightnerDataException;

class ContentMemoryTest extends AbstractTest {

	private String randomName;
	private CardMemory card;
	private ContentMemory content;

	@BeforeEach
	void prepareContent() throws LightnerDataException {
		randomName = "content-" + UUID.randomUUID();
		card = new CardMemory(new MemoryContentDriver(null), "card");
		content = new ContentMemory(card, randomName);
	}

	@Test
	void testMetadata() throws URISyntaxException {
		assertSame(card, content.getCard());
		assertEquals(randomName, content.getName());
		assertEquals(new URI("memory:card/%s".formatted(randomName)), content.getURI());
	}

	@Test
	void testContentRead() throws IOException {
		final String data = UUID.randomUUID().toString();

		content.setContent(data.getBytes());

		try (InputStream in = content.getInputStream()) {
			final byte[] b = new byte[1024];
			final int n = in.read(b);
			assertEquals(data.length(), n);
			assertEquals(data, new String(b, 0, n));
		}
	}

	@Test
	void testContentWrite() {
		final String data = UUID.randomUUID().toString();

		try (PrintStream o = new PrintStream(content.getOutputStream())) {
			o.print(data);
		}

		assertArrayEquals(data.getBytes(), content.getContent());
	}

}
