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

public class LightnerCard {

	private static final Logger logger = Logger.getLogger(LightnerCard.class.getName());

	private final LightnerBox box;
	private final LightnerDataCard data;
	private final LightnerCardContent.Type type;
	private final LightnerCardContent content;

	private LightnerDate lastChange;
	private int boxNumber;

	LightnerCard(final LightnerBox box, final LightnerCardContent.Type type, final LightnerDataCard data)
			throws IOException {
		this.box = box;
		this.data = data;
		this.type = type;
		lastChange = new LightnerDate("19741215");
		boxNumber = 1;
		content = type.getContent(data);

		final LightnerDataContent typeFile = data.getContent("type", true);
		try (final PrintStream o = new PrintStream(typeFile.getOutputStream())) {
			o.print(type.toString());
		}

		updateHistory("created", true);
	}

	LightnerCard(final LightnerBox box, final int boxNumber, final LightnerDataCard data) throws IOException {
		this.box = box;
		this.data = data;
		this.boxNumber = boxNumber;
		lastChange = getLast(data);
		type = getType(data);
		content = type.getContent(data);
		updateHistory("loaded", false);
	}

	private void updateHistory(final String comment, final boolean create) throws IOException {
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
	}

	private static LightnerDate getLast(final LightnerDataCard data) throws IOException {
		final LightnerDataContent lastData = data.getContent("last");
		try (InputStream in = lastData.getInputStream()) {
			final byte[] buffer = new byte[lastData.length()];
			in.read(buffer);
			return new LightnerDate(new String(buffer).trim());
		}
	}

	private static LightnerCardContent.Type getType(final LightnerDataCard data) throws IOException {
		final LightnerDataContent typeData = data.getContent("type");
		try (InputStream in = typeData.getInputStream()) {
			final byte[] buffer = new byte[typeData.length()];
			in.read(buffer);
			return LightnerCardContent.Type.valueOf(new String(buffer).trim());
		} catch (final IllegalArgumentException e) {
			throw new IOException(e);
		}
	}

	LightnerDataCard getData() {
		return data;
	}

	public String getName() {
		return data.getName();
	}

	public LightnerDate getLastChange() {
		return lastChange;
	}

	public LightnerCardContent.Type getType() {
		return type;
	}

	public int getBoxNumber() {
		return boxNumber;
	}

	public LightnerCardContent getContent() {
		return content;
	}

	public boolean update(final int boxNumber, final String comment) throws IOException {
		if (!box.move(this, this.boxNumber, boxNumber)) {
			logger.severe(() -> "Could not move box " + getName() + " from " + this.boxNumber + " to " + boxNumber);
			return false;
		}

		lastChange = new LightnerDate();
		this.boxNumber = boxNumber;
		updateHistory(comment, false);
		return true;
	}

}
