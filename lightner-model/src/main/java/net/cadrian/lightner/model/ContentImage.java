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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

/**
 *
 */
public class ContentImage extends AbstractLightnerCardContent {

	private static final Set<String> SUFFIXES = Collections
			.unmodifiableSet(new HashSet<>(Arrays.asList(".png", ".gif", ".jpg", ".jpeg")));

	public enum ImageType {
		PNG("png"), GIF("gif"), JPG("jpg", "jpeg");

		private static final Map<String, ImageType> MAP = new HashMap<>();
		private String[] suffixes;

		private ImageType(final String... suffixes) {
			this.suffixes = suffixes;
		}

		public String getSuffix() {
			return suffixes[0];
		}

		static {
			for (final ImageType t : values()) {
				for (final String s : t.suffixes) {
					MAP.put(s, t);
				}
			}
		}

		public static ImageType get(final String filename) {
			final int i = filename.lastIndexOf('.');
			final String suffix = i < 0 ? filename : filename.substring(i + 1);
			return MAP.get(suffix.toLowerCase());
		}
	}

	private BufferedImage question;
	private BufferedImage answer;

	ContentImage(final File file) throws IOException {
		super(file);
		question = ImageIO.read(new ByteArrayInputStream(read("question")));
		answer = ImageIO.read(new ByteArrayInputStream(read("answer")));
	}

	public BufferedImage getQuestion() {
		return question;
	}

	public void setQuestion(final BufferedImage question, final ImageType type) throws IOException {
		this.question = question;
		final ByteArrayOutputStream o = new ByteArrayOutputStream();
		ImageIO.write(question, type.getSuffix(), o);
		write("question." + type.getSuffix(), o.toByteArray());
	}

	public BufferedImage getAnswer() {
		return answer;
	}

	public void setAnswer(final BufferedImage answer, final ImageType type) throws IOException {
		this.answer = answer;
		final ByteArrayOutputStream o = new ByteArrayOutputStream();
		ImageIO.write(answer, type.getSuffix(), o);
		write("answer." + type.getSuffix(), o.toByteArray());
	}

	@Override
	public void accept(final Visitor v) {
		v.visitImage(this);
	}

	@Override
	public Collection<String> getFileSuffixes() {
		return SUFFIXES;
	}

}
