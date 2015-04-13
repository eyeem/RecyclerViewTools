package com.eyeem.recyclerviewtools;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewParent;

/**
 * Created by budius on 30.03.15.
 */
public class Tools {

   private static final int ANDROID_PARENT_ID = android.R.id.content;

   public static Point getNumberOfVisiblePixels(View view, int parentId, Point point, Rect rect) {
      if (Tools.isViewInsideLayout(view, parentId)) {
         boolean result = view.getGlobalVisibleRect(rect);
         if (result) {
            point.set(rect.width(), rect.height());
            return point;
         }
      }
      point.set(0, 0);
      return point;
   }

   public static boolean isViewInsideLayout(View view, int parentId) {
      return getParentLayout(view, parentId) != null;
   }

   private static View getParentLayout(View view, int parentId) {
      ViewParent parent = view.getParent();
      while (parent instanceof View) {
         int id = ((View) parent).getId();
         if (id == parentId || id == ANDROID_PARENT_ID)
            return (View) parent;
         else
            parent = parent.getParent();
      }
      return null;
   }

}
