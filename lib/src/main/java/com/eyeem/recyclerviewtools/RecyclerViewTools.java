package com.eyeem.recyclerviewtools;

import android.view.View;

import com.eyeem.recyclerviewtools.scroll_controller.Builder;

/**
 * Created by budius on 25.03.15.
 */
public class RecyclerViewTools {

   public static void setLogLevel(int level) {
      Log.DEBUG = level;
   }

   public static Builder setup(View view) {
      return new Builder(view);
   }

}
