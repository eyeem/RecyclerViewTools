package com.eyeem.recyclerviewtools;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.eyeem.recyclerviewtools.adapter.WrapAdapter;

/**
 * RecyclerView.ItemDecoration that adds space/offset in-between views.
 * It takes into account internal and external offset.
 * Useful for layout manager that might have several columns.
 * <p/>
 * This does not deal with Sections and assumes views are 1 span each.
 */
public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

   /**
    * this value is copied from WrapAdapter
    * because I really don't wanna make the method `isInternalHolder(Holder)` public.
    * So I can just here check against the mask used for special view types
    */
   private static final int MAIN_VIEW_TYPE_MASK = 0x40000000;

   private final Rect externalOffset;
   private final Rect internalOffset;
   private final boolean disableSpecialViewsSpacing;

   /**
    * Constructor where all internal and external item offsets are the same
    *
    * @param offsetLeft
    * @param offsetTop
    * @param offsetRight
    * @param offsetBottom
    * @param disableHeaderSpacing if header should have offset or should be all 0
    */
   public ItemOffsetDecoration(int offsetLeft,
                               int offsetTop,
                               int offsetRight,
                               int offsetBottom,
                               boolean disableHeaderSpacing) {
      this(
            new Rect(offsetLeft, offsetTop, offsetRight, offsetBottom),
            new Rect(offsetLeft, offsetTop, offsetRight, offsetBottom),
            disableHeaderSpacing);
   }

   /**
    * Constructor with separate offset for internal or external views
    *
    * @param externalOffset
    * @param internalOffset
    * @param disableSpecialViewsSpacing if header, footer, sections or custom view should have zero spacing
    */
   public ItemOffsetDecoration(Rect externalOffset, Rect internalOffset, boolean disableSpecialViewsSpacing) {
      this.externalOffset = externalOffset;
      this.internalOffset = internalOffset;
      this.disableSpecialViewsSpacing = disableSpecialViewsSpacing;
   }

   /**
    * Constructor with separate offset for internal or external views
    *
    * @param externalOffsetLeft
    * @param externalOffsetTop
    * @param externalOffsetRight
    * @param externalOffsetBottom
    * @param internalOffsetLeft
    * @param internalOffsetTop
    * @param internalOffsetRight
    * @param internalOffsetBottom
    * @param disableHeaderSpacing if header should have offset or should be all 0
    */
   public ItemOffsetDecoration(
         int externalOffsetLeft,
         int externalOffsetTop,
         int externalOffsetRight,
         int externalOffsetBottom,
         int internalOffsetLeft,
         int internalOffsetTop,
         int internalOffsetRight,
         int internalOffsetBottom,
         boolean disableHeaderSpacing) {
      this(new Rect(externalOffsetLeft, externalOffsetTop, externalOffsetRight, externalOffsetBottom),
            new Rect(internalOffsetLeft, internalOffsetTop, internalOffsetRight, internalOffsetBottom),
            disableHeaderSpacing);
   }

   @Override
   public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

      WrapAdapter wrapAdapter = null;
      RecyclerView.Adapter adapter = parent.getAdapter();

      int adapterCount = adapter.getItemCount();

      if (adapter instanceof WrapAdapter) {
         wrapAdapter = (WrapAdapter) adapter;
         adapter = wrapAdapter.getWrapped();
      }

      // make exception for wrapAdapter special view types
      if (wrapAdapter != null) {
         RecyclerView.ViewHolder vh = parent.getChildViewHolder(view);

         // if it's a special view
         if (vh.getItemViewType() > MAIN_VIEW_TYPE_MASK) {
            if (disableSpecialViewsSpacing) {
               outRect.set(0, 0, 0, 0);
            } else {
               outRect.set(externalOffset);
            }
            return;
         }
      }

      int numHeaders = (wrapAdapter == null) ? 0 : wrapAdapter.getHeaderCount();
      int adapterPosition = parent.getChildAdapterPosition(view);
      int offsetAdapterPosition = adapterPosition - numHeaders;

      int spanCount = 1;
      RecyclerView.LayoutManager lm = parent.getLayoutManager();
      if (lm instanceof GridLayoutManager) {
         spanCount = ((GridLayoutManager) lm).getSpanCount();
      } else if (lm instanceof StaggeredGridLayoutManager) {
         spanCount = ((StaggeredGridLayoutManager) lm).getSpanCount();
      }

      final boolean isTop = offsetAdapterPosition < spanCount;
      final boolean isBottom = adapterCount - adapterPosition < spanCount;

      boolean isLeft;
      boolean isRight;

      ViewGroup.LayoutParams lp = view.getLayoutParams();

      // Grid
      if (lp instanceof GridLayoutManager.LayoutParams) {
         GridLayoutManager.LayoutParams glp = (GridLayoutManager.LayoutParams) lp;

         isLeft = glp.getSpanIndex() == 0;
         isRight = glp.getSpanIndex() + glp.getSpanSize() == spanCount;

      }

      // StaggeredGrid
      else if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
         StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams) lp;

         isLeft = sglp.getSpanIndex() == 0;
         isRight = sglp.getSpanIndex() == spanCount - 1;

      }

      // Assume Linear
      else {

         int itemColumn = offsetAdapterPosition % spanCount;
         isLeft = itemColumn == 0;
         isRight = itemColumn == spanCount - 1;

      }

      outRect.set(internalOffset);

      if (isTop) {
         outRect.top = externalOffset.top;
      }
      if (isBottom) {
         outRect.bottom = externalOffset.bottom;
      }
      if (isLeft) {
         outRect.left = externalOffset.left;
      }
      if (isRight) {
         outRect.right = externalOffset.right;
      }
   }

}

