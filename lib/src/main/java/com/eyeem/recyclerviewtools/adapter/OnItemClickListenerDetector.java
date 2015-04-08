package com.eyeem.recyclerviewtools.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.eyeem.recyclerviewtools.Log;

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
   final boolean ignoreExtras;

   public OnItemClickListenerDetector(RecyclerView recyclerView, OnItemClickListener onItemClickListener) {
      this(recyclerView, onItemClickListener, true);
   }

   public OnItemClickListenerDetector(
      RecyclerView recyclerView,
      OnItemClickListener onItemClickListener,
      boolean ignoreExtras) {
      this.recyclerView = recyclerView;
      this.onItemClickListener = onItemClickListener;
      this.ignoreExtras = ignoreExtras;
   }

   @Override public void onClick(View view) {

      RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);
      int position = holder.getAdapterPosition();
      long id = holder.getItemId();

      RecyclerView.Adapter adapter = recyclerView.getAdapter();

      if (ignoreExtras && adapter instanceof WrapAdapter) {
         WrapAdapter a = (WrapAdapter) adapter;
         position = a.recyclerToWrappedPosition.get(position);
      }

      Log.d(this, "onClick position " + position);

      onItemClickListener.onItemClick(recyclerView, view, position, id);
   }

   public interface OnItemClickListener {
      public abstract void onItemClick(RecyclerView parent, View view, int position, long id);
   }
}
