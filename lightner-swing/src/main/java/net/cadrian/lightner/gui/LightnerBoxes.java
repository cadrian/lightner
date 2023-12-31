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
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
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

import net.cadrian.lightner.face.LightnerFaceException;
import net.cadrian.lightner.model.LightnerBox;
import net.cadrian.lightner.model.LightnerCard;
import net.cadrian.lightner.model.LightnerCardContent;
import net.cadrian.lightner.model.LightnerCardType;
import net.cadrian.lightner.model.LightnerModelException;
import net.cadrian.lightner.model.content.audio.ContentAudio;
import net.cadrian.lightner.model.content.image.ContentImage;
import net.cadrian.lightner.model.content.link.ContentLink;
import net.cadrian.lightner.model.content.text.ContentText;
import net.cadrian.lightner.model.content.video.ContentVideo;

class LightnerBoxes extends JPanel {

	private static final long serialVersionUID = -7213515056863712630L;
	private static final Logger logger = Logger.getLogger(LightnerBoxes.class.getName());

	private final Lightner owner;
	private final transient Content content = new Content();
	private final transient LightnerBox box;

	private final JLabel cardDescription;
	private final JButton previous;
	private final JButton next;
	private final JButton check;
	private final JButton fail;
	private final JButton edit;
	private final JButton add;
	private final JButton delete;
	private final JButton about;
	private final JPanel cards;

	LightnerBoxes(final Lightner owner, final LightnerBox box) throws LightnerFaceException {
		super(new BorderLayout());

		this.owner = owner;
		this.box = box;

		cardDescription = new JLabel();
		add(cardDescription, BorderLayout.NORTH);

		cards = new JPanel(new CardLayout());
		add(cards, BorderLayout.CENTER);

		final JToolBar tools = new JToolBar();
		tools.setFloatable(false);
		tools.setAlignmentX(CENTER_ALIGNMENT);
		add(tools, BorderLayout.SOUTH);

		previous = new JButton(LightnerIcon.CARD_PREVIOUS.getIcon());
		previous.setToolTipText("Previous");
		next = new JButton(LightnerIcon.CARD_NEXT.getIcon());
		next.setToolTipText("Next");
		check = new JButton(LightnerIcon.CARD_CHECK.getIcon());
		check.setToolTipText("Good answer");
		fail = new JButton(LightnerIcon.CARD_FAIL.getIcon());
		fail.setToolTipText("Wrong answer");
		edit = new JButton(LightnerIcon.CARD_EDIT.getIcon());
		edit.setToolTipText("Edit");
		add = new JButton(LightnerIcon.CARD_ADD.getIcon());
		add.setToolTipText("Add");
		delete = new JButton(LightnerIcon.CARD_DELETE.getIcon());
		delete.setToolTipText("Delete");
		about = new JButton(LightnerIcon.ABOUT.getIcon());
		about.setToolTipText("About Lightner");

		tools.add(previous);
		tools.add(next);
		tools.addSeparator();
		tools.add(check);
		tools.add(fail);
		tools.addSeparator();
		tools.add(edit);
		tools.add(add);
		tools.add(delete);
		tools.addSeparator();
		tools.add(about);

		final JPopupMenu addMenu = new JPopupMenu("Add");
		final JMenuItem addText = new JMenuItem("  Text", LightnerIcon.TYPE_TEXT.getIcon());
		final JMenuItem addLink = new JMenuItem("  Link", LightnerIcon.TYPE_LINK.getIcon());
		final JMenuItem addImage = new JMenuItem("  Image", LightnerIcon.TYPE_IMAGE.getIcon());
		final JMenuItem addAudio = new JMenuItem("  Audio", LightnerIcon.TYPE_AUDIO.getIcon());
		final JMenuItem addVideo = new JMenuItem("  Video", LightnerIcon.TYPE_VIDEO.getIcon());
		addVideo.setEnabled(false);// TODO
		addMenu.add(addText);
		addMenu.add(addLink);
		addMenu.add(addImage);
		addMenu.add(addAudio);
		addMenu.add(addVideo);

		addText.addActionListener(this::addTextCard);
		addLink.addActionListener(this::addLinkCard);
		addImage.addActionListener(this::addImageCard);
		addAudio.addActionListener(this::addAudioCard);
		// TODO addVideo.addActionListener(this::addVideoCard);

		// note: archive, don't delete!
		// TODO delete.addActionListener(this::deleteCard);

		about.addActionListener(this::about);

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
		try {
			final List<LightnerCard> theCards = new ArrayList<>(box.getCards());
			Collections.shuffle(theCards, new SecureRandom());
			for (final LightnerCard aCard : theCards) {
				content.add(aCard);
			}
		} catch (final LightnerModelException e) {
			throw new LightnerFaceException(e);
		}
	}

	private void about(final ActionEvent ae) {
		new JAboutDialog(owner).setVisible(true);
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
			final int boxNumber = card.getBoxNumber();
			try {
				if (boxNumber == 7) {
					card.update(boxNumber, "Card succeded");
				} else {
					card.update(boxNumber + 1, "Card succeded");
				}
				logger.info(() -> "Checked card " + card.getName());
				content.hide();
			} catch (LightnerModelException e) {
				logger.log(Level.SEVERE, e, () -> "Could not check card " + card.getName());
			}
		}
	}

	private void failCard(final ActionEvent ae) {
		final LightnerCard card = content.get();
		if (card != null) {
			try {
				card.update(1, "Card failed");
				logger.info(() -> "Failed card " + card.getName());
				content.hide();
			} catch (LightnerModelException e) {
				logger.log(Level.SEVERE, e, () -> "Could not fail card " + card.getName());
			}
		}
	}

	private void addTextCard(final ActionEvent ae) {
		final JContentTextDialog dialog = new JContentTextDialog(owner, (id, t, q, a) -> {
			try {
				final LightnerCard card = box.newCard(id.toString(), LightnerCardType.TEXT, t);
				final ContentText text = (ContentText) card.getContent();
				text.setQuestion(q);
				text.setAnswer(a);
				content.add(card);
			} catch (final LightnerModelException e) {
				logger.log(Level.SEVERE, e, () -> "Failed to create text card: " + id);
			}
		});
		dialog.setVisible(true);
	}

	private void addLinkCard(final ActionEvent ae) {
		final JContentLinkDialog dialog = new JContentLinkDialog(owner, (id, t, l) -> {
			try {
				final LightnerCard card = box.newCard(id.toString(), LightnerCardType.LINK, t);
				final ContentLink link = (ContentLink) card.getContent();
				link.setLink(l);
				content.add(card);
			} catch (final LightnerModelException e) {
				logger.log(Level.SEVERE, e, () -> "Failed to create link card: " + id);
			}
		});
		dialog.setVisible(true);
	}

	private void addImageCard(final ActionEvent ae) {
		final JContentImageDialog dialog = new JContentImageDialog(owner, (id, t, q, qt, a, at) -> {
			try {
				final LightnerCard card = box.newCard(id.toString(), LightnerCardType.IMAGE, t);
				final ContentImage image = (ContentImage) card.getContent();
				image.setQuestion(q, qt);
				image.setAnswer(a, at);
				content.add(card);
			} catch (final LightnerModelException e) {
				logger.log(Level.SEVERE, e, () -> "Failed to create image card: " + id);
			}
		});
		dialog.setVisible(true);
	}

	private void addAudioCard(final ActionEvent ae) {
		final JContentAudioDialog dialog = new JContentAudioDialog(owner, (id, t, q, a) -> {
			try {
				final LightnerCard card = box.newCard(id.toString(), LightnerCardType.AUDIO, t);
				final ContentAudio audio = (ContentAudio) card.getContent();
				audio.setQuestion(q);
				audio.setAnswer(a);
				content.add(card);
			} catch (final LightnerModelException e) {
				logger.log(Level.SEVERE, e, () -> "Failed to create audio card: " + id);
			}
		});
		dialog.setVisible(true);
	}

	private final class Content {

		private final List<LightnerCard> cardsList = new ArrayList<>();
		private final Map<LightnerCard, JComponent> cardsMap = new HashMap<>();
		private int currentIndex = 0;

		private static class JCardCreator implements LightnerCardContent.Visitor {

			JComponent createdCard;

			@Override
			public void visitText(final ContentText t) {
				createdCard = new JContentText(t);
			}

			@Override
			public void visitLink(final ContentLink l) {
				createdCard = new JContentLink(l);
			}

			@Override
			public void visitImage(final ContentImage i) {
				createdCard = new JContentImage(i);
			}

			@Override
			public void visitAudio(final ContentAudio a) {
				try {
					createdCard = new JContentAudio(a);
				} catch (final IOException e) {
					logger.log(Level.SEVERE, e, () -> "Failed to show audio card");
				}
			}

			@Override
			public void visitVideo(final ContentVideo v) {
				// TODO Auto-generated method stub

			}

		}

		void add(final LightnerCard card) {
			final String cardName = card.getName();
			logger.info(() -> "Adding card: " + cardName);
			currentIndex = cardsList.size();
			final JCardCreator visitor = new JCardCreator();
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
				cardDescription.setText("%s (box %d)".formatted(card.getTitle(), card.getBoxNumber()));
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
