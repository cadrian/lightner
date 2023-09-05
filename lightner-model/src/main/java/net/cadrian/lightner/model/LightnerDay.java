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

public class LightnerDay {

	private static final int[][] BOXES = { { 1, 2 }, { 1, 3 }, { 1, 2 }, { 1, 4 }, { 1, 2 }, { 1, 3 }, { 1, 2 }, { 1 },
			{ 1, 2 }, { 1, 3 }, { 1, 2 }, { 1, 5 }, { 1, 2, 4 }, { 1, 3 }, { 1, 2 }, { 1 }, { 1, 2 }, { 1, 3 },
			{ 1, 2 }, { 1, 4 }, { 1, 2 }, { 1, 3 }, { 1, 2 }, { 1, 6 }, { 1, 2 }, { 1, 3 }, { 1, 2 }, { 1, 5 },
			{ 1, 2, 4 }, { 1, 3 }, { 1, 2 }, { 1 }, { 1, 2 }, { 1, 3 }, { 1, 2 }, { 1, 4 }, { 1, 2 }, { 1, 3 },
			{ 1, 2 }, { 1 }, { 1, 2 }, { 1, 3 }, { 1, 2 }, { 1, 4, 5 }, { 1, 2 }, { 1, 3 }, { 1, 2 }, { 1 }, { 1, 2 },
			{ 1, 3 }, { 1, 2 }, { 1, 4 }, { 1, 2 }, { 1, 3 }, { 1, 2 }, { 1, 7 }, { 1, 2 }, { 1, 3 }, { 1, 2, 6 },
			{ 1, 5 }, { 1, 2, 4 }, { 1, 3 }, { 1, 2 }, { 1 } };

	private final LightnerDate date;
	private final int day0;

	public LightnerDay() {
		date = new LightnerDate();
		day0 = date.getDate() % 64;
	}

	public LightnerDate getDate() {
		return date;
	}

	public int getDay() {
		return day0 + 1;
	}

	public int[] getBoxes() {
		return BOXES[day0];
	}

}
