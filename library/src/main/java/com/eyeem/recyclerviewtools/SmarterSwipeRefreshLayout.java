package com.eyeem.recyclerviewtools;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;

/**
 * Created by budius on 25.03.15.
 * <p/>
 * Simple extension of `SwipeRefreshLayout` that allows
 * scrollable child view to be specified by XML or setter.
 * <p/>
 * The main point of this extension, is to customise the target invocation of `canChildScrollUp()`.
 * <p/>
 * Assigning a target, will compute directly on the target. For example a RecyclerView
 * inside a more complex view hierarchy with the SwipeRefreshLayout atop everything.
 * <p/>
 * Assigning an AppBarLayout, will compute for variations in the AppBarLayout offset change.
 * For example a RecyclerView with CoordinatorLayout and other scrolling elements.
 * <p/>
 * If no target view is specified the default implementation is invoked.
 */
public class SmarterSwipeRefreshLayout extends SwipeRefreshLayout implements AppBarLayout.OnOffsetChangedListener {

   public SmarterSwipeRefreshLayout(Context context) {
      super(context);
   }

   public SmarterSwipeRefreshLayout(Context context, AttributeSet attrs) {
      super(context, attrs);
      TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SmarterSwipeRefreshLayout);
      targetResId = a.getResourceId(R.styleable.SmarterSwipeRefreshLayout_target, -1);
      appbarLayoutResId = a.getResourceId(R.styleable.SmarterSwipeRefreshLayout_appBarLayout, -1);
      a.recycle();
   }

   private int appbarLayoutResId = -1;
   private int targetResId = -1;
   private View target = null;
   private AppBarLayout appBarLayout;
   private boolean appBarExpanded = true;

   @Override protected void onAttachedToWindow() {
      super.onAttachedToWindow();

      if (targetResId > 0 && target == null) {
         target = findViewById(targetResId);
      }

      if (appbarLayoutResId > 0 && appBarLayout == null) {
         appBarLayout = (AppBarLayout) findViewById(appbarLayoutResId);
         if (appBarLayout == null) {

            // we want to try to find the AppBarLayout anywhere in the layout,
            // even if not direct child of this view.
            ViewParent parent = getParent();
            ViewParent _parent = null;
            while (parent != null) { // navigate up the view hierarchy to the activity root
               _parent = parent;
               if (_parent instanceof View && ((View) _parent).getId() == android.R.id.content)
                  break;
               parent = parent.getParent();
            }
            if (_parent instanceof View)
               appBarLayout = (AppBarLayout) ((View) _parent).findViewById(appbarLayoutResId);
         }
      }

      if (appBarLayout != null)
         appBarLayout.addOnOffsetChangedListener(this);

      try {
         AppBarLayout.Behavior b = (AppBarLayout.Behavior)
            ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).getBehavior();
         int currentOffset = b.getTopAndBottomOffset();
         onOffsetChanged(appBarLayout, currentOffset);
      } catch (Exception ignored) { /* so much stuff can go wrong on the above call */ }
   }

   @Override protected void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      if (appBarLayout != null)
         appBarLayout.removeOnOffsetChangedListener(this);
   }

   /**
    * Setter for the target scrollable view.
    *
    * @param target       the scroll target. Usually a RecyclerView
    * @param appBarLayout (optional) AppBarLayout in case the target is inside a Coordinator layout
    */
   public void setTarget(View target, @Nullable AppBarLayout appBarLayout) {
      this.target = target;
      this.appBarLayout = appBarLayout;
   }

   @Override
   public boolean canChildScrollUp() {
      if (target != null)
         return !appBarExpanded || ViewCompat.canScrollVertically(target, -1);
      else
         return super.canChildScrollUp();
   }

   @Override public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
      appBarExpanded = (i >= 0);
   }
}
