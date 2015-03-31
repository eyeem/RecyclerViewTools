package com.eyeem.recyclerviewtools.scroll_controller;

import android.os.SystemClock;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.Scroller;

import com.eyeem.recyclerviewtools.Log;

import static com.eyeem.recyclerviewtools.scroll_controller.Builder.Config;

/**
 * Created by budius on 30.03.15.
 * This controller snaps the view to initial or final positions depending on
 * the movement direction and velocity.
 * Used for FLAG_SNAP_TO
 */
class _SnapToController extends AbstractController implements Runnable {

   // private variables of the `snap to` logic
   private float X, Y;
   private int numberOfActionMoveRegistered;
   private VelocityTracker velocityTracker;
   private MotionEvent motionEvent;
   private Scroller scroller;

   private boolean processingSnapTo = false;

   _SnapToController(Config config) {
      super(config);
      scroller = new Scroller(config.view.getContext());
   }

   @Override protected boolean isProcessing() {
      return processingSnapTo;
   }

   @Override
   boolean onScrollStateChanged(boolean refOnly, RecyclerView recyclerView, int newState) {

      switch (newState) {

         // start counting the user movement
         // ====================================
         case RecyclerView.SCROLL_STATE_DRAGGING:

            Log.d(this, "Starting track movement");

            // abort and reset previous states
            scroller.abortAnimation();
            processingSnapTo = false;
            numberOfActionMoveRegistered = 0;
            X = 0f;
            Y = 0f;

            // obtain and init VelocityTracker and MotionEvent
            if (velocityTracker == null) {
               velocityTracker = VelocityTracker.obtain();
               velocityTracker.clear();
            }
            if (motionEvent == null) {
               motionEvent = MotionEvent.obtain(SystemClock.uptimeMillis(),
                  SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0f, 0f, 0);
            }

            // add `ACTION_DOWN` movement
            velocityTracker.addMovement(motionEvent);

            return false;

         // end user movement and start `snap to` animation
         // ===============================================
         case RecyclerView.SCROLL_STATE_SETTLING:

            // user is not touching the view anymore,
            // but not enough movement was registered yet
            // just don't do anything and re-process on the STATE_IDLE callback
            if (numberOfActionMoveRegistered < 3) return false;

            // if that is for reference only,
            // we don't have anything more to process
            // maybe on STAT_IDLE we'll have be able to process, maybe not
            if (refOnly) return false;

         case RecyclerView.SCROLL_STATE_IDLE:

            // we've been calculating velocity and movement, so process it
            if (velocityTracker != null && motionEvent != null) {

               // change to ACTION_UP and add to tracker
               motionEvent.setAction(MotionEvent.ACTION_UP);
               motionEvent.addBatch(SystemClock.uptimeMillis(), X, Y, 1, 1, 0);
               velocityTracker.addMovement(motionEvent);

               // get velocity
               velocityTracker.computeCurrentVelocity(1000);
               float velocity = velocityTracker.getYVelocity();

               // zero velocity is an undetermined state
               // if it's still settling, we can wait for the view to scroll more
               // to try to detect a proper state
               if (newState == RecyclerView.SCROLL_STATE_SETTLING && velocity == 0f) {
                  return false;
               }

               // clear VelocityTracker and MotionEvent
               velocityTracker.clear();
               velocityTracker.recycle();
               velocityTracker = null;
               motionEvent.recycle();
               motionEvent = null;

               if (refOnly) {
                  // somebody else processed the this movement.
                  Log.d(this, "Ending track movement. refOnly");
                  return false;
               }

               boolean show = velocity < 0;

               // calculate start/final/delta positions
               int startY = (int) config.view.getTranslationY();
               int endY = show ? 0 : config.getLimit();
               int dy = endY - startY;

               if (dy == 0) {
                  // nothing to change
                  Log.d(this, "Ending track movement. Nothing to change");
                  return false;
               } else {
                  // scroll to position
                  Log.d(this, "Ending track movement. Velocity is " + velocity +
                     ". Moving to " + (show ? "shown" : "hidden") + " position.");

                  // TODO: Improvement.  Use velocity to calculate `duration` for the scroll.
                  processingSnapTo = true;
                  scroller.startScroll(0, startY, 0, dy);
                  ViewCompat.postOnAnimation(config.view, _SnapToController.this);
                  return true;
               }

            }
      }
      return false;
   }

   @Override boolean onScrolled(boolean refOnly, RecyclerView recyclerView, int dx, int dy) {
      if (velocityTracker != null && motionEvent != null) {

         numberOfActionMoveRegistered++;

         // change to ACTION_MOVE
         motionEvent.setAction(MotionEvent.ACTION_MOVE);

         // update "finger position", add to the event, add event to tracker
         X += dx;
         Y += dy;
         motionEvent.addBatch(SystemClock.uptimeMillis(), X, Y, 1, 1, 0);
         velocityTracker.addMovement(motionEvent);
      }
      return false;
   }

   @Override public void run() {
      // keeps the animation running until the end
      if (scroller.computeScrollOffset()) {
         processingSnapTo = true;
         config.view.setTranslationY(scroller.getCurrY());
         ViewCompat.postOnAnimation(config.view, _SnapToController.this);
      } else {
         processingSnapTo = false;
         Log.d(this, "Snap complete.");
      }
   }
}