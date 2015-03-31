package com.eyeem.recyclerviewtools;

import android.view.View;
import android.view.ViewParent;

/**
 * Created by budius on 30.03.15.
 */
public class Tools {

   private static final int ANDROID_PARENT_ID = android.R.id.content;

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

   public static boolean setTranslationY(View view, int val) {
      return setTranslationY(view, (float) val);
   }

   public static boolean setTranslationY(View view, float val) {
      if (view.getTranslationY() != val) {
         view.setTranslationY(val);
         return true;
      } else return false;
   }

}
