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

import net.cadrian.lightner.dao.LightnerDataCard;
import net.cadrian.lightner.model.content.audio.ContentAudio;
import net.cadrian.lightner.model.content.image.ContentImage;
import net.cadrian.lightner.model.content.link.ContentLink;
import net.cadrian.lightner.model.content.text.ContentText;
import net.cadrian.lightner.model.content.video.ContentVideo;

public enum LightnerCardType {
	TEXT {
		@Override
		ContentText getContent(final LightnerDataCard data, final String title) throws IOException {
			return new ContentText(data, title);
		}
	},
	LINK {
		@Override
		ContentLink getContent(final LightnerDataCard data, final String title) throws IOException {
			return new ContentLink(data, title);
		}
	},
	IMAGE {
		@Override
		ContentImage getContent(final LightnerDataCard data, final String title) throws IOException {
			return new ContentImage(data, title);
		}
	},
	AUDIO {
		@Override
		ContentAudio getContent(final LightnerDataCard data, final String title) throws IOException {
			return new ContentAudio(data, title);
		}
	},
	VIDEO {
		@Override
		ContentVideo getContent(final LightnerDataCard data, final String title) throws IOException {
			return new ContentVideo(data, title);
		}
	};

	abstract LightnerCardContent getContent(LightnerDataCard data, final String title) throws IOException;
}