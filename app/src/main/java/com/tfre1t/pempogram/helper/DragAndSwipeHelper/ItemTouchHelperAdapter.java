package com.tfre1t.pempogram.helper.DragAndSwipeHelper;

public interface ItemTouchHelperAdapter {

        boolean onItemMove(int fromPosition, int toPosition);

        void onItemDismiss(int position);
}
