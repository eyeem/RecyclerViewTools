package com.eyeem.recyclerviewtools.scroll_controller;

import android.support.v7.widget.RecyclerView;

import com.eyeem.recyclerviewtools.Tools;

import static com.eyeem.recyclerviewtools.scroll_controller.Builder.Config;
import static com.eyeem.recyclerviewtools.scroll_controller.Builder.FLAG_COVER;
import static com.eyeem.recyclerviewtools.scroll_controller.Builder.FLAG_DOWN;
import static com.eyeem.recyclerviewtools.scroll_controller.Builder.FLAG_NORMAL_RETURN;
import static com.eyeem.recyclerviewtools.scroll_controller.Builder.FLAG_UP;

/**
 * Created by budius on 30.03.15.
 * This controller makes the view matches on top of the reference view.
 * Used for FLAG_NORMAL_RETURN && FLAG_COVER
 */
class _MatchWithController extends AbstractController {

   _MatchWithController(Config config) {
      super(config);
   }

   @Override
   boolean onScrollStateChanged(boolean refOnly, RecyclerView recyclerView, int newState) {
      return !refOnly && computeScrolled(recyclerView.getId(), false);
   }

   @Override boolean onScrolled(boolean refOnly, RecyclerView recyclerView, int dx, int dy) {
      return !refOnly && computeScrolled(recyclerView.getId(), false);
   }

   @Override boolean onGlobalLayout(boolean refOnly) {
      return !refOnly && computeScrolled(0, true);
   }

   private boolean computeScrolled(int parentId, boolean force) {

      // if reference in view, we want to match position
      if (Tools.isViewInsideLayout(config.reference, parentId)) {
         int translation = config.reference.getTop() - config.view.getTop();

         // execute if during layout pass or NORMAL_RETURN
         boolean execute = force || config.is(FLAG_NORMAL_RETURN);
         if (!execute && config.is(FLAG_COVER)) {
            execute =
               // also execute if UP and translating up
               (config.is(FLAG_UP) && translation > config.view.getTranslationY()) ||
                  // or DOWN and translating down
                  (config.is(FLAG_DOWN) && translation < config.view.getTranslationY());
         }
         if (execute)
            config.limit(translation);

         // even if we don't actually change the view position,
         // because it's on the screen, we still return true,
         // to indicate we're taking care of it
         return true;
      }

      // if reference not in view, we want to hide the view
      // only if NORMAL_RETURN (not in cover)
      // or if we're doing the layout pass
      else if (force || config.is(FLAG_NORMAL_RETURN)) {
         Tools.setTranslationY(config.view, config.getLimit());
         return true;
      } else return false;
   }
}