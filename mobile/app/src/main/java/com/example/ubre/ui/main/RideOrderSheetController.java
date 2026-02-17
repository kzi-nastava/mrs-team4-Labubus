package com.example.ubre.ui.main;

import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class RideOrderSheetController {
    private final BottomSheetBehavior<View> sheetBehavior;
    private final View confirmButton;

    public RideOrderSheetController(View sheetView, View confirmButton, int peekHeightPx) {
        this.sheetBehavior = BottomSheetBehavior.from(sheetView);
        this.sheetBehavior.setHideable(true);
        this.sheetBehavior.setPeekHeight(peekHeightPx, true);
        this.sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        this.sheetBehavior.setDraggable(true);
        this.confirmButton = confirmButton;
    }

    public void toggle() {
        int state = sheetBehavior.getState();
        if (state == BottomSheetBehavior.STATE_HIDDEN) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    public void toggleCollapsed() {
        int state = sheetBehavior.getState();
        if (state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (state == BottomSheetBehavior.STATE_COLLAPSED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (state == BottomSheetBehavior.STATE_HIDDEN) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public void hide() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    public void collapse() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public boolean isHidden() {
        return sheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN;
    }

    public void updateGuestState(boolean isGuest) {
        if (confirmButton == null) {
            return;
        }
        confirmButton.setEnabled(!isGuest);
        confirmButton.setAlpha(isGuest ? 0.5f : 1.0f);
    }
}
