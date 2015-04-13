package com.eyeem.recyclerviewtools;

import android.support.v7.widget.RecyclerView;

/**
 * Created by budius on 13.04.15.
 * <p/>
 * RecyclerView.OnScrollListener to callback to "load more" content
 */
public class LoadMoreOnScrollListener extends RecyclerView.OnScrollListener {

   private static final int DEFAULT_POSITION_OFFSET = 3;

   private final int positionOffset;
   private final Listener listener;
   private boolean loadMoreListenerCalled = false;

   public LoadMoreOnScrollListener(Listener listener) {
      this(listener, DEFAULT_POSITION_OFFSET);
   }

   public LoadMoreOnScrollListener(Listener listener, int positionOffset) {
      this.listener = listener;
      this.positionOffset = positionOffset;
   }

   @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      try {
         int position = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(recyclerView.getChildCount() - 1));
         if (position == RecyclerView.NO_POSITION) return;

         if (position >= recyclerView.getAdapter().getItemCount() - positionOffset) {
            if (!loadMoreListenerCalled) {
               Log.d(this, "Load more");
               listener.onLoadMore(recyclerView);
               loadMoreListenerCalled = true;
            }
         } else {
            loadMoreListenerCalled = false;
         }

      } catch (NullPointerException e) {
         /* shouldn't happen, but you never know */
         loadMoreListenerCalled = false;
      }
   }

   /**
    * Interface to receive `load more` callbacks
    */
   public interface Listener {

      /**
       * Received to notify the application should load more content for this recyclerView
       *
       * @param recyclerView recyclerView being scrolled
       */
      public void onLoadMore(RecyclerView recyclerView);
   }
}
