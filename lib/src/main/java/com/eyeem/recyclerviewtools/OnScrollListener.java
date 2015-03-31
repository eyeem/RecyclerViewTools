package com.eyeem.recyclerviewtools;

import android.support.v7.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.WeakHashMap;

/**
 * Created by Lukasz and budius on 28.10,14 and 13.03.15.
 * Beefed up version of your traditional scroll listener.
 * <p/>
 * This allows multiple listeners and automatic calls to Picasso pause/resume
 */
public class OnScrollListener extends RecyclerView.OnScrollListener {

   // static access
   // ==============================================================================================
   private static final WeakHashMap<RecyclerView, OnScrollListener> cached = new WeakHashMap<>();

   /**
    * This method uses reflection to extract the current `RecyclerView.OnScrollListener`
    * from the RecyclerView and replaces it with a `OnScrollListener`,
    * and adding the old listener by calling `addListener`.
    * <p/>
    * This method uses reflection, which have some performance hit,
    * so it's advisable, whenever possible, to keep reference to the `OnScrollListener`
    * and call method directly on it.
    *
    * @param recyclerView
    * @return onScrollListener attached to the recyclerView.
    */
   public static OnScrollListener wrapMeInThyGlory(RecyclerView recyclerView) {

      if (recyclerView == null)
         return null;

      OnScrollListener result = null;

      // first try from the cache to avoid reflection
      result = cached.get(recyclerView);
      if (result != null)
         return result;

      // try to access private OnScrollListener mScrollListener; through reflection
      try {
         Field f = RecyclerView.class.getDeclaredField("mScrollListener");
         f.setAccessible(true);
         RecyclerView.OnScrollListener onScrollListener = (RecyclerView.OnScrollListener) f.get(recyclerView);
         if (onScrollListener == null) {
            // no listener whatsoever
            result = new OnScrollListener();
            recyclerView.setOnScrollListener(result);
         } else if (onScrollListener instanceof OnScrollListener) {
            result = (OnScrollListener) onScrollListener;
         } else {
            // a listener has been setup but it's not an instance of the recyclerviewtools.OnScrollListener
            // replace the existing listener with a wrapper, add existing one to the wrapper
            result = new OnScrollListener();
            result.addListener(onScrollListener);
            recyclerView.setOnScrollListener(result);
         }
      } catch (Exception e) {
         // NoSuchFieldException
         // IllegalAccessException
         // DoomsDayException
         // AnotherCrappyBoyBandException
         // PainfulDiarrheaException
      }

      cached.put(recyclerView, result);

      return result;
   }

   // class members
   // ==============================================================================================
   private final HashSet<RecyclerView.OnScrollListener> listeners = new HashSet<RecyclerView.OnScrollListener>();
   private Object picassoTag;
   private LoadMoreListener loadMoreListener;
   private int positionOffset = 1;
   private boolean loadMoreListenerCalled = false;

   /**
    * Adds RecyclerView.OnScrollListener to this OnScrollListener
    *
    * @param listener
    */
   public void addListener(RecyclerView.OnScrollListener listener) {
      listeners.add(listener);

   }

   /**
    * Removes RecyclerView.OnScrollListener from this OnScrollListener
    *
    * @param listener
    */
   public void removeListener(RecyclerView.OnScrollListener listener) {
      listeners.remove(listener);
   }

   @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
      for (RecyclerView.OnScrollListener listener : listeners)
         listener.onScrollStateChanged(recyclerView, newState);

      if (picassoTag == null) return;

      switch (newState) {
         case RecyclerView.SCROLL_STATE_DRAGGING:
         case RecyclerView.SCROLL_STATE_SETTLING:
            Picasso.with(recyclerView.getContext()).pauseTag(picassoTag);
            break;
         case RecyclerView.SCROLL_STATE_IDLE:
         default:
            Picasso.with(recyclerView.getContext()).resumeTag(picassoTag);
            break;
      }
   }

   @Override
   public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      for (RecyclerView.OnScrollListener listener : listeners)
         listener.onScrolled(recyclerView, dx, dy);

      if (loadMoreListener == null) return;

      try {
         int position = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(recyclerView.getChildCount() - 1));
         if (position == RecyclerView.NO_POSITION) return;

         if (position >= recyclerView.getAdapter().getItemCount() - positionOffset) {
            if (!loadMoreListenerCalled) {
               Log.d(this, "Load more");
               loadMoreListener.onLoadMore(recyclerView);
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
    * Add an object as a TAG to be used on Picasso pause/resume processing.
    *
    * @param picassoTag the tag to be used on Picasso
    */
   public void setPicassoTag(Object picassoTag) {
      this.picassoTag = picassoTag;
   }

   public void setLoadMoreListener(LoadMoreListener listener, int positionOffset) {
      this.loadMoreListener = listener;
      if (positionOffset < 1) positionOffset = 1;
      this.positionOffset = positionOffset;
   }

   public interface LoadMoreListener {
      public void onLoadMore(RecyclerView recyclerView);
   }
}