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
package net.cadrian.lightner.gui;

import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.ImageIcon;

public enum LightnerIcon {
	ABOUT, AUDIO_PAUSE, AUDIO_PLAY, AUDIO_STOP, BROWSE, CARD_ADD, CARD_CHECK, CARD_DELETE, CARD_EDIT, CARD_FAIL,
	CARD_NEXT, CARD_PREVIOUS, EDIT_CANCEL, EDIT_COMMIT, TYPE_AUDIO, TYPE_IMAGE, TYPE_LINK, TYPE_TEXT, TYPE_VIDEO;

	private final AtomicReference<ImageIcon> icon = new AtomicReference<>();

	public ImageIcon getIcon() {
		return icon.accumulateAndGet(null, (prev, x) -> {
			return prev == null ? createImageIcon(this) : prev;
		});
	}

	private static ImageIcon createImageIcon(final LightnerIcon icon) {
		final URL iconUrl = Thread.currentThread().getContextClassLoader()
				.getResource("net/cadrian/lightner/gui/icons/adwaita/" + icon + ".png");
		return new ImageIcon(iconUrl);
	}

}
