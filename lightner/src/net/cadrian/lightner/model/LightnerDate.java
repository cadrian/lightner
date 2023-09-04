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

import java.io.Serializable;
import java.util.Calendar;

public class LightnerDate implements Serializable, Comparable<LightnerDate> {

	private static final long serialVersionUID = -8229379414449783552L;

	private final int date;
	private final int year;
	private final int month;
	private final int day;

	public LightnerDate() {
		year = Calendar.getInstance().get(Calendar.YEAR);
		month = Calendar.getInstance().get(Calendar.MONTH) + 1;
		day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		date = year * 10000 + month * 100 + day;
	}

	public LightnerDate(final String dateString) {
		date = Integer.parseInt(dateString);
		year = date / 10000;
		month = (date - year * 10000) / 100;
		day = date % 100;
	}

	public int getDate() {
		return date;
	}

	@Override
	public String toString() {
		return String.format("%04d%02d%02d", year, month, day);
	}

	public String toPrettyString() {
		return String.format("%04d-%02d-%02d", year, month, day);
	}

	@Override
	public int compareTo(final LightnerDate o) {
		return Integer.compare(date, o.date);
	}

	@Override
	public int hashCode() {
		return date;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		final LightnerDate other = (LightnerDate) obj;
		return date == other.date;
	}

}
