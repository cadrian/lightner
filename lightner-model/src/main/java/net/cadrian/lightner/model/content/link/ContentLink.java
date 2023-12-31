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
package net.cadrian.lightner.model.content.link;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import net.cadrian.lightner.dao.LightnerDataCard;
import net.cadrian.lightner.model.LightnerModelException;
import net.cadrian.lightner.model.content.AbstractLightnerCardContent;

public class ContentLink extends AbstractLightnerCardContent {

	private static final Set<String> SUFFIXES = Collections.singleton(".lnk");

	private URI link;

	public ContentLink(final LightnerDataCard data, final String title) throws LightnerModelException {
		super(data, title);
		try {
			link = new URI(new String(read("link"), StandardCharsets.UTF_8));
		} catch (final URISyntaxException e) {
			throw new LightnerModelException(e);
		}
	}

	public void setLink(final String link) throws LightnerModelException {
		try {
			this.link = new URI(link);
		} catch (final URISyntaxException e) {
			throw new LightnerModelException(e);
		}
		write("link.lnk", link.getBytes(StandardCharsets.UTF_8));
	}

	public URI getLink() {
		return link;
	}

	@Override
	public void accept(final Visitor v) {
		v.visitLink(this);
	}

	@Override
	public Collection<String> getFileSuffixes() {
		return SUFFIXES;
	}

}
