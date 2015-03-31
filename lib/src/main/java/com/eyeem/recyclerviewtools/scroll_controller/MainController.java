package com.eyeem.recyclerviewtools.scroll_controller;

import android.support.v7.widget.RecyclerView;
import android.view.ViewTreeObserver;

import static com.eyeem.recyclerviewtools.scroll_controller.Builder.Config;
import static com.eyeem.recyclerviewtools.scroll_controller.Builder.FLAG_COVER;
import static com.eyeem.recyclerviewtools.scroll_controller.Builder.FLAG_NORMAL_RETURN;
import static com.eyeem.recyclerviewtools.scroll_controller.Builder.FLAG_SNAP_TO;

/**
 * Created by budius on 30.03.15.
 * <p/>
 * This mains controller is responsible in allocate and distribute callbacks
 * to the necessary controllers for the given configuration.
 * <p/>
 * To extended the functionality one can easily create a new
 * {@link com.eyeem.recyclerviewtools.scroll_controller.AbstractController AbstractController}
 * and add it to here.
 */
class MainController extends RecyclerView.OnScrollListener implements ViewTreeObserver.OnGlobalLayoutListener {

   AbstractController match; // matches the overlay with a view in the recycler
   AbstractController scroll; // scroll with the recycler
   AbstractController snap; // tracks movement and snaps to position when user let it go

   private final Config config;

   MainController(Config config) {
      this.config = config;

      if (config.is(FLAG_NORMAL_RETURN)) {
         match = new _MatchWithController(config);
      } else {
         scroll = new _ScrollWithController(config);
         if (config.is(FLAG_SNAP_TO))
            snap = new _SnapToController(config);
         if (config.is(FLAG_COVER))
            match = new _MatchWithController(config);
      }

      // do we want to know the initial layout
      if (config.is(FLAG_NORMAL_RETURN) || config.is(FLAG_COVER))
         config.getParent().getViewTreeObserver().addOnGlobalLayoutListener(this);

   }

   @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

      boolean refOnly = false;

      if (config.is(FLAG_NORMAL_RETURN)) {
         refOnly = dispatchOnScrollStateChanged(match, refOnly, recyclerView, newState);
      } else {

         if (config.is(FLAG_COVER)) {
            refOnly = dispatchOnScrollStateChanged(match, refOnly, recyclerView, newState);
         }

         if (config.is(FLAG_SNAP_TO)) {
            refOnly = dispatchOnScrollStateChanged(snap, refOnly, recyclerView, newState);
         }

         refOnly = dispatchOnScrollStateChanged(scroll, refOnly, recyclerView, newState);

      }
   }

   @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

      boolean refOnly = false;

      if (config.is(FLAG_NORMAL_RETURN)) {
         refOnly = dispatchOnScrolled(match, refOnly, recyclerView, dx, dy);
      } else {

         if (config.is(FLAG_COVER)) {
            refOnly = dispatchOnScrolled(match, refOnly, recyclerView, dx, dy);
         }

         if (config.is(FLAG_SNAP_TO)) {
            refOnly = dispatchOnScrolled(snap, refOnly, recyclerView, dx, dy);
         }

         refOnly = dispatchOnScrolled(scroll, refOnly, recyclerView, dx, dy);

      }
   }

   @Override public void onGlobalLayout() {

      // if layout pass done nicely
      if (config.view.getWidth() > 0 && config.view.getHeight() > 0 &&
         config.getParent().getWidth() > 0 && config.getParent().getHeight() > 0) {

         // remove self from listener
         config.getParent().getViewTreeObserver().removeGlobalOnLayoutListener(this);

         // let the `match` checks position
         if (config.is(FLAG_NORMAL_RETURN) || config.is(FLAG_COVER))
            match.onGlobalLayout(false);

      }
   }

   // All the controllers must obey the contract that:
   //  - they can only change the view translation, if refOnly == false
   //  - they must return true, if they changed the translation or are in control of the view position
   // that ensures a proper flow from one controller to the next
   // the next code checks if none of them screw that contract
   // ==============================================================================================
   // TODO: maybe we can remove those checks in 'production' builds of the library
   float lastTranslationY = Float.NaN;
   boolean lastRefOnly = false;

   private void preCheckTranslationChange(boolean refOnly) {
      lastRefOnly = refOnly;
      lastTranslationY = config.view.getTranslationY();
   }

   private void postCheckTranslationChange(boolean newRefOnly) {
      boolean changedTranslation = config.view.getTranslationY() != lastTranslationY;
      boolean changedRefOnly = lastRefOnly != newRefOnly;

      if (lastRefOnly && changedTranslation)
         throw new RuntimeException("Controllers must not change view translationY when refOnly == true");

      if (changedTranslation && !changedRefOnly)
         throw new RuntimeException("Controllers must return true then changing the view translation");
   }

   private boolean dispatchOnScrollStateChanged(AbstractController controller, boolean refOnly, RecyclerView recyclerView, int newState) {
      preCheckTranslationChange(refOnly);
      refOnly = controller.onScrollStateChanged(refOnly, recyclerView, newState);
      postCheckTranslationChange(refOnly);
      return refOnly;
   }

   private boolean dispatchOnScrolled(AbstractController controller, boolean refOnly, RecyclerView recyclerView, int dx, int dy) {
      preCheckTranslationChange(refOnly);
      refOnly = controller.onScrolled(refOnly, recyclerView, dx, dy);
      postCheckTranslationChange(refOnly);
      return refOnly;
   }

}
