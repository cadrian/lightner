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
package net.cadrian.lightner.model.content.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import net.cadrian.lightner.dao.LightnerDataCard;
import net.cadrian.lightner.model.LightnerModelException;
import net.cadrian.lightner.model.content.AbstractLightnerCardContent;

/**
 *
 */
public class ContentImage extends AbstractLightnerCardContent {

	private static final Set<String> SUFFIXES = Collections
			.unmodifiableSet(new HashSet<>(Arrays.asList(".png", ".gif", ".jpg", ".jpeg")));

	private BufferedImage question;
	private BufferedImage answer;

	public ContentImage(final LightnerDataCard data, final String title) throws LightnerModelException {
		super(data, title);
		try {
			question = ImageIO.read(new ByteArrayInputStream(read("question")));
			answer = ImageIO.read(new ByteArrayInputStream(read("answer")));
		} catch (final IOException e) {
			throw new LightnerModelException(e);
		}
	}

	public BufferedImage getQuestion() {
		return question;
	}

	public void setQuestion(final BufferedImage question, final ImageType type) throws LightnerModelException {
		this.question = question;
		final ByteArrayOutputStream o = new ByteArrayOutputStream();
		try {
			ImageIO.write(question, type.getSuffix(), o);
		} catch (final IOException e) {
			throw new LightnerModelException(e);
		}
		write("question." + type.getSuffix(), o.toByteArray());
	}

	public BufferedImage getAnswer() {
		return answer;
	}

	public void setAnswer(final BufferedImage answer, final ImageType type) throws LightnerModelException {
		this.answer = answer;
		final ByteArrayOutputStream o = new ByteArrayOutputStream();
		try {
			ImageIO.write(answer, type.getSuffix(), o);
		} catch (final IOException e) {
			throw new LightnerModelException(e);
		}
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
