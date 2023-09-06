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
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import net.cadrian.lightner.dao.LightnerDataContent;

class JAudioClip extends JPanel {

	private static final long serialVersionUID = 4654628538900133114L;
	private static final Logger logger = Logger.getLogger(JAudioClip.class.getName());
	private static final String NO_AUDIO = "(no audio)";

	static {
		Platform.startup(() -> {
		});
	}

	private final AtomicReference<MediaPlayer> player = new AtomicReference<>();

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
				final MediaPlayer thePlayer = player.get();
				if (!progressUpdateSkipping.get() && thePlayer != null) {
					progressUpdateUpdating.set(true);
					SwingUtilities.invokeLater(() -> {
						final int n = (int) (thePlayer.getMedia().getDuration().toSeconds() + 0.5);
						final int i = (int) (thePlayer.getCurrentTime().toSeconds() + 0.5);
						progress.setMaximum(n);
						progress.setValue(i);
						progressUpdateUpdating.set(false);
					});
				}
				try {
					Thread.sleep(1000L);
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

	public JAudioClip(final LightnerDataContent audio) throws IOException {
		this();
		open(audio);
	}

	private void skip() {
		final MediaPlayer thePlayer = player.get();
		if (!progressUpdateUpdating.get() && thePlayer != null) {
			progressUpdateSkipping.set(true);
			SwingUtilities.invokeLater(() -> {
				final Duration duration = thePlayer.getMedia().getDuration();
				thePlayer.seek(duration.multiply(progress.getValue() / (double) progress.getMaximum()));
				progressUpdateSkipping.set(false);
			});
		}
	}

	private void play() {
		player.get().play();
	}

	private void stop() {
		player.get().stop();
	}

	private void pause() {
		player.get().pause();
	}

	public void open(final LightnerDataContent audio) throws IOException {
		final MediaPlayer theplayer = new MediaPlayer(new Media(audio.getURI().toString()));
		player.set(theplayer);
		progress.setEnabled(true);
		play.setEnabled(true);
		pause.setEnabled(false);
		stop.setEnabled(false);

		theplayer.setOnPlaying(() -> {
			play.setEnabled(false);
			pause.setEnabled(true);
			stop.setEnabled(true);
		});
		theplayer.setOnPaused(() -> {
			play.setEnabled(true);
			pause.setEnabled(false);
			stop.setEnabled(true);
		});
		theplayer.setOnStopped(() -> {
			play.setEnabled(true);
			pause.setEnabled(false);
			stop.setEnabled(false);
		});

		title.setText(audio.getName());
	}

	public void close() {
		progress.setEnabled(false);
		play.setEnabled(false);
		pause.setEnabled(false);
		stop.setEnabled(false);
		player.set(null);
		title.setText(NO_AUDIO);
	}

	public void destroy() {
		progressUpdateStop.set(true);
	}

}
