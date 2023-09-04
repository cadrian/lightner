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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

class JContentLinkDialog extends JDialog {

	private static final long serialVersionUID = 5169348014757965019L;

	private final JButton validate;
	private final JButton cancel;

	@FunctionalInterface
	interface Creator {
		void create(UUID id, String link);
	}

	public JContentLinkDialog(final Lightner owner, final Creator creator) {
		super(owner);

		final UUID id = UUID.randomUUID();

		final JPanel contentPane = new JPanel(new BorderLayout());
		setContentPane(contentPane);

		final JTextField link = new JTextField();
		final JPanel linkPane = new JPanel(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		linkPane.add(link, c);
		contentPane.add(linkPane, BorderLayout.CENTER);

		final JToolBar tools = new JToolBar(SwingConstants.HORIZONTAL);
		tools.setFloatable(false);
		tools.setAlignmentX(CENTER_ALIGNMENT);
		validate = new JButton(" ✅ ");
		validate.setFont(LightnerBoxes.emojiFont);
		validate.setToolTipText("Good answer");
		cancel = new JButton(" ❌ ");
		cancel.setFont(LightnerBoxes.emojiFont);
		cancel.setToolTipText("Wrong answer");
		tools.add(validate);
		tools.add(cancel);
		contentPane.add(tools, BorderLayout.SOUTH);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		cancel.addActionListener(ae0 -> setVisible(false));
		validate.addActionListener(ae0 -> {
			setVisible(false);
			creator.create(id, link.getText());
		});

		setTitle("Text: " + id);
		setMinimumSize(new Dimension(640, 480));
		pack();
		setModal(true);
		setLocationRelativeTo(owner);
	}

}
