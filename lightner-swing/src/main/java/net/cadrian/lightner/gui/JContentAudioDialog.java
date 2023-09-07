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
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import net.cadrian.lightner.model.content.audio.AudioContainer;
import net.cadrian.lightner.model.content.audio.AudioContainerFile;
import net.cadrian.lightner.model.content.audio.AudioType;

class JContentAudioDialog extends AbstractContentDialog {

	private static final long serialVersionUID = 2682374975665447291L;

	private static final Logger logger = Logger.getLogger(JContentAudioDialog.class.getName());

	@FunctionalInterface
	interface Creator {
		void create(UUID id, String title, AudioContainer question, AudioContainer answer);
	}

	private static final AtomicReference<File> lastDirectory = new AtomicReference<>();

	private final AtomicReference<AudioContainer> question = new AtomicReference<>();
	private final AtomicReference<AudioContainer> answer = new AtomicReference<>();

	public JContentAudioDialog(final Lightner owner, final Creator creator) {
		super(owner);

		final JPanel contentPane = new JPanel(new BorderLayout());
		setContentPane(contentPane);

		final JPanel questionPane = new JPanel(new BorderLayout());
		final JToolBar questionTools = new JToolBar(SwingConstants.HORIZONTAL);
		questionTools.setFloatable(false);
		questionTools.add(new JLabel("Question   "));
		final JButton questionBrowse = new JButton(LightnerIcon.BROWSE.getIcon());
		questionBrowse.setToolTipText("Browse question audio");
		questionTools.add(questionBrowse);
		final JAudioClip questionClip = new JAudioClip();
		questionPane.add(questionClip, BorderLayout.CENTER);
		questionPane.add(questionTools, BorderLayout.NORTH);

		final JPanel answerPane = new JPanel(new BorderLayout());
		final JToolBar answerTools = new JToolBar(SwingConstants.HORIZONTAL);
		answerTools.setFloatable(false);
		answerTools.add(new JLabel("Answer   "));
		final JButton answerBrowse = new JButton(LightnerIcon.BROWSE.getIcon());
		answerBrowse.setToolTipText("Browse answer audio");
		answerTools.add(answerBrowse);
		final JAudioClip answerClip = new JAudioClip();
		answerPane.add(answerClip, BorderLayout.CENTER);
		answerPane.add(answerTools, BorderLayout.NORTH);

		questionBrowse.addActionListener(ae -> browse("Question", questionClip, question));
		answerBrowse.addActionListener(ae -> browse("Answer", answerClip, answer));

		final JPanel inputPane = new JPanel(new GridLayout(2, 1));
		inputPane.add(questionPane);
		inputPane.add(answerPane);
		contentPane.add(inputPane, BorderLayout.CENTER);
		contentPane.add(titlePane, BorderLayout.NORTH);
		contentPane.add(tools, BorderLayout.SOUTH);

		validate.addActionListener(ae0 -> {
			setVisible(false);
			creator.create(id, title.getText(), question.get(), answer.get());
		});

		setTitle("Image: " + id);
		pack();
	}

	private void browse(final String what, final JAudioClip clip, final AtomicReference<AudioContainer> audio) {
		final JFileChooser fc = new JFileChooser();

		fc.addChoosableFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "Audios";
			}

			@Override
			public boolean accept(final File f) {
				return f.isDirectory() || AudioType.get(f.getName()) != null;
			}
		});
		fc.setAcceptAllFileFilterUsed(false);

		final File dir = lastDirectory.get();
		if (dir != null) {
			fc.setCurrentDirectory(dir);
		}

		final int returnVal = fc.showDialog(this, what);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			final File file = fc.getSelectedFile();
			try {
				clip.open(file);
				audio.set(new AudioContainerFile(file));
				lastDirectory.set(file.getParentFile());
			} catch (final IOException e) {
				logger.log(Level.SEVERE, e, () -> "Error during audio loading: " + file.getPath());
			}
		} else {
			logger.info("Audio browser cancelled by user");
		}
	}

}
