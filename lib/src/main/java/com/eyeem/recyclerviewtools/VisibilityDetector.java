package com.eyeem.recyclerviewtools;

import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by budius on 30.03.15.
 * Smart detects number of visible pixels changed inside a scrollable container.
 * Great to use on Parallax effect.
 * <p/>
 * Usage is simple. Just call {@link #registerOnScrollChangedListener()} (or the variant
 * {@link #registerAndDispatchOnScrollChangedListener()}) and
 * {@link #unregisterOnScrollChangedListener()} on the Activity, Fragment or View life-cycle.
 */
public class VisibilityDetector {

   private static final int ANDROID_PARENT_ID = android.R.id.content;

   public static final int HORIZONTAL = LinearLayoutManager.HORIZONTAL;
   public static final int VERTICAL = LinearLayoutManager.VERTICAL;

   private final View view;
   private final int orientation;
   private final Listener listener;
   private int parentId = ANDROID_PARENT_ID;

   private ViewTreeObserver.OnScrollChangedListener viewTreeObserver;
   private final Rect tempRect = new Rect();
   private final Point tempPoint = new Point();
   private final Point lastNumberOfVisiblePixels = new Point();
   private final PointF lastPercentOfVisiblePixels = new PointF();

   /**
    * Default constructor.
    *
    * @param view        the view to calculate the visible pixels
    * @param orientation direction to calculate the pixels. One of {@link #HORIZONTAL} or {@link #VERTICAL}
    * @param listener    callback to receive changes
    * @param parentId    (optional) optimisation. The detector must traverse up the view hierarchy to find a known parent.
    *                    If no parentId is supplied, this detector will use android.R.id.content.
    *                    It's faster for this processing to be done on the closest parent possible
    *                    This is usually done on a scrollable container such as ScrollView, ListView or RecyclerView
    */
   public VisibilityDetector(View view, int orientation, Listener listener, @IdRes int parentId) {
      this.view = view;
      this.orientation = orientation;
      this.listener = listener;
      setParentId(parentId);
   }

   // public interface
   // ==============================================================================================

   /**
    * Register this detector to start detecting.
    * This is usually called from {@link android.app.Activity#onResume() onResume()} for Fragment or Activities
    * or {@link android.view.View#onAttachedToWindow() onAttachedToWindow()} for CustomViews
    */
   public void registerOnScrollChangedListener() {
      // update to last known good value
      Point p = getNumberOfVisiblePixels();
      lastNumberOfVisiblePixels.set(p.x, p.y);
      view.getViewTreeObserver().addOnScrollChangedListener(getOnScrollChangedListener());
   }

   /**
    * Same as {@link #registerOnScrollChangedListener()} but also dispatch the current value to the listener.
    * That's useful to run a first update on the view.
    */
   public void registerAndDispatchOnScrollChangedListener() {
      // force bad value to force trigger listener
      lastNumberOfVisiblePixels.set(Integer.MIN_VALUE, Integer.MIN_VALUE);
      onViewScrolled(); // here it re-calculates scroll and dispatches
      registerOnScrollChangedListener(); // register to listen to future updates
   }

   /**
    * Unregister this detector from detecting.
    * This is usually called from {@link android.app.Activity#onPause() onPause()} for Fragment or Activities
    * or {@link android.view.View#onDetachedFromWindow() onDetachedFromWindow()} for CustomViews
    */
   public void unregisterOnScrollChangedListener() {
      view.getViewTreeObserver().removeOnScrollChangedListener(getOnScrollChangedListener());
   }

   // private helpers
   // ==============================================================================================
   private void onViewScrolled() {
      Point p = getNumberOfVisiblePixels();
      if (!p.equals(lastNumberOfVisiblePixels)) {
         lastNumberOfVisiblePixels.set(p.x, p.y);
         lastPercentOfVisiblePixels.set(
            ((float) p.x / (float) view.getWidth()),
            ((float) p.y / (float) view.getHeight())
         );

         // percent is NaN when getHeight || getWidth == 0; That means the view was never layout
         // Here we don't want to dispatch to the listener
         // to avoid listener being fully visible but received a 0pxs callback
         if (Float.isNaN(lastPercentOfVisiblePixels.x) || Float.isNaN(lastPercentOfVisiblePixels.y))
            return;

         listener.onViewVisibilityChanged(view, lastNumberOfVisiblePixels, lastPercentOfVisiblePixels);
      }
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

   private ViewTreeObserver.OnScrollChangedListener getOnScrollChangedListener() {
      if (viewTreeObserver == null) {
         viewTreeObserver = new ViewTreeObserver.OnScrollChangedListener() {
            @Override public void onScrollChanged() {
               onViewScrolled();
            }
         };
      }
      return viewTreeObserver;
   }

   private Point getNumberOfVisiblePixels() {
      if (Tools.isViewInsideLayout(view, parentId)) {
         boolean result = view.getGlobalVisibleRect(tempRect);
         if (result) {
            tempPoint.set(tempRect.width(), tempRect.height());
            return tempPoint;
         }
      }
      tempPoint.set(0, 0);
      return tempPoint;
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
}
