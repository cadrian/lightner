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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class ContentText extends AbstractLightnerCardContent {

	private static final Set<String> SUFFIXES = Collections.singleton(".txt");

	private final File file;

	private String question;
	private String answer;

	ContentText(final File file) throws IOException {
		this.file = file;
		question = read(file, "question.txt");
		answer = read(file, "answer.txt");
	}

	public void setQuestion(final String question) throws IOException {
		this.question = question;
		write(file, "question.txt", question);
	}

	public void setAnswer(final String answer) throws IOException {
		this.answer = answer;
		write(file, "answer.txt", answer);
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
