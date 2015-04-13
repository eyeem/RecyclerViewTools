package com.eyeem.recyclerviewtools;

import android.support.annotation.IdRes;
import android.view.View;

import com.eyeem.recyclerviewtools.scroll_controller.Builder;

/**
 * Created by budius on 25.03.15.
 */
public class RecyclerViewTools {

   public static void setLogLevel(int level) {
      Log.DEBUG = level;
   }

   public static Builder setupMotionControl(View view) {
      return new Builder(view);
   }

   public static ParallaxDetector setupParallaxDetection(View view, ParallaxDetector.Listener listener, @IdRes int parentId) {
      return new ParallaxDetector(view, listener, parentId);
   }

}
