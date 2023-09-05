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

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import net.cadrian.lightner.model.content.text.ContentText;

class JContentText extends JSplitPane {

	private static final long serialVersionUID = -3931635794876357617L;

	public JContentText(final ContentText text) {
		super(JSplitPane.VERTICAL_SPLIT);

		final JTextArea question = new JTextArea(text.getQuestion());
		final JTextArea answer = new JTextArea(text.getAnswer());

		question.setEditable(false);
		answer.setEditable(false);

		setTopComponent(new JScrollPane(question));
		setBottomComponent(new JScrollPane(answer));

		question.setSize(getSize());
		answer.setMinimumSize(new Dimension(0, 0));
		setDividerLocation(1.0);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				setDividerLocation(1.0);
				question.setSize(getSize());
			}
		});
	}

}
