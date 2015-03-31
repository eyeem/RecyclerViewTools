package com.eyeem.recyclerviewtools.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by budius on 12.03.15.
 */
public class WrapHeaderFooterAdapter<VH extends RecyclerView.ViewHolder> extends AbstractHeaderFooterRecyclerAdapter<VH> {

   public static WrapHeaderFooterAdapter wrapMeInThyGlory(RecyclerView recyclerView) {
      RecyclerView.Adapter a = recyclerView.getAdapter();
      if (a instanceof WrapHeaderFooterAdapter)
         return (WrapHeaderFooterAdapter) a;
      WrapHeaderFooterAdapter wrap = new WrapHeaderFooterAdapter(a);
      recyclerView.setAdapter(wrap);
      return wrap;
   }

   // the adapter we're wrapping
   private final RecyclerView.Adapter<VH> wrapped;
   private final int maxNumberHeader;
   private final int maxNumberFooter;

   // easy and complete constructors
   // ==============================================================================================
   public WrapHeaderFooterAdapter(RecyclerView.Adapter<VH> wrapped) {
      this(wrapped, 10, 10);
   }

   public WrapHeaderFooterAdapter(RecyclerView.Adapter<VH> wrapped,
                                  int maxNumberHeader, int maxNumberFooter) {
      this.wrapped = wrapped;
      this.maxNumberHeader = maxNumberHeader;
      this.maxNumberFooter = maxNumberFooter;
      this.wrapped.registerAdapterDataObserver(observer);
      setHasStableIds(wrapped.hasStableIds());
   }

   public RecyclerView.Adapter<VH> getWrappedAdapter() {
      return wrapped;
   }

   // base adapter callbacks
   // ==============================================================================================
   @Override
   public int getItemCount_() {
      return wrapped.getItemCount();
   }

   @Override
   public int getItemViewType_(int position) {
      return wrapped.getItemViewType(position);
   }

   @Override
   public VH onCreateViewHolder_(ViewGroup viewGroup, int viewType) {
      return wrapped.onCreateViewHolder(viewGroup, viewType);
   }

   @Override
   public void onBindViewHolder_(VH viewHolder, int position) {
      wrapped.onBindViewHolder(viewHolder, position);
   }

   // extended adapter callbacks
   // ==============================================================================================
   @Override
   public void onViewAttachedToWindow_(VH holder) {
      wrapped.onViewAttachedToWindow(holder);
   }

   @Override
   public void onViewDetachedFromWindow_(VH holder) {
      wrapped.onViewDetachedFromWindow(holder);
   }

   @Override
   public void onViewRecycled_(VH holder) {
      wrapped.onViewRecycled(holder);
   }

   @Override
   public long getItemId_(int position) {
      return wrapped.getItemId(position);
   }

   // header/footer number limitation
   // ==============================================================================================
   @Override
   public int getMaxNumHeaders() {
      return maxNumberHeader;
   }

   @Override
   public int getMaxNumFooters() {
      return maxNumberFooter;
   }

   private final RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
      @Override
      public void onChanged() {
         notifyDataSetChanged();
      }

      @Override
      public void onItemRangeChanged(int positionStart, int itemCount) {
         _notifyItemRangeChanged(positionStart, itemCount);
      }

      @Override
      public void onItemRangeInserted(int positionStart, int itemCount) {
         _notifyItemRangeInserted(positionStart, itemCount);
      }

      @Override
      public void onItemRangeRemoved(int positionStart, int itemCount) {
         _notifyItemRangeRemoved(positionStart, itemCount);
      }

      @Override
      public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
         for (int i = 0; i < itemCount; i++)
            _notifyItemMoved(fromPosition + i, toPosition + i);
      }
   };
}
