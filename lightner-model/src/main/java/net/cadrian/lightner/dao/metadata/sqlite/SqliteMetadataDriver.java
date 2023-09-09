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
package net.cadrian.lightner.dao.metadata.sqlite;

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

import net.cadrian.lightner.dao.LightnerContentDriver;
import net.cadrian.lightner.dao.LightnerDataCard;
import net.cadrian.lightner.dao.LightnerDataException;
import net.cadrian.lightner.dao.metadata.AbstractMetadataDriver;

public class SqliteMetadataDriver extends AbstractMetadataDriver {

	private static final Logger logger = Logger.getLogger(SqliteMetadataDriver.class.getName());

	private final String url;

	public SqliteMetadataDriver(final File root, final LightnerContentDriver contentDriver)
			throws LightnerDataException {
		super(contentDriver);
		if (root.exists() && !root.isDirectory()) {
			throw new LightnerDataException(root + " already exists and is not a directory");
		}
		root.mkdirs();
		url = String.format("jdbc:sqlite:%s/metadata.db", root.getPath());
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
				PreparedStatement stmt = cnx.prepareStatement("select ID from CARD where BOX=?");
				PreparedStatement delstmt = cnx.prepareStatement("delete from CARD where ID=?");) {
			stmt.setInt(1, box);
			final ResultSet set = stmt.executeQuery();
			while (set.next()) {
				final String id = set.getString("ID");
				final LightnerDataCard card = contentDriver.getCard(id);
				if (card != null) {
					result.add(card);
				} else {
					// remove stale reference to missing card
					delstmt.setString(1, id);
					delstmt.executeUpdate();
				}
			}
		} catch (final SQLException e) {
			throw new LightnerDataException("Could not list cards", e);
		}
		return result;
	}

	@Override
	public LightnerDataCard createCard(final String name, final int box) throws LightnerDataException {
		final LightnerDataCard result = contentDriver.createCard(name);
		try (Connection cnx = DriverManager.getConnection(url);
				PreparedStatement stmt = cnx.prepareStatement("insert into CARD (ID, BOX) values (?, ?)")) {
			stmt.setString(1, name);
			stmt.setInt(2, box);
			final int n = stmt.executeUpdate();
			if (n != 1) {
				throw new LightnerDataException("Card was not created: " + name);
			}
		} catch (final SQLException e) {
			throw new LightnerDataException("Could not update database to create card: " + name, e);
		}
		return result;
	}

	@Override
	public void moveCard(final LightnerDataCard card, final int fromBox, final int toBox) throws LightnerDataException {
		try (Connection cnx = DriverManager.getConnection(url);
				PreparedStatement stmt = cnx.prepareStatement("update CARD set BOX=? where ID=? and BOX=?")) {
			stmt.setInt(1, toBox);
			stmt.setString(2, card.getName());
			stmt.setInt(3, fromBox);
			final int n = stmt.executeUpdate();
			if (n != 1) {
				logger.severe(() -> "Card was not updated: " + card.getName());
				throw new LightnerDataException("Could not move card");
			}
		} catch (final SQLException e) {
			logger.log(Level.SEVERE, e, () -> "Could not update database");
			throw new LightnerDataException("Could not move card", e);
		}
	}

}
