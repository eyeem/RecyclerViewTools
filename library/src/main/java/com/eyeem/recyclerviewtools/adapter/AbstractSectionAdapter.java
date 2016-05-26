package com.eyeem.recyclerviewtools.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by budius on 01.04.15.
 * <p/>
 * Base class for section adapter. Those are mostly mirrored calls from RecyclerView.Adapter only
 * changing the name "view" to "section".
 */
public abstract class AbstractSectionAdapter<VH extends RecyclerView.ViewHolder> {

   /**
    * Return the view type of the section at <code>sectionNumber</code> for the purposes
    * of view recycling.
    * <p/>
    * The default implementation of this method returns 0, making the assumption of
    * a single view type for the adapter.
    *
    * @param sectionIndex section number to query
    * @return integer value identifying the type of the view needed to represent the section at
    * <code>position</code>.
    */
   public int getSectionViewType(int sectionIndex) {
      return 0;
   }

   /**
    * Return the stable ID for the section at <code>sectionIndex</code>.
    * This is queried only if the RecyclerView.Adapter this SectionAdapter is bound to
    * return true to {@link android.support.v7.widget.RecyclerView.Adapter#hasStableIds() hasStableIds}
    * <p/>
    * The default implementation of this method returns <code>sectionIndex</code>.
    *
    * @param sectionIndex section index to query
    * @return the stable ID of the section number
    */
   public long getSectionId(int sectionIndex) {
      return sectionIndex;
   }

   /**
    * Similar to {@link android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int) onCreateViewHolder}
    * called by the adapter whenever the RecyclerView queries for a section ViewHolder
    *
    * @param parent   The ViewGroup into which the new View will be added after it is bound to
    *                 an adapter position.
    * @param viewType value returned by {@link #getSectionViewType(int)}
    * @return an object that extends {@link android.support.v7.widget.RecyclerView.ViewHolder}
    */
   public abstract VH onCreateSectionViewHolder(ViewGroup parent, int viewType);

   /**
    * @param viewHolder
    * @param sectionNumber
    */
   public abstract void onBindSectionView(VH viewHolder, int sectionNumber);

   /**
    * Returns the total number of sections in the adapter.
    *
    * @return The total number of sections in this adapter.
    */
   public abstract int getSectionCount();

   /**
    * Informs the WrapAdapter of the adapter position of each section.
    * Must return {@link AbstractSectionAdapter#NOT_A_SECTION NOT_A_SECTION}
    * if the queried position is not a section.
    *
    * @param position the position in the WrapAdapter
    * @return the index of the section for that position or NOT_A_SECTION
    */
   public abstract int getSectionIndex(int position);

   /**
    * Inverse of {@link #getSectionIndex(int)}. That's called during `notify_` events to offset
    * the notified data to the {@link WrapAdapter} position.
    *
    * @param index the index of the section
    * @return the position of the section within the wrapped adapter.
    */
   public abstract int getSectionPosition(int index);

   /**
    * Possible optimisation. Calls to {@link #getSectionIndex(int)} and {@link #getSectionPosition(int)} are
    * LruCached inside the {@link WrapAdapter}
    * Returning `false` will disable the caching. Only do this if you're sure that your
    * implementation can reliably return values faster than an {@link android.util.LruCache}
    *
    * @return false to disable the adapter internal LruCache
    */
   public boolean lruCacheEnabled() {
      return true;
   }

   /**
    * Value to be returned from {@link AbstractSectionAdapter#getSectionIndex(int)}
    * when the position is not a section
    */
   public static final int NOT_A_SECTION = WrapAdapter.NOT_A_SECTION;

}
