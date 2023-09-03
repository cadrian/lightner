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
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import net.cadrian.lightner.model.ContentAudio;
import net.cadrian.lightner.model.ContentImage;
import net.cadrian.lightner.model.ContentText;
import net.cadrian.lightner.model.LightnerBox;
import net.cadrian.lightner.model.LightnerCard;
import net.cadrian.lightner.model.LightnerCardContent;
import net.cadrian.lightner.model.LightnerCardContent.Type;

class LightnerBoxes extends JPanel {

	private static final long serialVersionUID = -7213515056863712630L;
	private static final Logger logger = Logger.getLogger(LightnerBoxes.class.getName());

	private static final boolean loadEmoji;
	static {
		// https://bugs.openjdk.org/browse/JDK-8269806
		loadEmoji = false;
	}
	private static final Font emojiFont;

	private final transient Content content = new Content();
	private final transient LightnerBox box;

	private final JButton previous;
	private final JButton next;
	private final JButton check;
	private final JButton fail;
	private final JButton edit;
	private final JButton add;
	private final JButton delete;
	private final JPanel cards;

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
			logger.info(() -> "Not loading emoji font");
			emojiFont = new JLabel().getFont().deriveFont(24f);
		}
	}

	LightnerBoxes(final LightnerBox box) throws IOException {
		super(new BorderLayout());

		this.box = box;

		cards = new JPanel(new CardLayout());
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

		add.addActionListener(this::addCard);
		next.addActionListener(this::nextCard);
		previous.addActionListener(this::previousCard);

		final List<LightnerCard> theCards = new ArrayList<>(box.getCards());
		Collections.shuffle(theCards, new SecureRandom());
		for (final LightnerCard aCard : theCards) {
			content.add(aCard);
		}
	}

	private void nextCard(final ActionEvent ae) {
		content.next();
	}

	private void previousCard(final ActionEvent ae) {
		content.previous();
	}

	private void addCard(final ActionEvent ae) {
		// TODO Auto-generated method stub
		try {
			final UUID id = UUID.randomUUID();
			final LightnerCard card = box.newCard("test:" + id, Type.TEXT);
			final ContentText text = (ContentText) card.getContent();
			text.setQuestion("Question: " + id);
			text.setAnswer("Answer: " + id);
			content.add(card);
		} catch (final IOException e) {
			logger.log(Level.SEVERE, e, () -> "Could not add card");
		}
	}

	private final class Content implements LightnerCardContent.Visitor {

		private final List<LightnerCard> cardsList = new ArrayList<>();
		private int currentIndex = 0;

		private String tmpname;

		void add(final LightnerCard card) {
			tmpname = card.getName();
			logger.info(() -> "Adding card: " + tmpname);
			currentIndex = cardsList.size();
			cardsList.add(card);
			card.getContent().accept(this);
			showCard();
		}

		LightnerCard showCard() {
			final LightnerCard card = get();
			if (card != null) {
				final String name = card.getName();
				logger.info(() -> "Showing card: " + name);
				final CardLayout cl = (CardLayout) (cards.getLayout());
				cl.show(cards, name);
				cards.revalidate();
			}
			return card;
		}

		LightnerCard get() {
			logger.info(() -> "currentIndex=" + currentIndex);
			if (cardsList.isEmpty()) {
				return null;
			}
			return cardsList.get(currentIndex);
		}

		LightnerCard next() {
			if (++currentIndex >= cardsList.size()) {
				currentIndex = 0;
			}
			return showCard();
		}

		LightnerCard previous() {
			if (--currentIndex < 0) {
				currentIndex = Math.max(0, cardsList.size() - 1);
			}
			return showCard();
		}

		@Override
		public void visitText(final ContentText t) {
			final JContentText text = new JContentText(t);
			cards.add(text, tmpname);
		}

		@Override
		public void visitImage(final ContentImage i) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visitAudio(final ContentAudio a) {
			// TODO Auto-generated method stub

		}
	}

}
