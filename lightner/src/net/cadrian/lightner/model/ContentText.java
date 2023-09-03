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
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

class ContentText implements LightnerCardContent<String> {

	private static final Set<String> SUFFIXES = Collections.singleton(".txt");

	private final File file;

	private String question;
	private String answer;

	ContentText(final File file) throws IOException {
		this.file = file;
		question = read(file, "question");
		answer = read(file, "answer");
	}

	@Override
	public void setQuestion(final String question) throws IOException {
		this.question = question;
		write(file, "question", question);
	}

	@Override
	public void setAnswer(final String answer) throws IOException {
		this.answer = answer;
		write(file, "answer", answer);
	}

	@Override
	public String getQuestion() {
		return question;
	}

	@Override
	public String getAnswer() {
		return answer;
	}

	@Override
	public void accept(final Visitor v) {
		v.visitText(this);
	}

	@Override
	public Collection<String> getFileSuffixes() {
		return SUFFIXES;
	}

	private static String read(final File file, final String what) throws IOException {
		final File f = new File(file, what);
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try (InputStream in = new BufferedInputStream(new FileInputStream(f))) {
			final byte[] buffer = new byte[4096];
			int n;
			while ((n = in.read(buffer)) >= 0) {
				bytes.write(buffer, 0, n);
			}
		}
		return new String(bytes.toByteArray());
	}

	private static void write(final File file, final String what, final String content) throws IOException {
		final File f = new File(file, what);
		try (OutputStream o = new BufferedOutputStream(new FileOutputStream(f))) {
			o.write(content.getBytes());
		}
	}

}
