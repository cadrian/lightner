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
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;

import net.cadrian.lightner.model.LightnerBox;

public class Lightner extends JFrame {

	private static final long serialVersionUID = -3335601051895440497L;
	private static final Logger logger = Logger.getLogger(Lightner.class.getName());

	private final transient LightnerBox box;

	public Lightner(final String boxPath) throws IOException {
		box = new LightnerBox(new File(boxPath));
		setSize(800, 600);
		setTitle("Lightner Box");
		setLookAndFeel(false);

		final JPanel content = new JPanel(new BorderLayout());
		setContentPane(content);

		final LightnerDayLabel dayLabel = new LightnerDayLabel(box.getDay());
		content.add(dayLabel, BorderLayout.NORTH);

		final JPanel boxesPanel = new JPanel(new GridLayout(1, 1));
		boxesPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		final LightnerBoxes boxes = new LightnerBoxes(this, box);
		boxesPanel.add(boxes);
		content.add(boxesPanel, BorderLayout.CENTER);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	private void setLookAndFeel(final boolean system) {
		try {
			if (system) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} else {
				boolean foundNimbus = false;
				for (final LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
					if ("Nimbus".equals(info.getName())) {
						logger.info("Using Nimbus L&F");
						UIManager.setLookAndFeel(info.getClassName());
						foundNimbus = true;
						break;
					}
				}
				if (!foundNimbus) {
					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			logger.log(Level.SEVERE, e, () -> "Could not load L&F");
		}
	}

}
