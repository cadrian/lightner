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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.UUID;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

class JContentTextDialog extends AbstractContentDialog {

	private static final long serialVersionUID = 5169348014757965019L;

	@FunctionalInterface
	interface Creator {
		void create(UUID id, String question, String answer);
	}

	public JContentTextDialog(final Lightner owner, final Creator creator) {
		super(owner);

		final UUID id = UUID.randomUUID();

		final JPanel contentPane = new JPanel(new BorderLayout());
		setContentPane(contentPane);

		final JPanel inputPane = new JPanel(new GridLayout(2, 1));
		final JTextArea question = new JTextArea();
		final JTextArea answer = new JTextArea();

		final JPanel questionPane = new JPanel(new BorderLayout());
		questionPane.add(new JLabel("Question"), BorderLayout.NORTH);
		questionPane.add(new JScrollPane(question), BorderLayout.CENTER);

		final JPanel answerPane = new JPanel(new BorderLayout());
		answerPane.add(new JLabel("Answer"), BorderLayout.NORTH);
		answerPane.add(new JScrollPane(answer), BorderLayout.CENTER);

		inputPane.add(questionPane);
		inputPane.add(answerPane);
		contentPane.add(inputPane, BorderLayout.CENTER);

		contentPane.add(tools, BorderLayout.SOUTH);

		validate.addActionListener(ae0 -> {
			setVisible(false);
			creator.create(id, question.getText(), answer.getText());
		});

		setTitle("Text: " + id);
		pack();
	}

}
