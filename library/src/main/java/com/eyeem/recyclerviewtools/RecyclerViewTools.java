package com.eyeem.recyclerviewtools;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

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
    * Check the several possible layout manager and executes a instant scroll to position with offset.
    * That means that `offset` is only properly applied for:
    * - LinearLayoutManager
    * - GridLayoutManager
    * - StaggeredGridLayoutManager
    * and any LayoutManager that extends them.
    *
    * @param recyclerView recycler view to execute the scroll
    * @param position     the adapter position to scroll to
    * @param offset       the offset in pixels from the start edge of the recycler and the itemView
    */
   public static void scrollToPositionWithOffset(RecyclerView recyclerView, int position, int offset) {
      RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
      if (lm instanceof LinearLayoutManager) { // GridLayoutManager extends LinearLayoutManager
         ((LinearLayoutManager) lm).scrollToPositionWithOffset(position, offset);
      } else if (lm instanceof StaggeredGridLayoutManager) {
         ((StaggeredGridLayoutManager) lm).scrollToPositionWithOffset(position, offset);
      } else {
         recyclerView.scrollToPosition(position);
      }
   }

   /**
    * Currently this simply calls recyclerView.smoothScrollToPosition(position);
    * But the API is already available and in the near future we'll try to code the `offset`
    *
    * @param recyclerView
    * @param position
    * @param offset
    */
   public static void smoothScrollToPositionWithOffset(RecyclerView recyclerView, int position, int offset) {
      recyclerView.smoothScrollToPosition(position);
   }

}
