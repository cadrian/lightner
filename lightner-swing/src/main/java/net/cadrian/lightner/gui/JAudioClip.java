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
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

class JAudioClip extends JPanel {

	private static final long serialVersionUID = 4654628538900133114L;
	private static final Logger logger = Logger.getLogger(JAudioClip.class.getName());
	private static final String NO_AUDIO = "(no audio)";

	private Clip clip;
	private final transient Thread progressUpdateThread;
	private final AtomicBoolean progressUpdateStop = new AtomicBoolean();
	private final AtomicBoolean progressUpdateSkipping = new AtomicBoolean();
	private final AtomicBoolean progressUpdateUpdating = new AtomicBoolean();

	private final JSlider progress;
	private final JLabel title;
	private JButton play;
	private JButton pause;
	private JButton stop;

	public JAudioClip() {
		super(new GridBagLayout());

		final JPanel content = new JPanel(new BorderLayout());

		progress = new JSlider(SwingConstants.HORIZONTAL);
		progress.setMinimumSize(new Dimension(240, 24));
		title = new JLabel(NO_AUDIO);

		final JPanel main = new JPanel(new BorderLayout());
		main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		main.add(progress, BorderLayout.CENTER);
		main.add(title, BorderLayout.SOUTH);

		play = new JButton(LightnerIcon.AUDIO_PLAY.getIcon());
		pause = new JButton(LightnerIcon.AUDIO_PAUSE.getIcon());
		stop = new JButton(LightnerIcon.AUDIO_STOP.getIcon());

		final JToolBar tools = new JToolBar(SwingConstants.HORIZONTAL);
		tools.setFloatable(false);
		tools.setBorderPainted(false);
		tools.add(play);
		tools.add(pause);
		tools.add(stop);

		progress.addChangeListener(ce -> skip());
		play.addActionListener(ae -> play());
		pause.addActionListener(ae -> pause());
		stop.addActionListener(ae -> stop());

		content.add(main, BorderLayout.CENTER);
		content.add(tools, BorderLayout.EAST);

		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		add(content, c);

		progressUpdateThread = new Thread(() -> {
			while (!progressUpdateStop.get()) {
				if (!progressUpdateSkipping.get() && clip != null && clip.isOpen()) {
					progressUpdateUpdating.set(true);
					SwingUtilities.invokeLater(() -> {
						final int n = clip.getFrameLength();
						final int i = clip.getFramePosition();
						progress.setMaximum(n);
						progress.setValue(i);
						progressUpdateUpdating.set(false);
					});
				}
				try {
					Thread.sleep(1000);
				} catch (final InterruptedException e) {
					logger.log(Level.SEVERE, e, () -> "Unexpected interrupt in audio progress update");
					Thread.currentThread().interrupt();
					return;
				}
			}
		});
		progressUpdateThread.setDaemon(true);
		progressUpdateThread.start();
	}

	public JAudioClip(final File audio) throws IOException {
		this();
		open(audio);
	}

	private void skip() {
		if (!progressUpdateUpdating.get() && clip.isOpen()) {
			progressUpdateSkipping.set(true);
			SwingUtilities.invokeLater(() -> {
				clip.setFramePosition(progress.getValue());
				progressUpdateSkipping.set(false);
			});
		}
	}

	private void play() {
		clip.start();
	}

	private void stop() {
		clip.stop();
		clip.setFramePosition(0);
	}

	private void pause() {
		clip.stop();
	}

	public void open(final File audio) throws IOException {
		try {
			if (clip == null) {
				clip = AudioSystem.getClip();
				progress.setEnabled(true);

				clip.addLineListener(event -> {
					final Type type = event.getType();
					if (Type.START.equals(type)) {
						play.setEnabled(false);
						pause.setEnabled(true);
						stop.setEnabled(true);
					} else if (Type.STOP.equals(type) || Type.OPEN.equals(type)) {
						play.setEnabled(true);
						pause.setEnabled(false);
						stop.setEnabled(clip.getFramePosition() > 0);
					} else if (Type.CLOSE.equals(type)) {
						progress.setEnabled(false);
						play.setEnabled(false);
						pause.setEnabled(false);
						stop.setEnabled(false);
					} else {
						logger.info(() -> "Unknown type: " + type);
					}
				});
			}
			clip.open(AudioSystem.getAudioInputStream(audio));
			title.setText(audio.getName());
		} catch (final LineUnavailableException | UnsupportedAudioFileException e) {
			throw new IOException(e);
		}
	}

	public void close() {
		clip.close();
		title.setText(NO_AUDIO);
	}

	public void destroy() {
		progressUpdateStop.set(true);
	}

}
