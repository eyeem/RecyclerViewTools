package com.eyeem.recyclerviewtools;

import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by budius on 30.03.15.
 * Smart detects number of visible pixels changed inside a scrollable container.
 * Great to use on Parallax effect.
 * <p/>
 * This detector can run on either a RecyclerView by calling {@link #getRecyclerViewScrollListener()}
 * or in any scrollable view by calling {@link #getViewTreeObserverOnScrollChangedListener()} and
 * registering those listeners on their correct place.
 */
public class ParallaxDetector implements ViewTreeObserver.OnGlobalLayoutListener {

   private static final int ANDROID_PARENT_ID = android.R.id.content;

   private final View view;
   private final Listener listener;
   private int parentId = ANDROID_PARENT_ID;

   private final Rect tempRect = new Rect();
   private final Point tempPoint = new Point();
   private final Point lastNumberOfVisiblePixels = new Point();
   private final PointF lastPercentOfVisiblePixels = new PointF();

   private RecyclerViewOnScrollListener recyclerViewScrollListener;
   private ViewTreeObserverOnScrollChangedListener viewTreeObserverOnScrollChangedListener;

   /**
    * Default constructor.
    *
    * @param view     the view to calculate the visible pixels
    * @param listener callback to receive changes
    * @param parentId (optional) optimisation. The detector must traverse up the view hierarchy to find a known parent.
    *                 If no parentId is supplied, this detector will use android.R.id.content.
    *                 It's faster for this processing to be done on the closest parent possible
    *                 This is usually done on a scrollable container such as ScrollView, ListView or RecyclerView
    */
   public ParallaxDetector(View view, Listener listener, @IdRes int parentId) {
      this.view = view;
      this.listener = listener;
      setParentId(parentId);
      tryDispatchInitialOnViewScrolledCallback();
   }

   // public interface
   // ==============================================================================================
   public RecyclerView.OnScrollListener getRecyclerViewScrollListener() {
      if (viewTreeObserverOnScrollChangedListener != null)
         throw new RuntimeException(MUTUAL_EXCLUSIVE_EXCEPTION);
      if (recyclerViewScrollListener == null)
         recyclerViewScrollListener = new RecyclerViewOnScrollListener();
      return recyclerViewScrollListener;
   }

   public ViewTreeObserver.OnScrollChangedListener getViewTreeObserverOnScrollChangedListener() {
      if (recyclerViewScrollListener != null)
         throw new RuntimeException(MUTUAL_EXCLUSIVE_EXCEPTION);
      if (viewTreeObserverOnScrollChangedListener == null)
         viewTreeObserverOnScrollChangedListener = new ViewTreeObserverOnScrollChangedListener();
      return viewTreeObserverOnScrollChangedListener;
   }

   // private helpers
   // ==============================================================================================
   public boolean onViewScrolled() {
      return onViewScrolled(false);
   }

   private boolean onViewScrolled(boolean force) {

      if (view.getWidth() == 0 || view.getHeight() == 0)
         return false;

      Point p = getNumberOfVisiblePixels();
      if (force || !p.equals(lastNumberOfVisiblePixels)) {

         // set values
         lastNumberOfVisiblePixels.set(p.x, p.y);
         lastPercentOfVisiblePixels.set(
            ((float) p.x / (float) view.getWidth()),
            ((float) p.y / (float) view.getHeight())
         );

         // percent is NaN when getHeight || getWidth == 0; That means the view was never layout
         // Here we don't want to dispatch to the listener
         // to avoid listener being fully visible but received a 0pxs callback
         if (Float.isNaN(lastPercentOfVisiblePixels.x) || Float.isNaN(lastPercentOfVisiblePixels.y))
            return false;

         listener.onViewVisibilityChanged(view, lastNumberOfVisiblePixels, lastPercentOfVisiblePixels);
      }
      return true;
   }

   private void setParentId(@IdRes int parentId) {
      if (parentId > 0)
         try {
            if (view.getResources().getResourceTypeName(parentId).equals("id")) {
               this.parentId = parentId;
            }
         } catch (Resources.NotFoundException e) {
         /* if it's not a valid ID we'll just use android.R.id.content */
         }
      this.parentId = ANDROID_PARENT_ID;
   }

   private Point getNumberOfVisiblePixels() {
      return Tools.getNumberOfVisiblePixels(view, parentId, tempPoint, tempRect);
   }

   private void tryDispatchInitialOnViewScrolledCallback() {
      if (!onViewScrolled(true)) {
         view.getViewTreeObserver().addOnGlobalLayoutListener(this);
      }
   }

   @Override public void onGlobalLayout() {
      if (onViewScrolled(true)) {
         view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
      }
   }

   // callback interface
   // ==============================================================================================

   /**
    * Interface to receive callbacks when the view visibility changes.
    */
   public interface Listener {
      /**
       * Notifies that the view visibility was changed.
       *
       * @param view    the view the detection is running on
       * @param pixels  the number of pixels currently visible on this view
       * @param percent the percentage of pixels currently visible on this view
       */
      public void onViewVisibilityChanged(View view, Point pixels, PointF percent);
   }

   // possible scroll detection techniques
   // mutually exclusive detections, implementing applications should use one or the other
   // ==============================================================================================
   private static final String MUTUAL_EXCLUSIVE_EXCEPTION =
      "Can only use ViewTreeObserver.OnScrollChangedListener or RecyclerView.OnScrollListener, but not both together";

   private class RecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {
      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
         onViewScrolled();
      }

      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
         onViewScrolled();
      }
   }

   private class ViewTreeObserverOnScrollChangedListener implements ViewTreeObserver.OnScrollChangedListener {

      @Override public void onScrollChanged() {
         onViewScrolled();
      }
   }
}
