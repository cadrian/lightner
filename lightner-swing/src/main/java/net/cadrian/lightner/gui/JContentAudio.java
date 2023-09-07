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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.cadrian.lightner.model.content.audio.ContentAudio;

class JContentAudio extends JPanel {

	private static final long serialVersionUID = -2132868618296976893L;

	public JContentAudio(final ContentAudio audio) throws IOException {
		super(new GridBagLayout());

		final JAudioClip question = new JAudioClip(audio.getQuestion());
		final JAudioClip answer = new JAudioClip(audio.getAnswer());

		final JPanel content = new JPanel(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.gridx = 0;
		c.gridy = 0;
		content.add(new JLabel("Question "), c);
		c.gridy = 1;
		content.add(new JLabel("Answer "), c);
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = 0;
		content.add(question, c);
		c.gridy = 1;
		content.add(answer, c);

		c.gridx = c.gridy = 0;
		c.weightx = 0.0;
		c.weightx = 1.0;
		add(content, c);
	}

}
