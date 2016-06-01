package com.eyeem.recyclerviewtools;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by budius on 26.05.16.
 */
public class StaggeredLayoutManagerUtils {

   /**
    * Fix for some odd behavior with StaggeredGridLayoutManager animations
    *
    * @param recyclerView
    * @param aroundPosition
    */
   public static void onItemChanged(RecyclerView recyclerView, int aroundPosition) {

      if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
         recyclerView.addOnScrollListener(new ScrollHack(aroundPosition));
      } else {
         throw new IllegalArgumentException("This method is to fix issues on StaggeredGridLayoutManager");
      }

   }

   private static class ScrollHack extends RecyclerView.OnScrollListener {
      private final int position;

      public ScrollHack(int aroundPosition) {
         position = aroundPosition;
      }

      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

         View v1 = recyclerView.getChildAt(0);
         View v2 = recyclerView.getChildAt(recyclerView.getChildCount() - 1);

         int i1 = recyclerView.getChildAdapterPosition(v1);
         int i2 = recyclerView.getChildAdapterPosition(v2);

         if (position >= i1 && position <= i2) {
            ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).invalidateSpanAssignments();
            recyclerView.invalidateItemDecorations();
            recyclerView.removeOnScrollListener(this);
         }
      }
   }
}
