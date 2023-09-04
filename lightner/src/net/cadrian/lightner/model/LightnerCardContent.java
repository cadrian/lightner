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

public interface LightnerCardContent {

	public enum Type {
		TEXT {
			@Override
			ContentText getContent(final File file) throws IOException {
				return new ContentText(file);
			}
		},
		LINK {
			@Override
			ContentLink getContent(final File file) throws IOException {
				return new ContentLink(file);
			}
		},
		IMAGE {
			@Override
			ContentImage getContent(final File file) throws IOException {
				return new ContentImage(file);
			}
		},
		AUDIO {
			@Override
			ContentAudio getContent(final File file) throws IOException {
				return new ContentAudio(file);
			}
		},
		VIDEO {
			@Override
			ContentVideo getContent(final File file) throws IOException {
				return new ContentVideo(file);
			}
		};

		abstract LightnerCardContent getContent(File file) throws IOException;
	}

	interface Visitor {
		void visitText(ContentText t);

		void visitLink(ContentLink l);

		void visitImage(ContentImage i);

		void visitAudio(ContentAudio a);

		void visitVideo(ContentVideo v);
	}

	Collection<String> getFileSuffixes();

	void accept(Visitor v);
}
