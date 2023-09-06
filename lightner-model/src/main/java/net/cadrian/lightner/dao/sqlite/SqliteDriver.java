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
package net.cadrian.lightner.dao.sqlite;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.cadrian.lightner.dao.LightnerDataCard;
import net.cadrian.lightner.dao.LightnerDataDriver;
import net.cadrian.lightner.dao.LightnerDataException;

public class SqliteDriver implements LightnerDataDriver {

	private static final Logger logger = Logger.getLogger(SqliteDriver.class.getName());

	private final File cards;
	private final String url;

	public SqliteDriver(final File root) throws LightnerDataException {
		url = String.format("jdbc:sqlite:%s/metadata.db", root.getPath());
		cards = new File(root, "cards");
		if (!root.isDirectory()) {
			if (root.exists()) {
				throw new LightnerDataException(root + " already exists and is not a directory");
			}
			cards.mkdirs();
		}
		initializeDatabase();
	}

	private void initializeDatabase() throws LightnerDataException {
		try (Connection cnx = DriverManager.getConnection(url)) {
			if (cnx != null) {
				final DatabaseMetaData meta = cnx.getMetaData();
				logger.info(() -> {
					try {
						return "The driver name is " + meta.getDriverName();
					} catch (final SQLException e) {
						return "The driver name is unknown";
					}
				});

				try (Statement stmt = cnx.createStatement()) {
					stmt.execute("create table if not exists CARD (ID varchar primary key, BOX integer not null);");
					stmt.execute("create index if not exists CARD_BOX_IDX on CARD (BOX);");
				}

				logger.info(() -> "A new database has been created.");
			}
		} catch (final SQLException e) {
			throw new LightnerDataException("Could not create database: " + url, e);
		}
	}

	@Override
	public Collection<LightnerDataCard> listCards(final int box) throws LightnerDataException {
		final List<LightnerDataCard> result = new ArrayList<>();
		try (Connection cnx = DriverManager.getConnection(url);
				PreparedStatement stmt = cnx.prepareStatement("select ID from CARD where BOX=?")) {
			stmt.setInt(1, box);
			final ResultSet set = stmt.executeQuery();
			while (set.next()) {
				final String id = set.getString("ID");
				result.add(new CardSqlite(new File(cards, id)));
			}
		} catch (final SQLException e) {
			throw new LightnerDataException("Could not list cards", e);
		}
		return result;
	}

	@Override
	public LightnerDataCard createCard(final String name, final int box) throws LightnerDataException {
		final File f = new File(cards, name);
		if (!f.mkdir()) {
			final String msg = "Could not create " + f.getPath();
			logger.severe(msg);
			throw new LightnerDataException(msg);
		}
		try (Connection cnx = DriverManager.getConnection(url);
				PreparedStatement stmt = cnx.prepareStatement("insert into BOX (ID, BOX) values (?, ?)")) {
			stmt.setString(1, name);
			stmt.setInt(2, box);
			final int n = stmt.executeUpdate();
			if (n != 1) {
				throw new LightnerDataException("Card was not created: " + name);
			}
		} catch (final SQLException e) {
			throw new LightnerDataException("Could not update database to create card: " + name, e);
		}
		return new CardSqlite(f);
	}

	@Override
	public boolean moveCard(final LightnerDataCard card, final int fromBox, final int toBox) {
		try (Connection cnx = DriverManager.getConnection(url);
				PreparedStatement stmt = cnx.prepareStatement("update CARD set BOX=? where ID=? and BOX=?")) {
			stmt.setInt(1, toBox);
			stmt.setString(2, card.getName());
			stmt.setInt(3, fromBox);
			final int n = stmt.executeUpdate();
			if (n != 1) {
				logger.severe(() -> "Card was not updated: " + card.getName());
				return false;
			}
			return true;
		} catch (final SQLException e) {
			logger.log(Level.SEVERE, e, () -> "Could not update database");
			return false;
		}
	}

}
