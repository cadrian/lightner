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

import java.awt.Font;

import javax.swing.JLabel;

import net.cadrian.lightner.model.LightnerDay;

class LightnerDayLabel extends JLabel {

	private static final long serialVersionUID = 8442051196444294133L;

	LightnerDayLabel(final LightnerDay day) {
		setText(buildText(day.getDate(), day.getDay(), day.getBoxes()));
		final Font f = getFont();
		setFont(f.deriveFont(f.getStyle() | Font.BOLD));
	}

	private static String buildText(final String date, final int day, final int[] boxes) {
		final StringBuilder result = new StringBuilder(date).append(" - Day ").append(day).append("/64 - Boxes: ");
		for (final int box : boxes) {
			if (box > 1) {
				result.append(", ");
			}
			result.append(box);
		}
		return result.toString();
	}

}
