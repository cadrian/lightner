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
package net.cadrian.lightner.model.content.audio;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.cadrian.lightner.dao.LightnerDataCard;
import net.cadrian.lightner.dao.LightnerDataContent;
import net.cadrian.lightner.model.LightnerModelException;
import net.cadrian.lightner.model.content.AbstractLightnerCardContent;

public class ContentAudio extends AbstractLightnerCardContent {

	private static final Set<String> SUFFIXES = Collections
			.unmodifiableSet(new HashSet<>(Arrays.asList(".wav", ".mp3", ".ogg")));

	private AudioContainer question;
	private AudioContainer answer;

	public ContentAudio(final LightnerDataCard data, final String title) throws LightnerModelException {
		super(data, title);
		final LightnerDataContent questionContent = getContent("question");
		question = questionContent == null ? null : new AudioContainerContent(questionContent);
		final LightnerDataContent answerContent = getContent("answer");
		answer = answerContent == null ? null : new AudioContainerContent(answerContent);
	}

	@Override
	public void accept(final Visitor v) {
		v.visitAudio(this);
	}

	@Override
	public Collection<String> getFileSuffixes() {
		return SUFFIXES;
	}

	public AudioContainer getQuestion() {
		return question;
	}

	public void setQuestion(final AudioContainer question) throws LightnerModelException {
		this.question = new AudioContainerContent(
				write("question." + question.getType().getSuffix(), question.getAudioBytes()));
	}

	public AudioContainer getAnswer() {
		return answer;
	}

	public void setAnswer(final AudioContainer answer) throws LightnerModelException {
		this.answer = new AudioContainerContent(
				write("answer." + answer.getType().getSuffix(), answer.getAudioBytes()));
	}

}
