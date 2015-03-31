package com.eyeem.recyclerviewtools.scroll_controller;

/**
 * Created by budius on 30.03.15.
 */
abstract class AbstractController {

   final Builder.Config config;

   AbstractController(Builder.Config config) {
      this.config = config;
   }

   /**
    * Callback method to be invoked when RecyclerView's scroll state changes.
    * <p/>
    * That's the same `onScrollStateChanged` from the {@link android.support.v7.widget.RecyclerView.OnScrollListener RecyclerView.OnScrollListener}
    * with two added boolean flags: refOnly and return value
    *
    * @param refOnly      Reference Only. If true, the controller sub-class should not change the view translationY.
    *                     This gets called only for the purposes of motion tracking
    *                     the MainController will throw exception if translationY is changed.
    * @param recyclerView The RecyclerView whose scroll state has changed.
    * @param newState     The updated scroll state. One of
    *                     {@link android.support.v7.widget.RecyclerView#SCROLL_STATE_IDLE SCROLL_STATE_IDLE},
    *                     {@link android.support.v7.widget.RecyclerView#SCROLL_STATE_DRAGGING SCROLL_STATE_DRAGGING} or
    *                     {@link android.support.v7.widget.RecyclerView#SCROLL_STATE_SETTLING SCROLL_STATE_SETTLING}.
    * @return true, if this controller changed the view translationY, false otherwise.
    */
   boolean onScrollStateChanged(boolean refOnly, android.support.v7.widget.RecyclerView recyclerView, int newState) {
      return false;
   }

   /**
    * Callback method to be invoked when the RecyclerView has been scrolled. This will be
    * called after the scroll has completed.
    * <p/>
    * This callback will also be called if visible item range changes after a layout
    * calculation. In that case, dx and dy will be 0.
    * <p/>
    * That's the same `onScrollStateChanged` from the {@link android.support.v7.widget.RecyclerView.OnScrollListener RecyclerView.OnScrollListener}
    * with two added boolean flags: refOnly and return value
    *
    * @param refOnly      Reference Only. If true, the controller sub-class should not change the view translationY.
    *                     This gets called only for the purposes of motion tracking
    *                     the MainController will throw exception if translationY is changed.
    * @param recyclerView The RecyclerView which scrolled.
    * @param dx           The amount of horizontal scroll.
    * @param dy           The amount of vertical scroll.
    * @return true, if this controller changed the view translationY, false otherwise.
    */
   boolean onScrolled(boolean refOnly, android.support.v7.widget.RecyclerView recyclerView, int dx, int dy) {
      return false;
   }

   /**
    * Callback method to be invoked when the global layout state or the visibility of views
    * within the view tree changes.
    * <p/>
    * That's the same from {@link android.view.ViewTreeObserver.OnGlobalLayoutListener}
    * with two added boolean flags: refOnly and return value
    * <p/>
    * At the moment those flags are not really being used. But just for possible future usage.
    *
    * @param refOnly Reference Only. If true, the controller sub-class should not change the view translationY.
    *                This gets called only for the purposes of motion tracking
    * @return true, if this controller changed the view translationY, false otherwise.
    */
   boolean onGlobalLayout(boolean refOnly) {
      return false;
   }

   /**
    * Override this to indicate that this controller is processing some animated scroll
    *
    * @return true for processing animated scroll
    */
   protected boolean isProcessing() {
      return false;
   }
}
