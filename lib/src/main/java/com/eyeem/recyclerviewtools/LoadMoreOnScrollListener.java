package com.eyeem.recyclerviewtools;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;

import com.eyeem.recyclerviewtools.adapter.WrapAdapter;

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

   public void setLoadMoreListenerCalled(boolean value) {
      loadMoreListenerCalled = value;
   }

   @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

      if (!ViewCompat.canScrollVertically(recyclerView, -1)) {
         loadMoreListenerCalled = false;
      }

      try {

         // don't call listener if adapter is empty
         RecyclerView.Adapter a = recyclerView.getAdapter();
         if (a instanceof WrapAdapter && ((WrapAdapter) a).getWrappedCount() == 0)
            return;
         else if (a.getItemCount() == 0) return;

         // get position for last childView on recyclerView
         int position = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(recyclerView.getChildCount() - 1));

         // if recyclerView don't know the position, there's nothing I can do
         if (position == RecyclerView.NO_POSITION) return;

         // check offset to call listener
         if (position >= recyclerView.getAdapter().getItemCount() - positionOffset) {
            if (!loadMoreListenerCalled) {
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
