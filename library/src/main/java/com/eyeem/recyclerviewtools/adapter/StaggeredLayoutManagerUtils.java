package com.eyeem.recyclerviewtools.adapter;

import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by budius on 26.05.16.
 */
public class StaggeredLayoutManagerUtils {

   static void checkLayoutParamsAfterLayout(View view) {
      view.addOnLayoutChangeListener(new OnLayout());
   }

   static void checkLayoutParams(View view) {
      // if StaggeredGridLayoutManager
      if (view.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams &&
         // and not set to full span yet
         !((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).isFullSpan()) {
         // set it to full span
         ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).setFullSpan(true);
      }
   }

   private static class OnLayout implements View.OnLayoutChangeListener {

      @Override
      public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
         checkLayoutParams(v);
      }
   }

}
