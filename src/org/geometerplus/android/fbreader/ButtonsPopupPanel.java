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

import java.util.ArrayList;
import android.util.Log;

import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.android.fbreader.api.ApiServerImplementation;

import android.graphics.drawable.Drawable;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;
import android.widget.ZoomButton;

abstract class ButtonsPopupPanel extends PopupPanel implements View.OnClickListener {
	class ActionButton extends ZoomButton {
		final String ActionId;
		final boolean IsCloseButton;

		ActionButton(Context context, String actionId, boolean isCloseButton) {
			super(context);
			ActionId = actionId;
			IsCloseButton = isCloseButton;
		}
	}

	private final ArrayList<ActionButton> myButtons = new ArrayList<ActionButton>();

	ButtonsPopupPanel(FBReaderApp fbReader) {
		super(fbReader);
	}

	protected void addButton(String actionId, boolean isCloseButton, int imageId) {
		final ActionButton button = new ActionButton(myWindow.getContext(), actionId, isCloseButton);
		button.setImageResource(imageId);
		myWindow.addView(button);
		button.setOnClickListener(this);
		myButtons.add(button);
	}

	protected void addButton(String actionId, boolean isCloseButton, Drawable image) {
		final ActionButton button = new ActionButton(myWindow.getContext(), actionId, isCloseButton);
		button.setImageDrawable(image);
		myWindow.addView(button);
		button.setOnClickListener(this);
		myButtons.add(button);
	}

	@Override
	protected void update() {
		for (ActionButton button : myButtons) {
			button.setEnabled(Application.isActionEnabled(button.ActionId) || button.ActionId.charAt(0) == '@');
		}
	}

	public void onClick(View view) {
		final ActionButton button = (ActionButton)view;
		if (button.ActionId.charAt(0) == '@') {
			ApiServerImplementation.sendEvent((ContextWrapper)myWindow.getContext(), button.ActionId.substring(1));
		} else {
			Application.runAction(button.ActionId);
		}
		if (button.IsCloseButton) {
			storePosition();
			StartPosition = null;
			Application.hideActivePopup();
		}
	}
}
