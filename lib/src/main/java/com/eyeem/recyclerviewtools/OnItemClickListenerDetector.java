package com.eyeem.recyclerviewtools;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.eyeem.recyclerviewtools.adapter.AbstractHeaderFooterRecyclerAdapter;

/**
 * Created by budius on 01.04.15.
 * <p/>
 * Simple implementation of {@link android.widget.AdapterView.OnItemClickListener AdapterView.OnItemClickListener}
 * refactored to {@link android.support.v7.widget.RecyclerView RecyclerView}
 * <p/>
 * Just like original, this only catch clicks on the whole view.
 * For finer control on the target view for the click, you still must create a custom implementation.
 */
public class OnItemClickListenerDetector implements View.OnClickListener {

   private final RecyclerView recyclerView;
   private final OnItemClickListener onItemClickListener;

   private boolean ignoreHeaders = false;

   public OnItemClickListenerDetector(RecyclerView recyclerView, OnItemClickListener onItemClickListener) {
      this(recyclerView, onItemClickListener, false);
   }

   public OnItemClickListenerDetector(
      RecyclerView recyclerView,
      OnItemClickListener onItemClickListener,
      boolean ignoreHeaders) {
      this.recyclerView = recyclerView;
      this.onItemClickListener = onItemClickListener;
      this.ignoreHeaders = ignoreHeaders;
   }

   @Override public void onClick(View view) {

      int position = recyclerView.getChildAdapterPosition(view);
      long id = recyclerView.getChildItemId(view);

      RecyclerView.Adapter adapter = recyclerView.getAdapter();
      if (ignoreHeaders && adapter instanceof AbstractHeaderFooterRecyclerAdapter) {
         AbstractHeaderFooterRecyclerAdapter a = (AbstractHeaderFooterRecyclerAdapter) adapter;
         if (a.isFooterPosition(position) || a.isHeaderPosition(position)) return;
         position -= a.getNumberOfHeaders();
      }

      onItemClickListener.onItemClick(recyclerView, view, position, id);
   }

   public interface OnItemClickListener {
      public abstract void onItemClick(RecyclerView parent, View view, int position, long id);
   }
}
