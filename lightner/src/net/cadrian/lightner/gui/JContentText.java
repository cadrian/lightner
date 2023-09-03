package net.cadrian.lightner.gui;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import net.cadrian.lightner.model.ContentText;

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
