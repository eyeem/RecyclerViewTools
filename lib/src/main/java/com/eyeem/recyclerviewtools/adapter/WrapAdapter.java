package com.eyeem.recyclerviewtools.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by budius on 01.04.15.
 */
public class WrapAdapter
   extends RecyclerView.Adapter {

   private static final int MAX_CACHE_SIZE = 666; // TODO: what's a sensible value?

   private static final int HEADER_VIEW_TYPE_MASK = 0x10000000;
   private static final long HEADER_ITEM_ID_MASK = 0x1000000000000000L;

   private static final int FOOTER_VIEW_TYPE_MASK = 0x20000000;
   private static final long FOOTER_ITEM_ID_MASK = 0x2000000000000000L;

   private static final int SECTION_VIEW_TYPE_MASK = 0x40000000;
   private static final long SECTION_ITEM_ID_MASK = 0x4000000000000000L;
   static final int NOT_A_SECTION = -1;

   private final RecyclerView.Adapter wrapped;
   private final AbstractSectionAdapter sections;
   private OnItemClickListenerDetector onItemClickListenerDetector;

   public WrapAdapter(RecyclerView.Adapter wrappedAdapter) {
      this(wrappedAdapter, new EmptySectionAdapter());
   }

   public WrapAdapter(RecyclerView.Adapter wrappedAdapter, AbstractSectionAdapter sectionAdapter) {

      if (wrappedAdapter == null || sectionAdapter == null)
         throw new IllegalArgumentException("wrappedAdapter and sectionAdapter cannot be null");

      this.wrapped = wrappedAdapter;
      this.sections = sectionAdapter;

      setHasStableIds(wrapped.hasStableIds());
      wrapped.registerAdapterDataObserver(dataObserver);

      // pre-init real item position
      int count = wrapped.getItemCount();
      int cacheSize = Math.min(count > 0 ? count : MAX_CACHE_SIZE / 2, MAX_CACHE_SIZE);
      initPositionCaching(cacheSize);
   }

   // basic adapter callbacks (viewType, ID, count, create n bind viewHolder)
   // ==============================================================================================
   @Override public int getItemViewType(int position) {
      if (isHeaderPosition(position)) {
         return HEADER_VIEW_TYPE_MASK | position;
      } else if (isFooterPosition(position)) {
         return FOOTER_VIEW_TYPE_MASK | (getItemCount() - position - 1);
      } else {
         int sectionPosition = getSectionIndex(position);
         if (sectionPosition != NOT_A_SECTION) {
            return SECTION_VIEW_TYPE_MASK | sections.getSectionViewType(sectionPosition);
         } else {
            return wrapped.getItemViewType(recyclerToWrappedPosition.get(position));
         }
      }
   }

   @Override public long getItemId(int position) {
      if (!hasStableIds()) return RecyclerView.NO_ID;

      if (isHeaderPosition(position)) {
         return HEADER_ITEM_ID_MASK | position;
      } else if (isFooterPosition(position)) {
         return FOOTER_ITEM_ID_MASK | (getItemCount() - position);
      } else {
         int sectionPosition = getSectionIndex(position);
         if (sectionPosition != NOT_A_SECTION) {
            return SECTION_ITEM_ID_MASK | sections.getSectionId(sectionPosition);
         } else {
            return wrapped.getItemViewType(recyclerToWrappedPosition.get(position));
         }
      }
   }

   @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

      RecyclerView.ViewHolder viewHolder;
      boolean extra = false;
      if (isHeaderViewType(viewType)) {
         extra = true;
         viewHolder = new HeaderFooterHolder(getHeaders().get(removeMask(viewType, HEADER_VIEW_TYPE_MASK)));
      } else if (isFooterViewType(viewType)) {
         extra = true;
         viewHolder = new HeaderFooterHolder(getFooters().get(removeMask(viewType, FOOTER_VIEW_TYPE_MASK)));
      } else if (isSectionViewType(viewType)) {
         extra = true;
         viewHolder = sections.onCreateSectionViewHolder(parent, removeMask(viewType, SECTION_VIEW_TYPE_MASK));
      } else {
         viewHolder = wrapped.onCreateViewHolder(parent, viewType);
      }
      bindOnItemClickListener(viewHolder, extra);
      return viewHolder;
   }

   @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

      if (holder instanceof HeaderFooterHolder)
         return;

      if (isSectionViewType(holder.getItemViewType())) {
         sections.onBindSectionView(holder, getSectionIndex(position));
      } else {
         wrapped.onBindViewHolder(holder, recyclerToWrappedPosition.get(position));
      }
   }

   @Override public int getItemCount() {
      return wrapped.getItemCount() + sections.getSectionCount() + getHeaderCount() + getFooterCount();
   }

   // extended adapter callbacks, most adapters don't use, but just for completeness
   // ==============================================================================================
   @Override public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
      if (holder instanceof HeaderFooterHolder || isSectionViewType(holder.getItemViewType()))
         return;
      wrapped.onViewAttachedToWindow(holder);
   }

   @Override public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
      if (holder instanceof HeaderFooterHolder || isSectionViewType(holder.getItemViewType()))
         return;
      wrapped.onViewDetachedFromWindow(holder);
   }

   @Override public void onViewRecycled(RecyclerView.ViewHolder holder) {
      if (holder instanceof HeaderFooterHolder || isSectionViewType(holder.getItemViewType()))
         return;
      wrapped.onViewRecycled(holder);
   }

   @Override public void onAttachedToRecyclerView(RecyclerView recyclerView) {
      wrapped.onAttachedToRecyclerView(recyclerView);
   }

   @Override public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
      wrapped.onDetachedFromRecyclerView(recyclerView);
   }

   @Override public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
      if (holder instanceof HeaderFooterHolder || isSectionViewType(holder.getItemViewType()))
         return super.onFailedToRecycleView(holder);
      else
         return wrapped.onFailedToRecycleView(holder);
   }

   // OnItemClick handling
   // ==============================================================================================
   public void setOnItemClickListenerDetector(OnItemClickListenerDetector onItemClickListenerDetector) {
      this.onItemClickListenerDetector = onItemClickListenerDetector;
   }

   private void bindOnItemClickListener(RecyclerView.ViewHolder holder, boolean isExtra) {
      if (onItemClickListenerDetector == null) return;
      if (onItemClickListenerDetector.ignoreExtras && isExtra)
         return;

      holder.itemView.setOnClickListener(onItemClickListenerDetector);
   }

   // View type helpers
   // ==============================================================================================
   private boolean isHeaderViewType(int viewType) {
      return isViewType(viewType, HEADER_VIEW_TYPE_MASK);
   }

   private boolean isFooterViewType(int viewType) {
      return isViewType(viewType, FOOTER_VIEW_TYPE_MASK);
   }

   private boolean isSectionViewType(int viewType) {
      return isViewType(viewType, SECTION_VIEW_TYPE_MASK);
   }

   private boolean isViewType(int viewType, int viewTypeMask) {
      return (viewTypeMask & viewType) == viewTypeMask;
   }

   private int removeMask(int val, int mask) {
      return val & ~mask; // ~ is bitwise not: NOT mask AND val
   }

   // Headers and Footers
   // ==============================================================================================
   private List<View> headers; // Lazy initialised list of headers
   private List<View> footers; // Lazy initialised list of footers

   public void addHeader(View v) {
      setDefaultLayoutParams(v);
      getHeaders().add(v);
      clearCache();
   }

   public void addFooter(View v) {
      setDefaultLayoutParams(v);
      getFooters().add(v);
      clearCache();
   }

   private void setDefaultLayoutParams(View v) {
      if (v.getLayoutParams() == null) {
         RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
         v.setLayoutParams(lp);
      }
   }

   private boolean isHeaderPosition(int position) {
      return position < getHeaderCount();
   }

   private boolean isFooterPosition(int position) {
      int footerCount = getFooterCount();
      if (footerCount == 0)
         return false;
      else
         return getItemCount() - position <= footerCount;
   }

   private List<View> getHeaders() {
      if (headers == null)
         headers = new ArrayList<>();
      return headers;
   }

   private List<View> getFooters() {
      if (footers == null)
         footers = new ArrayList<>();
      return footers;
   }

   public int getHeaderCount() {
      return headers == null ? 0 : headers.size();
   }

   public int getFooterCount() {
      return footers == null ? 0 : footers.size();
   }

   private static class HeaderFooterHolder extends RecyclerView.ViewHolder {
      public HeaderFooterHolder(View itemView) {
         super(itemView);
      }
   }

   // position conversion and caching for sections
   // ==============================================================================================
   private LruCache<Integer, Integer> sectionIndex;
   private LruCache<Integer, Integer> sectionPosition;

   LruCache<Integer, Integer> recyclerToWrappedPosition;
   private LruCache<Integer, Integer> wrappedToRecyclerPosition;
   private boolean lruCacheEnabled;

   private int getSectionIndex(int position) {
      position -= getHeaderCount(); // offset number of headers
      if (lruCacheEnabled) {
         return sectionIndex.get(position);
      } else {
         return sections.getSectionIndex(position);
      }
   }

   private int getSectionPosition(int index) {
      if (lruCacheEnabled) {
         return sectionPosition.get(index);
      } else {
         return sections.getSectionPosition(index);
      }
   }

   private void initPositionCaching(int cacheSize) {

      lruCacheEnabled = sections.lruCacheEnabled();

      if (lruCacheEnabled) {
         sectionIndex = new LruCache<Integer, Integer>(cacheSize) {
            @Override protected Integer create(Integer position) {
               return sections.getSectionIndex(position - getHeaderCount());
            }
         };

         sectionPosition = new LruCache<Integer, Integer>(cacheSize) {
            @Override protected Integer create(Integer index) {
               return sections.getSectionPosition(index);
            }
         };
      }

      recyclerToWrappedPosition = new LruCache<Integer, Integer>(cacheSize) {
         @Override protected Integer create(Integer position) {

            int sectionIndexVal;
            int numberOfSectionsBeforePosition = 0;

            // find the 1st section position before requested position
            for (int i = position; i >= getHeaderCount(); i--) {
               sectionIndexVal = getSectionIndex(i);
               if (sectionIndexVal != NOT_A_SECTION) {
                  // found a section, there are that amount of sections before `position`
                  numberOfSectionsBeforePosition = sectionIndexVal + 1;
                  break;
               }
            }
            return position - numberOfSectionsBeforePosition - getHeaderCount();
         }
      };

      wrappedToRecyclerPosition = new LruCache<Integer, Integer>(cacheSize) {
         @Override protected Integer create(Integer position) {

            int value = position;
            for (int i = 0; i < sections.getSectionCount(); i++) {
               if (getSectionPosition(i) > value) {
                  break;
               } else {
                  value++;
               }
            }
            return value + getHeaderCount();
         }
      };
   }

   private void clearCache() {
      if (sectionIndex != null) sectionIndex.evictAll();
      if (sectionPosition != null) sectionPosition.evictAll();
      recyclerToWrappedPosition.evictAll();
      wrappedToRecyclerPosition.evictAll();
   }

   // Data observing
   // ==============================================================================================
   private final RecyclerView.AdapterDataObserver dataObserver = new RecyclerView.AdapterDataObserver() {

      @Override public void onChanged() {
         notifyDataSetChanged();
      }

      @Override public void onItemRangeChanged(int positionStart, int itemCount) {
         notifyItemRangeChanged(wrappedToRecyclerPosition.get(positionStart), itemCount);
      }

      @Override public void onItemRangeInserted(int positionStart, int itemCount) {
         // TODO: section after this point will `blink` on screen
         notifyItemRangeInserted(wrappedToRecyclerPosition.get(positionStart), itemCount);
      }

      @Override public void onItemRangeRemoved(int positionStart, int itemCount) {
         // TODO: section after this point will `blink` on screen
         notifyItemRangeRemoved(wrappedToRecyclerPosition.get(positionStart), itemCount);
      }

      @Override public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
         // TODO: moved? what if there was a header in the middle
         int from = wrappedToRecyclerPosition.get(fromPosition);
         int to = wrappedToRecyclerPosition.get((toPosition));
         for (int i = 0; i < itemCount; i++)
            notifyItemMoved(from + i, to + i);
      }
   };

   // Goodies for GridLayoutManager
   // ==============================================================================================

   /**
    * Generates a `GridLayoutManager.SpanSizeLookup` that makes each section
    * take the whole `spanCount` and other adapter positions are 1 span each
    *
    * @param spanCount the number of spans
    * @return a new GridLayoutManager.SpanSizeLookup to be used with this WrapAdapter
    */
   public GridLayoutManager.SpanSizeLookup createSpanSizeLookup(int spanCount) {
      return new SectionSpanSizeLookup(null, spanCount);
   }

   /**
    * Generates a `GridLayoutManager.SpanSizeLookup` that makes each section
    * take the whole `spanCount`, whilst keeping the wrapped SpanSizeLookup for other positions.
    *
    * @param wrap      the SpanSizeLookup to wrap from
    * @param spanCount the number of spans
    * @return a new GridLayoutManager.SpanSizeLookup to be used with this WrapAdapter
    */
   public GridLayoutManager.SpanSizeLookup createSpanSizeLookup(GridLayoutManager.SpanSizeLookup wrap, int spanCount) {
      return new SectionSpanSizeLookup(wrap, spanCount);
   }

   private final class SectionSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
      private final GridLayoutManager.SpanSizeLookup source;
      private final int spanCount;

      private SectionSpanSizeLookup(GridLayoutManager.SpanSizeLookup source, int spanCount) {
         this.source = source;
         this.spanCount = spanCount;
      }

      @Override public int getSpanSize(int position) {

         if (isHeaderPosition(position)) {
            return spanCount; // header take the whole width
         } else if (isFooterPosition(position)) {
            return spanCount; // footer take the whole width
         } else {
            int sectionIndexVal = getSectionIndex(position);
            if (sectionIndexVal != NOT_A_SECTION) {
               return spanCount; // sections take the whole width
            } else if (source == null) {
               return 1; // default behavior, every item is 1 span
            } else {
               return source.getSpanSize(recyclerToWrappedPosition.get(position));
            }
         }
      }
   }
}
