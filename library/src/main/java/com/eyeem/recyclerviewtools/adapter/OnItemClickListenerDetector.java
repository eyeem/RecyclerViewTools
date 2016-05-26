package com.eyeem.recyclerviewtools.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.eyeem.recyclerviewtools.OnItemClickListener;

/**
 * Created by budius on 01.04.15.
 * <p/>
 * Simple implementation of {@link android.widget.AdapterView.OnItemClickListener AdapterView.OnItemClickListener}
 * refactored to {@link android.support.v7.widget.RecyclerView RecyclerView}
 * <p/>
 * Just like original, this only catch clicks on the whole view.
 * For finer control on the target view for the click, you still must create a custom implementation.
 */
/* package */ class OnItemClickListenerDetector implements View.OnClickListener {

   private final RecyclerView recyclerView;
   private final OnItemClickListener onItemClickListener;
   final boolean ignoreExtras;

   OnItemClickListenerDetector(
           RecyclerView recyclerView,
           OnItemClickListener onItemClickListener,
           boolean ignoreExtras) {
      this.recyclerView = recyclerView;
      this.onItemClickListener = onItemClickListener;
      this.ignoreExtras = ignoreExtras;
   }

   @Override
   public void onClick(View view) {

      RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);
      int position = holder.getAdapterPosition();
      long id = holder.getItemId();

      RecyclerView.Adapter adapter = recyclerView.getAdapter();

      if (ignoreExtras && adapter instanceof WrapAdapter) {
         WrapAdapter a = (WrapAdapter) adapter;
         position = a.recyclerToWrappedPosition.get(position);
      }

      // this can happen if data set is changing onItemClick and user clicks fast
      if (position < 0 || position >= adapter.getItemCount()) return;

      onItemClickListener.onItemClick(recyclerView, view, position, id, holder);
   }
}
