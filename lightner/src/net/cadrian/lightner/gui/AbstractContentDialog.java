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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

abstract class AbstractContentDialog extends JDialog {

	private static final long serialVersionUID = 6484109033153047981L;

	protected final JToolBar tools;
	protected final JButton validate;
	protected final JButton cancel;

	protected AbstractContentDialog(final Lightner owner) {
		super(owner);

		tools = new JToolBar(SwingConstants.HORIZONTAL);
		tools.setFloatable(false);
		tools.setAlignmentX(CENTER_ALIGNMENT);
		validate = new JButton(LightnerIcon.EDIT_COMMIT.getIcon());
		validate.setToolTipText("Commit");
		cancel = new JButton(LightnerIcon.EDIT_CANCEL.getIcon());
		cancel.setToolTipText("Cancel");
		tools.add(validate);
		tools.add(cancel);

		cancel.addActionListener(ae0 -> setVisible(false));

		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	}

}
