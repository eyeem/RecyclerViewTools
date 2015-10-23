package com.eyeem.recyclerviewtools.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;

import java.util.Arrays;

/**
 * Created by budius on 08.04.15.
 * Implements the {@link AbstractSectionAdapter AbstractSectionAdapter}
 * using an {@link android.util.SparseIntArray SparseIntArray} as the positioning container.
 * As per docs, this should have a good performance up to a few hundred sections.
 * <p/>
 * For larger section count, it's not advisable to use it.
 */
public abstract class SimpleSectionAdapter<VH extends RecyclerView.ViewHolder> extends AbstractSectionAdapter<VH> {

   private final SparseIntArray positions;

   /**
    * Default constructor
    *
    * @param sectionsAt and array with the positions of each section
    */
   public SimpleSectionAdapter(int[] sectionsAt) {
      positions = new SparseIntArray(sectionsAt.length);
      Arrays.sort(sectionsAt);
      for (int i : sectionsAt)
         positions.put(i, positions.size());
   }

   @Override public boolean lruCacheEnabled() {
      // TODO: I'm still thinking about this one
      // SparseIntArray.get(int, int) runs the code:
      // `int i = ContainerHelpers.binarySearch(mKeys, mSize, key);`
      // is that slower than the LruCache hit?

      // at the moment it uses the cache if there are lots of sections
      return positions.size() > 133; // like in 133Mhz in my old Pentium-PC
   }

   @Override public final int getSectionCount() {
      return positions.size();
   }

   @Override public final int getSectionIndex(int position) {
      return positions.get(position, NOT_A_SECTION);
   }

   @Override public final int getSectionPosition(int index) {
      return positions.keyAt(index);
   }
}

