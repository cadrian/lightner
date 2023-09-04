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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class LightnerCard {

	private static final Logger logger = Logger.getLogger(LightnerCard.class.getName());

	private final LightnerBox box;
	private final File file;
	private final LightnerCardContent.Type type;
	private final LightnerCardContent content;

	private LightnerDate lastChange;
	private int boxNumber;

	LightnerCard(final LightnerBox box, final LightnerCardContent.Type type, final File file) throws IOException {
		this.box = box;
		this.file = file;
		this.type = type;
		lastChange = new LightnerDate("19741215");
		boxNumber = 1;
		content = type.getContent(file);

		final File typeFile = new File(file, "type");
		try (final PrintStream o = new PrintStream(new FileOutputStream(typeFile))) {
			o.print(type.toString());
		}

		updateHistory("created");
	}

	LightnerCard(final LightnerBox box, final int boxNumber, final File file) throws IOException {
		this.box = box;
		this.file = file;
		this.boxNumber = boxNumber;
		lastChange = getLast(file);
		type = getType(file);
		content = type.getContent(file);
		updateHistory("loaded");
	}

	private void updateHistory(final String comment) throws IOException {
		final File lastFile = new File(file, "last");
		try (final PrintStream o = new PrintStream(new FileOutputStream(lastFile))) {
			o.print(lastChange.toString());
		}
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final String now = sdf.format(new Date());
		final File historyFile = new File(file, "history");
		final String line = String.format("%s | box %d | %s", now, boxNumber, comment);
		if (!historyFile.exists()) {
			try (final PrintStream o = new PrintStream(new FileOutputStream(historyFile))) {
				o.println(line);
			}
		} else {
			try (final PrintStream o = new PrintStream(new FileOutputStream(historyFile, true))) {
				o.println(line);
			}
		}
	}

	private static LightnerDate getLast(final File file) throws IOException {
		final File dateFile = new File(file, "last");
		try (InputStream in = new BufferedInputStream(new FileInputStream(dateFile))) {
			final byte[] buffer = new byte[(int) dateFile.length()];
			in.read(buffer);
			return new LightnerDate(new String(buffer).trim());
		}
	}

	private static LightnerCardContent.Type getType(final File file) throws IOException {
		final File typeFile = new File(file, "type");
		try (InputStream in = new BufferedInputStream(new FileInputStream(typeFile))) {
			final byte[] buffer = new byte[(int) typeFile.length()];
			in.read(buffer);
			return LightnerCardContent.Type.valueOf(new String(buffer).trim());
		} catch (final IllegalArgumentException e) {
			throw new IOException(e);
		}
	}

	public String getName() {
		return file.getName();
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
		updateHistory(comment);
		return true;
	}

}
