package com.eyeem.recyclerviewtools.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by budius on 26.05.16.
 */
class StaggeredLayoutManagerInternalUtils {

   static void setFullWidthLayoutParams(View parent, RecyclerView.ViewHolder holder) {

      // is recycler view (should be)
      if (!(parent instanceof RecyclerView)) {
         return;
      }
      RecyclerView rv = (RecyclerView) parent;

      // is grid layout manager, maybe is, maybe not
      if (!(rv.getLayoutManager() instanceof StaggeredGridLayoutManager)) {
         return;
      }

      StaggeredGridLayoutManager.LayoutParams lp;
      if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
         lp = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
         lp.setFullSpan(true);
      } else {
         lp = new StaggeredGridLayoutManager.LayoutParams(holder.itemView.getLayoutParams());
         lp.setFullSpan(true);
         holder.itemView.setLayoutParams(lp);
      }
   }
}
