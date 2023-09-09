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
package net.cadrian.lightner.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import net.cadrian.lightner.dao.LightnerDataCard;
import net.cadrian.lightner.dao.LightnerDataContent;
import net.cadrian.lightner.dao.LightnerDataException;

public class LightnerCard {

	private static final Logger logger = Logger.getLogger(LightnerCard.class.getName());

	private final LightnerBox box;
	private final String title;
	private final LightnerCardType type;
	private final LightnerCardContent content;
	private final LightnerDataCard data;

	private LightnerDate lastChange;
	private int boxNumber;

	LightnerCard(final LightnerBox box, final LightnerCardType type, final String title, final LightnerDataCard data)
			throws LightnerModelException {
		this.box = box;
		this.title = title;
		this.type = type;
		this.data = data;
		lastChange = new LightnerDate("19741215");
		boxNumber = 1;
		content = type.getContent(data, title);

		try {
			final LightnerDataContent titleContent = data.getContent("title", true);
			try (final PrintStream o = new PrintStream(titleContent.getOutputStream())) {
				o.print(title);
			}

			final LightnerDataContent typeContent = data.getContent("type", true);
			try (final PrintStream o = new PrintStream(typeContent.getOutputStream())) {
				o.print(type.toString());
			}
		} catch (final LightnerDataException e) {
			throw new LightnerModelException(e);
		}

		updateHistory("created", true);
	}

	LightnerCard(final LightnerBox box, final int boxNumber, final LightnerDataCard data)
			throws LightnerModelException {
		this.box = box;
		this.data = data;
		this.boxNumber = boxNumber;
		lastChange = getLast(data);
		type = getType(data);
		title = getTitle(data);
		content = type.getContent(data, title);
		updateHistory("loaded", false);
	}

	private void updateHistory(final String comment, final boolean create) throws LightnerModelException {
		try {
			final LightnerDataContent lastData = data.getContent("last", create);
			try (final PrintStream o = new PrintStream(lastData.getOutputStream())) {
				o.print(lastChange.toString());
			}
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			final String now = sdf.format(new Date());
			final LightnerDataContent historyData = data.getContent("history", create);
			final String line = String.format("%s | box %d | %s", now, boxNumber, comment);
			try (final PrintStream o = new PrintStream(historyData.getOutputStream(true))) {
				o.println(line);
			}
		} catch (final LightnerDataException e) {
			throw new LightnerModelException(e);
		}
	}

	private static LightnerDate getLast(final LightnerDataCard data) throws LightnerModelException {
		try {
			final LightnerDataContent lastData = data.getContent("last");
			final byte[] buffer = read(lastData);
			return new LightnerDate(new String(buffer).trim());
		} catch (final LightnerDataException e) {
			throw new LightnerModelException(e);
		}
	}

	private static String getTitle(final LightnerDataCard data) throws LightnerModelException {
		try {
			final LightnerDataContent titleData = data.getContent("title");
			final byte[] buffer = read(titleData);
			return new String(buffer).trim();
		} catch (final LightnerDataException e) {
			throw new LightnerModelException(e);
		}
	}

	private static LightnerCardType getType(final LightnerDataCard data) throws LightnerModelException {
		try {
			final LightnerDataContent typeData = data.getContent("type");
			final byte[] buffer = read(typeData);
			return LightnerCardType.valueOf(new String(buffer).trim());
		} catch (final IllegalArgumentException | LightnerDataException e) {
			throw new LightnerModelException(e);
		}
	}

	private static byte[] read(final LightnerDataContent lastData) throws LightnerModelException {
		final int length = lastData.length();
		final byte[] buffer = new byte[length];
		try (InputStream in = lastData.getInputStream()) {
			final int n = in.read(buffer);
			if (n < length) {
				logger.severe(() -> "Truncated input (%d<%d): %s/%s".formatted(n, length, lastData.getCard().getName(),
						lastData.getName()));
			}
		} catch (final IOException e) {
			throw new LightnerModelException(e);
		}
		return buffer;
	}

	LightnerDataCard getData() {
		return data;
	}

	public String getName() {
		return data.getName();
	}

	public String getTitle() {
		return title;
	}

	public LightnerDate getLastChange() {
		return lastChange;
	}

	public LightnerCardType getType() {
		return type;
	}

	public int getBoxNumber() {
		return boxNumber;
	}

	public LightnerCardContent getContent() {
		return content;
	}

	public void update(final int boxNumber, final String comment) throws LightnerModelException {
		try {
			box.move(this, this.boxNumber, boxNumber);
		} catch (final LightnerModelException e) {
			throw new LightnerModelException(
					"Could not move box " + getName() + " from " + this.boxNumber + " to " + boxNumber);
		}

		lastChange = new LightnerDate();
		this.boxNumber = boxNumber;
		updateHistory(comment, false);
	}

}
