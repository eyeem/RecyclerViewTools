package com.eyeem.recyclerviewtools.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import com.eyeem.recyclerviewtools.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by budius on 01.04.15.
 * <p/>
 * Base wrapping adapter that allows usage of headers, footers, sections and OnItemClick within a
 * {@link android.support.v7.widget.RecyclerView RecyclerView}.
 */
public class WrapAdapter
      extends RecyclerView.Adapter {

   private static final int MAX_CACHE_SIZE = 666; // TODO: what's a sensible value?

   private static final int MAIN_VIEW_TYPE_MASK = 0x40000000;
   private static final long MAIN_ITEM_ID_MASK = 0x4000000000000000L;

   private static final int HEADER_VIEW_TYPE_MASK = 0x1000000 + MAIN_VIEW_TYPE_MASK;
   private static final long HEADER_ITEM_ID_MASK = 0x100000000000000L + MAIN_ITEM_ID_MASK;

   private static final int FOOTER_VIEW_TYPE_MASK = 0x2000000 + MAIN_VIEW_TYPE_MASK;
   private static final long FOOTER_ITEM_ID_MASK = 0x200000000000000L + MAIN_ITEM_ID_MASK;

   private static final int SECTION_VIEW_TYPE_MASK = 0x4000000 + MAIN_VIEW_TYPE_MASK;
   private static final long SECTION_ITEM_ID_MASK = 0x400000000000000L + MAIN_ITEM_ID_MASK;

   private static final int CUSTOM_VIEW_TYPE = 0x8000000 + MAIN_VIEW_TYPE_MASK;

   static final int NOT_A_SECTION = -1;

   private final RecyclerView.Adapter wrapped;
   private final AbstractSectionAdapter sections;
   private OnItemClickListenerDetector onItemClickListenerDetector;
   private boolean isReverseOrder = false;

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
   @Override
   public int getItemViewType(int position) {

      int type;

      if (customView != null && customViewBehavior == BEHAVIOR_DEFAULT) {
         type = CUSTOM_VIEW_TYPE;
      } else if (isHeaderPosition(position)) {
         type = HEADER_VIEW_TYPE_MASK | headerTypes.get(getHeaders().get(position).hashCode());
      } else if (isFooterPosition(position)) {
         int footerPosition = position - getItemCount() + getFooterCount();
         type = FOOTER_VIEW_TYPE_MASK | footerTypes.get(getFooters().get(footerPosition).hashCode());
      } else if (customView != null && customViewBehavior == BEHAVIOR_ALLOW_HEADER_FOOTER) {
         type = CUSTOM_VIEW_TYPE;
      } else {
         int sectionPosition = getSectionIndex(position);
         if (sectionPosition != NOT_A_SECTION) {
            type = SECTION_VIEW_TYPE_MASK | sections.getSectionViewType(sectionPosition);
         } else {
            type = wrapped.getItemViewType(recyclerToWrappedPosition.get(position));
            if (type > MAIN_VIEW_TYPE_MASK)
               throw new IllegalArgumentException("ItemView type cannot be greater than 0x" + Integer.toHexString(MAIN_VIEW_TYPE_MASK));
         }
      }
      l("getItemViewType for " + position + " = 0x" + Integer.toHexString(type));
      return type;
   }

   @Override
   public long getItemId(int position) {

      long id;

      if (!hasStableIds()) {
         id = RecyclerView.NO_ID;
      } else if (customView != null && customViewBehavior == BEHAVIOR_DEFAULT) {
         id = RecyclerView.NO_ID;
      } else if (isHeaderPosition(position)) {
         id = HEADER_ITEM_ID_MASK | getHeaders().get(position).hashCode();
      } else if (isFooterPosition(position)) {
         int footerPosition = position - getItemCount() + getFooterCount();
         id = FOOTER_ITEM_ID_MASK | getFooters().get(footerPosition).hashCode();
      } else if (customView != null && customViewBehavior == BEHAVIOR_ALLOW_HEADER_FOOTER) {
         id = RecyclerView.NO_ID;
      } else {
         int sectionPosition = getSectionIndex(position);
         if (sectionPosition != NOT_A_SECTION) {
            id = SECTION_ITEM_ID_MASK | sections.getSectionId(sectionPosition);
         } else {
            id = wrapped.getItemId(recyclerToWrappedPosition.get(position));
            if (id > MAIN_ITEM_ID_MASK)
               throw new IllegalArgumentException("ItemView type cannot be greater than 0x" + Long.toHexString(MAIN_ITEM_ID_MASK));
         }
      }
      l("getItemId for " + position + " = 0x" + Long.toHexString(id));
      return id;
   }

   @Override
   public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

      RecyclerView.ViewHolder viewHolder;

      if (customView != null && viewType == CUSTOM_VIEW_TYPE) {
         viewHolder = new CustomViewHolder(customView);
         StaggeredLayoutManagerInternalUtils.setFullWidthLayoutParams(parent, viewHolder);
         return viewHolder;
      }

      if (isHeaderViewType(viewType)) {
         viewHolder = new HeaderFooterHolder(findViewByType(removeMask(viewType, HEADER_VIEW_TYPE_MASK), getHeaders(), headerTypes));
         StaggeredLayoutManagerInternalUtils.setFullWidthLayoutParams(parent, viewHolder);
      } else if (isFooterViewType(viewType)) {
         viewHolder = new HeaderFooterHolder(findViewByType(removeMask(viewType, FOOTER_VIEW_TYPE_MASK), getFooters(), footerTypes));
         StaggeredLayoutManagerInternalUtils.setFullWidthLayoutParams(parent, viewHolder);
      } else if (isSectionViewType(viewType)) {
         viewHolder = sections.onCreateSectionViewHolder(parent, removeMask(viewType, SECTION_VIEW_TYPE_MASK));
         StaggeredLayoutManagerInternalUtils.setFullWidthLayoutParams(parent, viewHolder);
      } else {
         viewHolder = wrapped.onCreateViewHolder(parent, viewType);
      }
      bindOnItemClickListener(viewHolder);
      return viewHolder;
   }

   @Override
   public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

      if (holder instanceof HeaderFooterHolder ||
            holder instanceof CustomViewHolder) {
         return;
      }

      if (isSectionViewType(holder.getItemViewType())) {
         sections.onBindSectionView(holder, getSectionIndex(position));
      } else {
         wrapped.onBindViewHolder(holder, recyclerToWrappedPosition.get(position));
      }
   }

   @Override
   public int getItemCount() {
      if (customView != null) {
         if (customViewBehavior == BEHAVIOR_DEFAULT) {
            return 1;
         } else {
            return 1 + sections.getSectionCount() + getHeaderCount() + getFooterCount();
         }
      } else {
         return wrapped.getItemCount() + sections.getSectionCount() + getHeaderCount() + getFooterCount();
      }
   }

   // extended adapter callbacks, most adapters don't use, but just for completeness
   // ==============================================================================================
   @Override
   public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
      if (isInternalHolder(holder))
         return;
      wrapped.onViewAttachedToWindow(holder);
   }

   @Override
   public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
      if (isInternalHolder(holder))
         return;
      wrapped.onViewDetachedFromWindow(holder);
   }

   @Override
   public void onViewRecycled(RecyclerView.ViewHolder holder) {
      if (isInternalHolder(holder))
         return;
      wrapped.onViewRecycled(holder);
   }

   @Override
   public void onAttachedToRecyclerView(RecyclerView recyclerView) {
      wrapped.onAttachedToRecyclerView(recyclerView);
   }

   @Override
   public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
      wrapped.onDetachedFromRecyclerView(recyclerView);
   }

   @Override
   public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
      if (isInternalHolder(holder))
         return super.onFailedToRecycleView(holder);
      else
         return wrapped.onFailedToRecycleView(holder);
   }

   private boolean isInternalHolder(RecyclerView.ViewHolder holder) {
      return holder.getItemViewType() > MAIN_VIEW_TYPE_MASK;
   }

   // OnItemClick handling
   // ==============================================================================================

   /**
    * Simple OnItemClick for RecyclerView. This binds a {@link android.view.View.OnClickListener}
    * to the root view of the each view holder.
    * <p/>
    * This call automatically ignore clicks on headers, footers and sections.
    *
    * @param recyclerView        the recyclerView this adapter will be attached to
    * @param onItemClickListener the listener for the click events
    */
   public void setOnItemClickListener(
         RecyclerView recyclerView,
         OnItemClickListener onItemClickListener) {
      setOnItemClickListener(recyclerView, onItemClickListener, true);
   }

   /**
    * Simple OnItemClick for RecyclerView. This binds a {@link android.view.View.OnClickListener}
    * to the root view of the each view holder.
    *
    * @param recyclerView        the recyclerView this adapter will be attached to
    * @param onItemClickListener the listener for the click events
    * @param ignoreExtras        true if it should ignore header, footer and sections; false otherwise
    */
   public void setOnItemClickListener(
         RecyclerView recyclerView,
         OnItemClickListener onItemClickListener,
         boolean ignoreExtras) {
      onItemClickListenerDetector = new OnItemClickListenerDetector(recyclerView, onItemClickListener, ignoreExtras);
   }

   private void bindOnItemClickListener(RecyclerView.ViewHolder holder) {
      if (onItemClickListenerDetector == null) return;
      if (onItemClickListenerDetector.ignoreExtras && isInternalHolder(holder))
         return;

      if (holder instanceof CustomViewHolder)
         return; // we never handle CustomView click.

      holder.itemView.setOnClickListener(onItemClickListenerDetector);
   }

   // View type helpers
   // ==============================================================================================
   private static boolean isHeaderViewType(int viewType) {
      return isViewType(viewType, HEADER_VIEW_TYPE_MASK);
   }

   private static boolean isFooterViewType(int viewType) {
      return isViewType(viewType, FOOTER_VIEW_TYPE_MASK);
   }

   private static boolean isSectionViewType(int viewType) {
      return isViewType(viewType, SECTION_VIEW_TYPE_MASK);
   }

   private static boolean isViewType(int viewType, int viewTypeMask) {
      return (viewTypeMask & viewType) == viewTypeMask;
   }

   private static int removeMask(int val, int mask) {
      return val & ~mask; // ~ is bitwise not: NOT mask AND val
   }

   private static View findViewByType(int viewType, List<View> views, SparseIntArray hashToType) {
      for (int i = 0, size = views.size(); i < size; i++) {
         View v = views.get(i);
         int thisViewType = hashToType.get(v.hashCode());
         if (thisViewType == viewType) {
            return v;
         }
      }
      return null;
   }

   // Headers and Footers
   // ==============================================================================================
   private List<View> headers; // Lazy initialised list of headers
   private List<View> footers; // Lazy initialised list of footers
   private final AtomicInteger headerViewTypeGenerator = new AtomicInteger();
   private final AtomicInteger footerViewTypeGenerator = new AtomicInteger();
   private SparseIntArray headerTypes;
   private SparseIntArray footerTypes;

   public void addHeader(View v) {
      if (!getHeaders().contains(v)) {
         setDefaultLayoutParams(v);
         getHeaders().add(v);
         headerTypes.put(v.hashCode(), headerViewTypeGenerator.incrementAndGet());
         clearCache();
      }
   }

   public void addHeader(int headerPosition, View v) {
      if (!getHeaders().contains(v)) {
         setDefaultLayoutParams(v);
         getHeaders().add(headerPosition, v);
         headerTypes.put(v.hashCode(), headerViewTypeGenerator.incrementAndGet());
         clearCache();
      }
   }

   public void removeHeader(View v, boolean autoNotify) {
      if (headers == null) return;
      if (getHeaders().contains(v)) {
         int position = -1;
         if (autoNotify) position = getHeaders().indexOf(v);
         if (getHeaders().remove(v)) {
            headerTypes.delete(v.hashCode());
            clearCache();
            if (autoNotify && position >= 0)
               notifyItemRemoved(position);
         }
      }
   }

   public void addFooter(View v) {
      if (!getFooters().contains(v)) {
         setDefaultLayoutParams(v);
         getFooters().add(v);
         footerTypes.put(v.hashCode(), footerViewTypeGenerator.incrementAndGet());
         clearCache();
      }
   }

   public void removeFooter(View v, boolean autoNotify) {
      if (footers == null) return;
      if (getFooters().contains(v)) {
         int position = -1;
         if (autoNotify) position = getFooters().indexOf(v);
         if (getFooters().remove(v)) {
            footerTypes.delete(v.hashCode());
            clearCache();
            if (autoNotify && position >= 0) {
               notifyItemRemoved(getHeaderCount() + sections.getSectionCount() + getWrappedCount() + position);
            }
         }
      }
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
      if (headerTypes == null)
         headerTypes = new SparseIntArray();
      return headers;
   }

   private List<View> getFooters() {
      if (footers == null)
         footers = new ArrayList<>();
      if (footerTypes == null)
         footerTypes = new SparseIntArray();
      return footers;
   }

   public int getHeaderCount() {
      return headers == null ? 0 : headers.size();
   }

   public int getFooterCount() {
      return footers == null ? 0 : footers.size();
   }

   public int getWrappedCount() {
      return wrapped.getItemCount();
   }

   public RecyclerView.Adapter getWrapped() {
      return wrapped;
   }

   private static class HeaderFooterHolder extends RecyclerView.ViewHolder {
      public HeaderFooterHolder(View itemView) {
         super(itemView);
      }
   }

   // Custom view (useful for empty/loading states)
   // ==============================================================================================
   private View customView;

   /**
    * Set a view to replace the contents of the wrapped adapter.
    * This is useful to be used for different data states. For example `loading` or `empty`.
    * Call this method with `null` to remove the custom view.
    *
    * @param view view to be displayed as the solely RecyclerView content
    */
   public void setCustomView(View view) {
      // avoid calling notifyDataSetChanged if not really needed
      if (customView == null && view == null) return;
      if (customView != null && view != null && customView.equals(view)) return;
      customView = view;
      notifyDataSetChanged();
   }

   /**
    * Default custom view behavior.
    * Makes the WrapAdapter getCount() return 1.
    */
   public static final int BEHAVIOR_DEFAULT = 0;

   /**
    * Custom view behavior that allows showing of any added Header or Footer.
    * This makes the WrapAdapter getCount() return 1 + getHeaderCount() + getFooterCount()
    */
   public static final int BEHAVIOR_ALLOW_HEADER_FOOTER = 1;
   private int customViewBehavior = BEHAVIOR_DEFAULT;

   /**
    * Change the behavior of the custom view. Default is to overtake the whole RecyclerView.
    * Call to `notifyDataSetChanged()` might be needed to reflect the new behavior.
    *
    * @param behavior BEHAVIOR_DEFAULT or BEHAVIOR_ALLOW_HEADER_FOOTER
    */
   public void setCustomViewBehavior(int behavior) {
      if (customViewBehavior == behavior) return;
      this.customViewBehavior = behavior;
   }

   private static class CustomViewHolder extends RecyclerView.ViewHolder {
      public CustomViewHolder(View itemView) {
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
            @Override
            protected Integer create(Integer position) {
               return sections.getSectionIndex(position - getHeaderCount());
            }
         };

         sectionPosition = new LruCache<Integer, Integer>(cacheSize) {
            @Override
            protected Integer create(Integer index) {
               return sections.getSectionPosition(index);
            }
         };
      }

      recyclerToWrappedPosition = new LruCache<Integer, Integer>(cacheSize) {
         @Override
         protected Integer create(Integer position) {

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

            int retVal = position - numberOfSectionsBeforePosition - getHeaderCount();
            if (isReverseOrder) {
               retVal = wrapped.getItemCount() - 1 - retVal;
            }
            return retVal;
         }
      };

      wrappedToRecyclerPosition = new LruCache<Integer, Integer>(cacheSize) {
         @Override
         protected Integer create(Integer position) {

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

   public void setIsReverseOrder(boolean isReverseOrder) {
      this.isReverseOrder = isReverseOrder;
      clearCache();
   }

   // Data observing
   // ==============================================================================================
   private final RecyclerView.AdapterDataObserver dataObserver = new RecyclerView.AdapterDataObserver() {

      @Override
      public void onChanged() {
         clearCache();
         notifyDataSetChanged();
      }

      @Override
      public void onItemRangeChanged(int positionStart, int itemCount) {
         clearCache();
         notifyItemRangeChanged(wrappedToRecyclerPosition.get(positionStart), itemCount);
      }

      @Override
      public void onItemRangeInserted(int positionStart, int itemCount) {
         // TODO: section after this point will `blink` on screen
         clearCache();
         notifyItemRangeInserted(wrappedToRecyclerPosition.get(positionStart), itemCount);
      }

      @Override
      public void onItemRangeRemoved(int positionStart, int itemCount) {
         // TODO: section after this point will `blink` on screen
         clearCache();
         notifyItemRangeRemoved(wrappedToRecyclerPosition.get(positionStart), itemCount);
      }

      @Override
      public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
         clearCache();
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

      @Override
      public int getSpanSize(int position) {

         if (customView != null) {
            return spanCount; // custom take whole width
         }

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

   //region static loggin
   private static boolean logEnabled = false;

   public static void setLogging(boolean enabled) {
      logEnabled = enabled;
   }

   private static void l(String message) {
      if (logEnabled) {
         Log.d("WrapAdapter", message);
      }
   }
   //endregion
}
