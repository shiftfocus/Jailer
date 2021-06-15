/*
 * Copyright 2007 - 2021 Ralf Wisser.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.jailer.ui.util;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public abstract class SmallButton extends JLabel {

	private final boolean borderStyle;
	private static final Color BORDER_LIGHT = Color.lightGray;
	private static final Color BORDER_SHADOW = Color.gray;
	private boolean entered = false;
	
	public SmallButton(Icon icon) {
		this(icon, false);
	}
	
	public SmallButton(Icon icon, boolean borderStyle) {
		super(icon);
		this.borderStyle = borderStyle;
		onMouseExited();
		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (entered && SwingUtilities.isLeftMouseButton(e)) {
					onClick(e);
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
				onMouseExited();
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				onMouseEntered();
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					onClick(e);
				}
			}
		});
	}

	protected abstract void onClick(MouseEvent e);

	protected void onMouseExited() {
		entered = false;
		if (borderStyle) {
			setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, BORDER_LIGHT, BORDER_SHADOW));
		} else {
			setEnabled(false);
		}
	}

	protected void onMouseEntered() {
		entered = true;
		if (borderStyle) {
			setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED, BORDER_LIGHT, BORDER_SHADOW));
		} else {
			setEnabled(true);
		}
	}
	
}
