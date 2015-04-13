package com.eyeem.recyclerviewtools;

import android.support.v7.widget.RecyclerView;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.WeakHashMap;

/**
 * Created by Lukasz and budius on 28.10,14 and 13.03.15.
 * Beefed up version of your traditional scroll listener.
 * <p/>
 * This allows multiple listeners
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
   }

   @Override
   public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      for (RecyclerView.OnScrollListener listener : listeners)
         listener.onScrolled(recyclerView, dx, dy);
   }
}