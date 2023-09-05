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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.cadrian.lightner.model.content.link.ContentLink;

class JContentLink extends JPanel {

	private static final long serialVersionUID = 8604811393806569292L;
	private static final Logger logger = Logger.getLogger(JContentLink.class.getName());

	public JContentLink(final ContentLink link) {
		super(new BorderLayout());

		final URI uri = link.getLink();
		final JLabel thelink = new JLabel(uri.toString());
		final Font font = thelink.getFont();
		@SuppressWarnings("unchecked")
		final Map<TextAttribute, Object> attributes = (Map<TextAttribute, Object>) font.getAttributes();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		thelink.setFont(font.deriveFont(attributes));
		thelink.setForeground(Color.blue);
		thelink.setHorizontalTextPosition(SwingConstants.CENTER);
		thelink.setVerticalAlignment(SwingConstants.CENTER);

		final JPanel linkPane = new JPanel(new GridBagLayout());
		linkPane.add(thelink, new GridBagConstraints());

		add(linkPane, BorderLayout.CENTER);

		thelink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		thelink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent me) {
				if (Desktop.isDesktopSupported()) {
					final Desktop desktop = Desktop.getDesktop();
					if (desktop.isSupported(Desktop.Action.BROWSE)) {
						try {
							desktop.browse(uri);
						} catch (final IOException e) {
							logger.log(Level.SEVERE, e, () -> "Could not open: " + uri);
						}
					} else {
						logger.info(() -> "Desktop browse action not supported: " + uri);
					}
				} else {
					logger.info(() -> "Desktop not supported: " + uri);
				}
			}
		});

	}

}
