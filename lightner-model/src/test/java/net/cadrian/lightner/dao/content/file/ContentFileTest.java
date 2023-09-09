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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContentFileTest extends AbstractTest {

	private String randomName;
	private CardFile card;
	private File contentFile;
	private ContentFile content;

	@BeforeEach
	void prepareContent() {
		randomName = UUID.randomUUID().toString();
		card = new CardFile(null, new File(tmpdir, "card"));
		contentFile = new File(tmpdir, randomName);
		content = new ContentFile(contentFile, card);
	}

	@AfterEach
	void removeContent() {
		if (contentFile.exists()) {
			contentFile.delete();
		}
	}

	@Test
	void testMetadata() {
		assertSame(card, content.getCard());
		assertEquals(contentFile.getName(), content.getName());
		assertEquals(contentFile.toURI(), content.getURI());
	}

	@Test
	void testContentRead() throws IOException {
		final String data = UUID.randomUUID().toString();

		try (PrintStream o = new PrintStream(contentFile)) {
			o.print(data);
		}

		try (InputStream in = content.getInputStream()) {
			final byte[] b = new byte[1024];
			final int n = in.read(b);
			assertEquals(data.length(), n);
			assertEquals(data, new String(b, 0, n));
		}
	}

	@Test
	void testContentWrite() throws IOException {
		final String data = UUID.randomUUID().toString();

		try (PrintStream o = new PrintStream(content.getOutputStream())) {
			o.print(data);
		}

		assertTrue(contentFile.exists());

		try (InputStream in = new FileInputStream(contentFile)) {
			final byte[] b = new byte[1024];
			final int n = in.read(b);
			assertEquals(data.length(), n);
			assertEquals(data, new String(b, 0, n));
		}
	}

}
