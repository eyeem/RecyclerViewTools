package com.eyeem.recyclerviewtools.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.eyeem.recyclerviewtools.OnItemClickListenerDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by budius on 12.03.15.
 */
public abstract class AbstractHeaderFooterRecyclerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {

   // Lazy initialised list of headers and footers
   private List<View> headers;
   private List<View> footers;

   // abstract mirrored methods, those are basically the same from RecyclerView.Adapter
   // ==============================================================================================
   public abstract int getItemCount_();

   public abstract VH onCreateViewHolder_(ViewGroup viewGroup, int viewType);

   public abstract void onBindViewHolder_(VH viewHolder, int position);

   // annoyance for sure, but necessary to keep flexibility
   // ==============================================================================================

   /**
    * Informs how many headers this adapter will have at maximum.
    * This method will only be called once after construction, the value cannot be changed after that.
    *
    * @return the max number of headers this adapter can have.
    */
   public abstract int getMaxNumHeaders();

   /**
    * Informs how many footers this adapter will have at maximum.
    * This method will only be called once after construction, the value cannot be changed after that.
    *
    * @return the max number of footers this adapter can have.
    */
   public abstract int getMaxNumFooters();

   // public interface to add/remove headers/footers and generate SpanSizeLookUp
   // ==============================================================================================
   public void addHeader(View v) {
      if (getNumberOfHeaders() == MAX_NUM_HEADERS())
         throw new RuntimeException("Exceed maximum allowed number of headers");
      setDefaultLayoutParams(v);
      getHeaders().add(v);
      super.notifyItemInserted(getNumberOfHeaders() - 1);
   }

   public void addFooter(View v) {
      if (getNumberOfFooters() == MAX_NUM_FOOTERS())
         throw new RuntimeException("Exceed maximum allowed number of footers");
      setDefaultLayoutParams(v);
      getFooters().add(v);
      super.notifyItemInserted(getItemCount());
   }

   public void clearHeaders() {
      int val = getNumberOfHeaders();
      if (val > 0) {
         getHeaders().clear();
         super.notifyItemRangeRemoved(0, val);
      }
   }

   public void clearFooters() {
      int val = getNumberOfFooters();
      if (val > 0) {
         getFooters().clear();
         super.notifyItemRangeRemoved(getItemCount(), val);
      }
   }

   /**
    * Generates a `GridLayoutManager.SpanSizeLookup` that makes headers and footers
    * take the whole `spanCount`, whilst keeping the wrapped SpanSizeLookup for other positions.
    *
    * @param from      the SpanSizeLookup to wrap from
    * @param spanCount the number of spans
    * @return
    */
   public GridLayoutManager.SpanSizeLookup generateSpanSizeLookup(GridLayoutManager.SpanSizeLookup from, int spanCount) {
      return new GridSpanSize(from, spanCount);
   }

   /**
    * Generates a `GridLayoutManager.SpanSizeLookup` that makes headers and footers
    * take the whole `spanCount`, whilst keeping the wrapped SpanSizeLookup for other positions.
    *
    * @param spanCount the number of spans
    * @return
    */
   public GridLayoutManager.SpanSizeLookup generateSpanSizeLookup(int spanCount) {
      return new GridSpanSize(new GridLayoutManager.DefaultSpanSizeLookup(), spanCount);
   }

   // getItemViewType, onViewAttachedToWindow, onViewDetachedFromWindow and onViewRecycled are 'final',
   // so this is here to supply the same functionality to extending classes
   // ==============================================================================================

   /**
    * Return the view type of the item at position for the purposes of view recycling.
    * Headers and Footers use a range from Integer.MAX_VALUE to Integer.MAX_VALUE - MAX_NUM_HEADERS - MAX_NUM_FOOTERS
    * Extending classes must not use anything on that range
    *
    * @param position position to query
    * @return integer value identifying the type of the view needed to represent the item at position. Type codes need not be contiguous. Type codes must not clash with header or footer types.
    */
   public int getItemViewType_(int position) {
      return 0;
   }

   public long getItemId_(int position) {
      return super.getItemId(position);
   }

   public void onViewAttachedToWindow_(VH holder) { /* default does nothing */ }

   public void onViewDetachedFromWindow_(VH holder) { /* default does nothing */ }

   public void onViewRecycled_(VH holder) { /* default does nothing */ }

   // private header/footer related helpers
   // ==============================================================================================
   public boolean isHeaderPosition(int position) {
      return position < getNumberOfHeaders();
   }

   public boolean isFooterPosition(int position) {
      return position >= footersOffset();
   }

   public int getNumberOfHeaders() {
      return headers == null ? 0 : headers.size();
   }

   public int getNumberOfFooters() {
      return footers == null ? 0 : footers.size();
   }

   private boolean isHeaderViewType(int viewType) {
      return viewType > (Integer.MAX_VALUE - MAX_NUM_HEADERS());
   }

   private boolean isFooterViewType(int viewType) {
      if (isHeaderViewType(viewType)) return false;
      return viewType > (Integer.MAX_VALUE - MAX_NUM_HEADERS() - MAX_NUM_FOOTERS());
   }

   private List<View> getHeaders() {
      if (headers == null)
         headers = new ArrayList<View>();
      return headers;
   }

   private List<View> getFooters() {
      if (footers == null)
         footers = new ArrayList<View>();
      return footers;
   }

   private int footersOffset() {
      return getNumberOfHeaders() + getItemCount_();
   }

   private void setDefaultLayoutParams(View v) {
      if (v.getLayoutParams() == null) {
         RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
         v.setLayoutParams(lp);
      }
   }

   // final methods. Those have been finalised from RecyclerView.Adapter to avoid breaking the positioning
   // ==============================================================================================
   @Override
   public final int getItemCount() {
      return getItemCount_() + getNumberOfFooters() + getNumberOfHeaders();
   }

   @Override
   public final long getItemId(int position) {

      // we only do stuff if there are stable IDs to work with
      if (!hasStableIds()) return super.getItemId(position);

      // this header and footer code is pretty much the same as in 'getItemViewType',
      // the difference is because here we return Long, instead of Int, the MAX value is Long.MAX_VALUE
      if (isHeaderPosition(position)) {
         // headers goes from MAX to (MAX - numHeaders)
         return Long.MAX_VALUE - position;
      } else if (isFooterPosition(position)) {
         // footers goes from (MAX - numHeaders) to (MAX - numHeaders - numFooters)
         return Long.MAX_VALUE - MAX_NUM_HEADERS() - (position - footersOffset());
      } else {
         return getItemId_(position - MAX_NUM_HEADERS());
      }
   }

   @Override
   public final int getItemViewType(int position) {
      if (isHeaderPosition(position)) {
         // headers goes from MAX to (MAX - numHeaders)
         return Integer.MAX_VALUE - position;
      } else if (isFooterPosition(position)) {
         // footers goes from (MAX - numHeaders) to (MAX - numHeaders - numFooters)
         return Integer.MAX_VALUE - MAX_NUM_HEADERS() - (position - footersOffset());
      } else {
         int val = getItemViewType_(position - getNumberOfHeaders());
         if (val >= (Integer.MAX_VALUE - MAX_NUM_HEADERS() - MAX_NUM_FOOTERS()))
            throw new IllegalArgumentException("item view type for recycled views must be smaller than (Integer.MAX_VALUE - MAX_NUM_HEADERS - MAX_NUM_FOOTERS)");
         return val;
      }
   }

   @Override
   public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
      RecyclerView.ViewHolder vh;
      if (isHeaderViewType(viewType)) {
         int headerPosition = Integer.MAX_VALUE - viewType;
         vh = new InternalHeaderFooterViewHolder(getHeaders().get(headerPosition));
      } else if (isFooterViewType(viewType)) {
         int footerPosition = Integer.MAX_VALUE - MAX_NUM_HEADERS() - viewType;
         vh = new InternalHeaderFooterViewHolder(getFooters().get(footerPosition));
      } else {
         vh = onCreateViewHolder_(viewGroup, viewType);
      }
      if (onItemClickListenerDetector != null)
         vh.itemView.setOnClickListener(onItemClickListenerDetector);
      return vh;
   }

   @Override
   public final void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
      if (viewHolder instanceof InternalHeaderFooterViewHolder) return;
      onBindViewHolder_((VH) viewHolder, position - getNumberOfHeaders());
   }

   @Override
   public final void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
      if (viewHolder instanceof InternalHeaderFooterViewHolder) return;
      onViewAttachedToWindow_((VH) viewHolder);
   }

   @Override
   public final void onViewDetachedFromWindow(RecyclerView.ViewHolder viewHolder) {
      if (viewHolder instanceof InternalHeaderFooterViewHolder) return;
      onViewDetachedFromWindow_((VH) viewHolder);
   }

   @Override
   public final void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
      if (viewHolder instanceof InternalHeaderFooterViewHolder) return;
      onViewRecycled_((VH) viewHolder);
   }

   // Those have been re-worked to properly mirror the position
   // unfortunately the native methods are final and I can't override them
   // call to them might/will produce wrong effects
   // ==============================================================================================
   public final void _notifyItemChanged(int position) {
      notifyItemChanged(position + getNumberOfHeaders());
   }

   public final void _notifyItemInserted(int position) {
      notifyItemInserted(position + getNumberOfHeaders());
   }

   public final void _notifyItemMoved(int fromPosition, int toPosition) {
      notifyItemMoved(fromPosition + getNumberOfHeaders(), toPosition + getNumberOfHeaders());
   }

   public final void _notifyItemRangeChanged(int positionStart, int itemCount) {
      notifyItemRangeChanged(positionStart + getNumberOfHeaders(), itemCount);
   }

   public final void _notifyItemRangeInserted(int positionStart, int itemCount) {
      notifyItemRangeInserted(positionStart + getNumberOfHeaders(), itemCount);
   }

   public final void _notifyItemRangeRemoved(int positionStart, int itemCount) {
      notifyItemRangeRemoved(positionStart + getNumberOfHeaders(), itemCount);
   }

   public final void _notifyItemRemoved(int position) {
      notifyItemRemoved(position + getNumberOfHeaders());
   }

   // Internal ViewHolder to be used for Footers and Headers
   // ==============================================================================================
   private static class InternalHeaderFooterViewHolder extends RecyclerView.ViewHolder {
      public InternalHeaderFooterViewHolder(View itemView) {
         super(itemView);
      }
   }

   // Deals with max of headers/footers.
   // Those were to be `final int`, but we're delaying their initialisation on purpose to allow
   // extending classes to init themselves on constructor.
   // ==============================================================================================
   private int internal_max_num_headers;
   private int internal_max_num_footers;
   private boolean initNumHeadersFootersComplete = false;

   private void initNumHeadersFooters() {
      if (initNumHeadersFootersComplete) return;
      internal_max_num_headers = getMaxNumHeaders();
      internal_max_num_footers = getMaxNumFooters();
      initNumHeadersFootersComplete = true;
   }

   private int MAX_NUM_HEADERS() {
      initNumHeadersFooters();
      return internal_max_num_headers;
   }

   private int MAX_NUM_FOOTERS() {
      initNumHeadersFooters();
      return internal_max_num_footers;
   }

   // Wrapped SpanSizeLookup for the GridLayoutManager
   // ==============================================================================================
   private final class GridSpanSize extends GridLayoutManager.SpanSizeLookup {

      private final GridLayoutManager.SpanSizeLookup source;
      private final int spanCount;

      private GridSpanSize(GridLayoutManager.SpanSizeLookup source, int spanCount) {
         this.source = source;
         this.spanCount = spanCount;
      }

      @Override
      public int getSpanSize(int position) {
         if (isHeaderPosition(position) || isFooterPosition(position)) {
            // header and footer takes the whole width
            return spanCount;
         } else {
            return source.getSpanSize(position - getNumberOfHeaders());
         }
      }
   }

   // OnItemClickListenerDetector
   // ==============================================================================================
   private OnItemClickListenerDetector onItemClickListenerDetector;

   public void setOnItemClickListenerDetector(OnItemClickListenerDetector onItemClickListenerDetector) {
      this.onItemClickListenerDetector = onItemClickListenerDetector;
   }

}
