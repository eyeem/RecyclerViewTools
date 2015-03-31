package com.eyeem.recyclerviewtools;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by budius on 25.03.15.
 * <p/>
 * Simple extension of `SwipeRefreshLayout` that allows
 * scrollable child view to be specified by XML or setter.
 * <p/>
 * The main point of this extension, is that it allows a ViewGroup to wrap a RecyclerView
 * and have the `canChildScrollUp()` still be computed directly on the RecyclerView.
 * This allows the ViewGroup to have views that are controlled by our scroll_controllers
 * whilst the `MaterialProgressDrawable` is still drawn on top of everything.
 * <p/>
 * If no view is specified the default implementation is invoked.
 */
public class SmarterSwipeRefreshLayout extends SwipeRefreshLayout {

   public SmarterSwipeRefreshLayout(Context context) {
      super(context);
   }

   public SmarterSwipeRefreshLayout(Context context, AttributeSet attrs) {
      super(context, attrs);
      TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SmarterSwipeRefreshLayout);
      targetResId = a.getResourceId(R.styleable.SmarterSwipeRefreshLayout_target, -1);
      a.recycle();
   }

   private int targetResId = -1;
   private View target = null;

   /**
    * Setter for the target scrollable view.
    *
    * @param target child view to be queried with `ViewCompat.canScrollVertically(View, int);`
    */
   public void setTarget(View target) {
      this.target = target;
   }

   @Override public boolean canChildScrollUp() {

      if (targetResId > 0 && target == null) {
         target = findViewById(targetResId);
         if (target != null)
            Log.d(this, "Found target view " + target.getClass().getSimpleName());
      }

      if (target != null)
         return ViewCompat.canScrollVertically(target, -1);
      else
         return super.canChildScrollUp();
   }
}
