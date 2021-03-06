/*
 * Copyright (C) 2007-2014 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.RelativeLayout;
import java.util.List;
import java.util.LinkedList;

import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.ui.android.R;

public class SelectionPopup extends ButtonsPopupPanel {
	final static String ID = "SelectionPopup";

	private static class SelectionPopupButton {
		String actionId;
		boolean isCloseButton;
		int imageId;
		Drawable image;
		int weight;
		SelectionPopupButton(String ai, boolean icb, int ii, int w) {
			actionId=ai;
			isCloseButton=icb;
			imageId=ii;
			image=null;
			weight=w;
		}
		SelectionPopupButton(String ai, boolean icb, Drawable i, int w) {
			actionId=ai;
			isCloseButton=icb;
			imageId=0;
			image=i;
			weight=w;
		}
		public boolean compareWeight(int w) {
			return w<weight;
		}
		public boolean compareAction(String action) {
			return actionId.equals(action);
		}
		public void setImage(Drawable image) {
			this.image = image;
			this.imageId = 0;
		}
	}

	static private List<SelectionPopupButton> buttonSet=new LinkedList<SelectionPopupButton>();

	static {
		addSelectionHandler(ActionCode.SELECTION_COPY_TO_CLIPBOARD, true, R.drawable.selection_copy,100);
		addSelectionHandler(ActionCode.SELECTION_SHARE, true, R.drawable.selection_share,200);
		addSelectionHandler(ActionCode.SELECTION_TRANSLATE, true, R.drawable.selection_translate,300);
		addSelectionHandler(ActionCode.SELECTION_BOOKMARK, true, R.drawable.selection_bookmark,400);
		addSelectionHandler(ActionCode.SELECTION_CLEAR, true, R.drawable.selection_close,500);
	}

	SelectionPopup(FBReaderApp fbReader) {
		super(fbReader);
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public void createControlPanel(FBReader activity, RelativeLayout root) {
		if (myWindow != null && activity == myWindow.getActivity()) {
			return;
		}

		myWindow = new PopupWindow(activity, root, PopupWindow.Location.Floating);

		for (SelectionPopupButton curButton: buttonSet) {
			if (curButton.image == null) {
				addButton(curButton.actionId,curButton.isCloseButton,curButton.imageId);
			} else {
				addButton(curButton.actionId,curButton.isCloseButton,curButton.image);
			}
		}
	}

	public void move(int selectionStartY, int selectionEndY) {
		if (myWindow == null) {
			return;
		}

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.WRAP_CONTENT,
			RelativeLayout.LayoutParams.WRAP_CONTENT
		);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

		final int verticalPosition;
		final int screenHeight = ((View)myWindow.getParent()).getHeight();
		final int diffTop = screenHeight - selectionEndY;
		final int diffBottom = selectionStartY;
		if (diffTop > diffBottom) {
			verticalPosition = diffTop > myWindow.getHeight() + 20
				? RelativeLayout.ALIGN_PARENT_BOTTOM : RelativeLayout.CENTER_VERTICAL;
		} else {
			verticalPosition = diffBottom > myWindow.getHeight() + 20
				? RelativeLayout.ALIGN_PARENT_TOP : RelativeLayout.CENTER_VERTICAL;
		}

		layoutParams.addRule(verticalPosition);
		myWindow.setLayoutParams(layoutParams);
	}
	
	public static void addSelectionHandler(String actionId, boolean isCloseButton, int imageId, int weight) {
		int i = getActionExists(actionId);
		if (i > -1) {
			return;
		}
		for (i = 0;i<buttonSet.size();i++)
			if ((buttonSet.get(i)).compareWeight(weight)) {
				break;
			};
		buttonSet.add(i,new SelectionPopupButton(actionId,isCloseButton,imageId,weight));
	}

	public static void addSelectionHandler(String actionId, boolean isCloseButton, Drawable image, int weight) {
		int i = getActionExists(actionId);
		if (i > -1) {
			buttonSet.get(i).setImage(image);
			return;
		}
		for (i = 0;i<buttonSet.size();i++)
			if ((buttonSet.get(i)).compareWeight(weight)) {
				break;
			};
		buttonSet.add(i,new SelectionPopupButton(actionId,isCloseButton,image,weight));
	}
	
	public static int getActionExists(String actionId) {
		int i = 0;
		for (;i<buttonSet.size();i++) {
			if ((buttonSet.get(i)).compareAction(actionId)) {
				return i;
			};
		};
		return -1;
	}
}
