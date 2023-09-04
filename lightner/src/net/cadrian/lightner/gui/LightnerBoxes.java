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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import net.cadrian.lightner.model.ContentAudio;
import net.cadrian.lightner.model.ContentImage;
import net.cadrian.lightner.model.ContentText;
import net.cadrian.lightner.model.LightnerBox;
import net.cadrian.lightner.model.LightnerCard;
import net.cadrian.lightner.model.LightnerCardContent;

class LightnerBoxes extends JPanel {

	private static final long serialVersionUID = -7213515056863712630L;
	private static final Logger logger = Logger.getLogger(LightnerBoxes.class.getName());

	private static final boolean loadEmoji;
	static {
		// https://bugs.openjdk.org/browse/JDK-8269806
		loadEmoji = false;
	}
	static final Font emojiFont;

	private final Lightner owner;
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

	LightnerBoxes(final Lightner owner, final LightnerBox box) throws IOException {
		super(new BorderLayout());

		this.owner = owner;
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

		final JPopupMenu addMenu = new JPopupMenu("Add");
		final JMenuItem addText = new JMenuItem("Text");
		final JMenuItem addImage = new JMenuItem("Image");
		final JMenuItem addAudio = new JMenuItem("Audio");
		addMenu.add(addText);
		addMenu.add(addImage);
		addMenu.add(addAudio);

		addText.addActionListener(this::addTextCard);

		add.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				addMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		});

		next.addActionListener(this::nextCard);
		previous.addActionListener(this::previousCard);
		check.addActionListener(this::validateCard);
		fail.addActionListener(this::failCard);

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

	private void validateCard(final ActionEvent ae) {
		final LightnerCard card = content.get();
		if (card != null) {
			try {
				final int boxNumber = card.getBoxNumber();
				final boolean updated;
				if (boxNumber == 7) {
					updated = card.update(boxNumber, "Card checked");
				} else {
					updated = card.update(boxNumber + 1, "Card checked");
				}
				if (updated) {
					logger.severe(() -> "Checked card " + card.getName());
					content.hide();
				} else {
					logger.severe(() -> "Could not check card " + card.getName());
				}
			} catch (final IOException e) {
				logger.log(Level.SEVERE, e, () -> "Could not check card " + card.getName());
			}
		}
	}

	private void failCard(final ActionEvent ae) {
		final LightnerCard card = content.get();
		if (card != null) {
			try {
				final boolean updated = card.update(1, "Card failed");
				if (updated) {
					logger.severe(() -> "Failed card " + card.getName());
					content.hide();
				} else {
					logger.severe(() -> "Could not fail card " + card.getName());
				}
			} catch (final IOException e) {
				logger.log(Level.SEVERE, e, () -> "Could not fail card " + card.getName());
			}
		}
	}

	private void addTextCard(final ActionEvent ae) {
		final JContentTextDialog dialog = new JContentTextDialog(owner, (id, q, a) -> {
			try {
				final LightnerCard card = box.newCard(id.toString(), LightnerCardContent.Type.TEXT);
				final ContentText text = (ContentText) card.<String>getContent();
				text.setQuestion(q);
				text.setAnswer(a);
				content.add(card);
			} catch (final IOException e) {
				logger.log(Level.SEVERE, e, () -> "Failed to create text card: " + id);
			}
		});
		dialog.setVisible(true);
	}

	private final class Content {

		private final List<LightnerCard> cardsList = new ArrayList<>();
		private final Map<LightnerCard, JComponent> cardsMap = new HashMap<>();
		private final JCardCreator visitor = new JCardCreator();
		private int currentIndex = 0;

		private static class JCardCreator implements LightnerCardContent.Visitor {

			JComponent createdCard;

			@Override
			public void visitText(final ContentText t) {
				createdCard = new JContentText(t);
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

		void add(final LightnerCard card) {
			final String cardName = card.getName();
			logger.info(() -> "Adding card: " + cardName);
			currentIndex = cardsList.size();
			card.getContent().accept(visitor);
			if (visitor.createdCard != null) {
				cardsList.add(card);
				cardsMap.put(card, visitor.createdCard);
				cards.add(visitor.createdCard, cardName);
				showCard();
			}
		}

		/**
		 * @return the hidden card, or <code>null</code> if none were hidden
		 */
		LightnerCard hide() {
			// Remove from current deck only, just not to show the card again
			final LightnerCard card;
			if (cardsList.isEmpty()) {
				card = null;
			} else {
				card = cardsList.remove(currentIndex);
				cards.remove(cardsMap.remove(card));
				if (currentIndex >= cardsList.size()) {
					currentIndex = 0;
				}
				showCard();
			}
			return card;
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

	}

}
