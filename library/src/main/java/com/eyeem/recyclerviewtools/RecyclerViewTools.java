package com.eyeem.recyclerviewtools;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by budius on 06.10.15.
 */
public class RecyclerViewTools {

   /**
    * Fast scroll-to-top with default parameters.
    * Same as calling fastScrollToTop(recyclerView, 15, 5, 222);
    *
    * @param recyclerView RecyclerView to be scrolled to top
    */
   public static void fastScrollToTop(RecyclerView recyclerView) {
      new FastScrollToTop(recyclerView);
   }

   /**
    * Fast scroll-to-top with custom parameters
    *
    * @param recyclerView          RecyclerView to be scrolled to top
    * @param threshold             distance from the top to use fast scroll
    * @param lastScroll            position to jump to before finishing scrolling up
    * @param scrollToPositionDelay delay before jump to position
    */
   public static void fastScrollToTop(RecyclerView recyclerView, int threshold, int lastScroll, long scrollToPositionDelay) {
      new FastScrollToTop(recyclerView, threshold, lastScroll, scrollToPositionDelay);
   }

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
    *
    * @param recyclerView To be scrolled
    * @param position     The adapter position to scroll to
    * @param offset       The distance (in pixels) between the start edge of the item view and start edge of the RecyclerView
    * @param smooth       true for smooth scroll; false to immediate scroll;
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
