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
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import net.cadrian.lightner.model.ContentImage.ImageType;

class JContentImageDialog extends AbstractContentDialog {

	private static final long serialVersionUID = 5169348014757965019L;
	private static final Logger logger = Logger.getLogger(JContentImageDialog.class.getName());

	@FunctionalInterface
	interface Creator {
		void create(UUID id, BufferedImage question, ImageType questionType, BufferedImage answer,
				ImageType answerType);
	}

	private static final AtomicReference<File> lastDirectory = new AtomicReference<>();

	private final AtomicReference<BufferedImage> question = new AtomicReference<>();
	private final AtomicReference<ImageType> questionType = new AtomicReference<>();
	private final AtomicReference<BufferedImage> answer = new AtomicReference<>();
	private final AtomicReference<ImageType> answerType = new AtomicReference<>();

	public JContentImageDialog(final Lightner owner, final Creator creator) {
		super(owner);

		final UUID id = UUID.randomUUID();

		final JPanel contentPane = new JPanel(new BorderLayout());
		setContentPane(contentPane);

		final JPanel questionPane = new JPanel(new BorderLayout());
		final JToolBar questionTools = new JToolBar(SwingConstants.HORIZONTAL);
		questionTools.setFloatable(false);
		questionTools.add(new JLabel("Question   "));
		final JButton questionBrowse = new JButton(" ⏏️ ");
		questionBrowse.setToolTipText("Browse question image");
		questionTools.add(questionBrowse);
		final JLabel questionImage = new JLabel();
		questionPane.add(questionImage, BorderLayout.CENTER);
		questionPane.add(questionTools, BorderLayout.NORTH);

		final JPanel answerPane = new JPanel(new BorderLayout());
		final JToolBar answerTools = new JToolBar(SwingConstants.HORIZONTAL);
		answerTools.setFloatable(false);
		answerTools.add(new JLabel("Answer   "));
		final JButton answerBrowse = new JButton(" ⏏️ ");
		answerBrowse.setToolTipText("Browse answer image");
		answerTools.add(answerBrowse);
		final JLabel answerImage = new JLabel();
		answerPane.add(answerImage, BorderLayout.CENTER);
		answerPane.add(answerTools, BorderLayout.NORTH);

		questionBrowse.addActionListener(ae -> browse("Question", questionImage, question, questionType));
		answerBrowse.addActionListener(ae -> browse("Answer", answerImage, answer, answerType));

		final JPanel inputPane = new JPanel(new GridLayout(2, 1));
		inputPane.add(questionPane);
		inputPane.add(answerPane);
		contentPane.add(inputPane, BorderLayout.CENTER);

		contentPane.add(tools, BorderLayout.SOUTH);

		validate.addActionListener(ae0 -> {
			setVisible(false);
			creator.create(id, question.get(), questionType.get(), answer.get(), answerType.get());
		});

		setTitle("Text: " + id);
		setMinimumSize(new Dimension(640, 480));
		pack();
		setModal(true);
		setLocationRelativeTo(owner);
	}

	private void browse(final String what, final JLabel displayImage, final AtomicReference<BufferedImage> image,
			final AtomicReference<ImageType> imageType) {
		final JFileChooser fc = new JFileChooser();

		fc.addChoosableFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "Images";
			}

			@Override
			public boolean accept(final File f) {
				return f.isDirectory() || ImageType.get(f.getName()) != null;
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
				final BufferedImage img = ImageIO.read(file);
				imageType.set(ImageType.get(file.getName()));
				image.set(img);
				displayImage.setIcon(new ImageIcon(fitimage(img, displayImage.getWidth(), displayImage.getHeight())));
				for (final ComponentListener l : displayImage.getComponentListeners()) {
					displayImage.removeComponentListener(l);
				}
				displayImage.addComponentListener(new ComponentAdapter() {
					@Override
					public void componentResized(final ComponentEvent e) {
						displayImage.setIcon(
								new ImageIcon(fitimage(img, displayImage.getWidth(), displayImage.getHeight())));
					}
				});
				lastDirectory.set(file.getParentFile());
			} catch (final IOException e) {
				logger.log(Level.SEVERE, e, () -> "Error during image loading: " + file.getPath());
			}
		} else {
			logger.info("Image browser cancelled by user");
		}
	}

	// https://stackoverflow.com/a/34169084 (improved)
	static BufferedImage fitimage(final BufferedImage img, final int w, final int h) {
		final int w0 = img.getWidth();
		final int h0 = img.getHeight();
		final double dw = (double) w / w0;
		final double dh = (double) h / h0;
		final double d = dw < dh ? dw : dh;

		final int w1 = (int) (d * w0);
		final int h1 = (int) (d * h0);

		logger.info(() -> "img[" + w0 + "×" + h0 + "] to [" + w + "×" + h + "] ⇒ [" + w1 + "×" + h1 + "] ("
				+ (int) (d * 100) + "%)");

		final BufferedImage resizedimage = new BufferedImage(w1, h1, BufferedImage.TYPE_INT_RGB);
		final Graphics2D g2 = resizedimage.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(img, 0, 0, w1, h1, null);
		g2.dispose();
		return resizedimage;
	}

}
