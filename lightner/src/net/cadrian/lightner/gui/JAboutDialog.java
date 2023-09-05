package net.cadrian.lightner.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;

class JAboutDialog extends JDialog {

	private static final long serialVersionUID = -9142185738901664952L;
	private static final Logger logger = Logger.getLogger(JAboutDialog.class.getName());

	JAboutDialog(final Lightner owner) {
		super(owner);

		final JPanel contentPane = new JPanel(new BorderLayout());
		setContentPane(contentPane);

		final JEditorPane about = new JEditorPane();
		about.setEditable(false);
		about.setContentType("text/html");
		about.addHyperlinkListener(he -> {
			if (he.getEventType() == HyperlinkEvent.EventType.ACTIVATED && Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().browse(he.getURL().toURI());
				} catch (IOException | URISyntaxException e) {
					logger.log(Level.SEVERE, e, () -> "Could not open URL: " + he.getURL());
				}
			}
		});
		try (InputStream in = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("net/cadrian/lightner/gui/JAboutDialog.html")) {
			about.read(in, null);
		} catch (final IOException e) {
			logger.log(Level.SEVERE, e, () -> "Could not display about dialog");
		}
		contentPane.add(new JScrollPane(about), BorderLayout.CENTER);

		final JToolBar tools = new JToolBar(SwingConstants.HORIZONTAL);
		tools.setFloatable(false);
		tools.setAlignmentX(CENTER_ALIGNMENT);
		final JButton close = new JButton(LightnerIcon.EDIT_CANCEL.getIcon());
		close.setToolTipText("Close");
		tools.add(close);

		close.addActionListener(ae0 ->

		setVisible(false));
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		setMinimumSize(new Dimension(640, 480));
		setModal(true);
		setLocationRelativeTo(owner);
	}

}
