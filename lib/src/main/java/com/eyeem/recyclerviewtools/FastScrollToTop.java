package com.eyeem.recyclerviewtools;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by budius on 18.12.15.
 */
/* package */  class FastScrollToTop {

   private static final int POSITION_THRESHOLD = 15;
   private static final int LAST_SCROLL = 5;
   private static final long SCROLL_TO_POSITION_DELAY = 222;

   private final WeakReference<RecyclerView> weakRecycler;

   private final int lastScroll;

   public FastScrollToTop(RecyclerView recyclerView) {
      this(recyclerView, POSITION_THRESHOLD, LAST_SCROLL, SCROLL_TO_POSITION_DELAY);
   }

   public FastScrollToTop(RecyclerView recyclerView, int threshold, int lastScroll, long scrollToPositionDelay) {
      this.lastScroll = lastScroll;

      if (recyclerView.getChildCount() == 0) {
         weakRecycler = null;
         return;
      }

      View v = recyclerView.getChildAt(0);
      int position = recyclerView.getChildAdapterPosition(v);
      if (position < threshold) {
         recyclerView.smoothScrollToPosition(0);
         weakRecycler = null;
         return;
      }

      // here is where the fun happens
      weakRecycler = new WeakReference<>(recyclerView);
      recyclerView.smoothScrollToPosition(0);
      recyclerView.postDelayed(runAfterSpeedUp, scrollToPositionDelay);
   }

   private Runnable runAfterSpeedUp = new Runnable() {
      @Override public void run() {
         RecyclerView recyclerView = weakRecycler.get();
         if (recyclerView == null) return;
         recyclerView.scrollToPosition(lastScroll);
         recyclerView.smoothScrollToPosition(0);
      }
   };
}
