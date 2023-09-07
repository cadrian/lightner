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
package net.cadrian.lightner.model.content.text;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import net.cadrian.lightner.dao.LightnerDataCard;
import net.cadrian.lightner.model.content.AbstractLightnerCardContent;

public class ContentText extends AbstractLightnerCardContent {

	private static final Set<String> SUFFIXES = Collections.singleton(".txt");

	private String question;
	private String answer;

	public ContentText(final LightnerDataCard file, final String title) throws IOException {
		super(file, title);
		question = new String(read("question"), StandardCharsets.UTF_8);
		answer = new String(read("answer"), StandardCharsets.UTF_8);
	}

	public void setQuestion(final String question) throws IOException {
		this.question = question;
		write("question.txt", question.getBytes(StandardCharsets.UTF_8));
	}

	public void setAnswer(final String answer) throws IOException {
		this.answer = answer;
		write("answer.txt", answer.getBytes(StandardCharsets.UTF_8));
	}

	public String getQuestion() {
		return question;
	}

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

}
