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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.cadrian.lightner.dao.LightnerDataCard;
import net.cadrian.lightner.dao.LightnerDataContent;

class ContentMemory implements LightnerDataContent {

	private static final Logger logger = Logger.getLogger(ContentMemory.class.getName());

	private final CardMemory card;
	private final String name;

	private final AtomicReference<byte[]> content = new AtomicReference<>(new byte[0]);

	public ContentMemory(final CardMemory card, final String name) {
		this.card = card;
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public LightnerDataCard getCard() {
		return card;
	}

	@Override
	public URI getURI() {
		try {
			return new URI("memory:%s/%s".formatted(card.getName(), name));
		} catch (final URISyntaxException e) {
			logger.log(Level.SEVERE, e, () -> "Problem with memory URI");
			return null;
		}
	}

	@Override
	public OutputStream getOutputStream(final boolean append) {
		return new ByteArrayOutputStream() {
			@Override
			public void flush() throws IOException {
				content.set(toByteArray());
			}

			@Override
			public void close() throws IOException {
				content.set(toByteArray());
			}
		};
	}

	@Override
	public int length() {
		return content.get().length;
	}

	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(content.get());
	}

}
