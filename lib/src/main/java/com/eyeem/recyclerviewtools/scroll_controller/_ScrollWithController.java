package com.eyeem.recyclerviewtools.scroll_controller;

import android.support.v7.widget.RecyclerView;

import static com.eyeem.recyclerviewtools.scroll_controller.Builder.Config;
import static com.eyeem.recyclerviewtools.scroll_controller.Builder.FLAG_DOWN;

/**
 * Created by budius on 30.03.15.
 * This controller scrolls the view at the same delta of the RecyclerView.
 * Used for FLAG_QUICK_RETURN
 */
class _ScrollWithController extends AbstractController {

   _ScrollWithController(Config config) {
      super(config);
   }

   @Override boolean onScrolled(boolean refOnly, RecyclerView recyclerView, int dx, int dy) {
      if (refOnly) return false;
      float newY;
      if (config.is(FLAG_DOWN)) {
         newY = config.view.getTranslationY() + dy;
      } else {
         newY = config.view.getTranslationY() - dy;
      }
      return config.limit((int) newY);
   }
}
