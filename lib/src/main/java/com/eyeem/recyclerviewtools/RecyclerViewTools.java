package com.eyeem.recyclerviewtools;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by budius on 06.10.15.
 */
public class RecyclerViewTools {

   /**
    * Scroll immediately to the given position with zero offset
    *
    * @param recyclerView To be scrolled
    * @param position     The adapter position to scroll to
    */
   public static void scrollToPosition(RecyclerView recyclerView, int position) {
      scrollToPosition(recyclerView, position, 0, false);
   }

   /**
    * Scroll immediately to the given position with offset
    *
    * @param recyclerView To be scrolled
    * @param position     The adapter position to scroll to
    * @param offset       The distance (in pixels) between the start edge of the item view and start edge of the RecyclerView
    */
   public static void scrollToPosition(RecyclerView recyclerView, int position, int offset) {
      scrollToPosition(recyclerView, position, offset, false);
   }

   /**
    * Scroll the recycler view
    * @param recyclerView To be scrolled
    * @param position The adapter position to scroll to
    * @param offset The distance (in pixels) between the start edge of the item view and start edge of the RecyclerView
    * @param smooth true for smooth scroll; false to immediate scroll;
    */
   public static void scrollToPosition(RecyclerView recyclerView, int position, int offset, boolean smooth) {

      if (smooth) {
         recyclerView.smoothScrollToPosition(position);
         return;
      }

      RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
      if (layoutManager instanceof LinearLayoutManager) {
         LinearLayoutManager llm = (LinearLayoutManager) layoutManager;
         llm.scrollToPositionWithOffset(position, offset);
      } else {
         recyclerView.scrollToPosition(position);
      }
   }
}
