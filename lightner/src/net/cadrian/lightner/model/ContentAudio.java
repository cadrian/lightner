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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.sound.sampled.Clip;

class ContentAudio implements LightnerCardContent<Clip> {

	private static final Set<String> SUFFIXES = Collections
			.unmodifiableSet(new HashSet<>(Arrays.asList(".wav", ".mp3", ".ogg")));

	ContentAudio(final File file) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setQuestion(final Clip question) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAnswer(final Clip answer) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public Clip getQuestion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Clip getAnswer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void accept(final Visitor v) {
		v.visitAudio(this);
	}

	@Override
	public Collection<String> getFileSuffixes() {
		return SUFFIXES;
	}

}
