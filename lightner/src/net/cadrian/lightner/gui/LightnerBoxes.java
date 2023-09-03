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
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import net.cadrian.lightner.model.LightnerBox;

class LightnerBoxes extends JPanel {

	private static final long serialVersionUID = -7213515056863712630L;
	private static final Logger logger = Logger.getLogger(LightnerBoxes.class.getName());

	private static final boolean loadEmoji;
	static {
		// https://bugs.openjdk.org/browse/JDK-8269806
		loadEmoji = false;
	}
	private static final Font emojiFont;

	private final JButton previous;
	private final JButton next;
	private final JButton check;
	private final JButton fail;
	private final JButton edit;
	private final JButton add;
	private final JButton delete;

	static {
		if (loadEmoji) {
			final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

			Font font = null;
			try (InputStream in = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("net/cadrian/lightner/rc/fonts/NotoColorEmoji-Regular.ttf")) {
				if (in != null) {
					logger.info("Loading emoji font…");
					font = Font.createFont(Font.TRUETYPE_FONT, in).deriveFont(Font.PLAIN, 48f);
					ge.registerFont(font);

				}
			} catch (FontFormatException | IOException e) {
				e.printStackTrace();
			}
			emojiFont = font;
		} else {
			logger.info("Not loading emoji font");
			emojiFont = new JLabel().getFont().deriveFont(24f);
		}
	}

	LightnerBoxes(final LightnerBox box) {
		super(new BorderLayout());

		final JPanel cards = new JPanel(new CardLayout());
		add(cards, BorderLayout.CENTER);

		final JToolBar tools = new JToolBar();
		tools.setFloatable(false);
		tools.setAlignmentX(CENTER_ALIGNMENT);
		add(tools, BorderLayout.SOUTH);

		previous = new JButton(" ◀️ ");
		previous.setFont(emojiFont);
		previous.setToolTipText("Previous");
		next = new JButton(" ▶️ ");
		next.setFont(emojiFont);
		next.setToolTipText("Next");
		check = new JButton(" ✅ ");
		check.setFont(emojiFont);
		check.setToolTipText("Good answer");
		fail = new JButton(" ❌ ");
		fail.setFont(emojiFont);
		fail.setToolTipText("Wrong answer");
		edit = new JButton(" ✏️ ");
		edit.setFont(emojiFont);
		edit.setToolTipText("Edit");
		add = new JButton(" ➕ ");
		add.setFont(emojiFont);
		add.setToolTipText("Add");
		delete = new JButton(" ➖ ");
		delete.setFont(emojiFont);
		delete.setToolTipText("Delete");

		tools.add(previous);
		tools.add(next);
		tools.addSeparator();
		tools.add(check);
		tools.add(fail);
		tools.addSeparator();
		tools.add(edit);
		tools.add(add);
		tools.add(delete);
	}

}
