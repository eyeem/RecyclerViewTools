package com.eyeem.recyclerviewtools.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by budius on 07.04.15.
 * zero sections implementation used when the WrapAdapter is created without sections
 */
final class EmptySectionAdapter extends AbstractSectionAdapter {
   @Override
   public RecyclerView.ViewHolder onCreateSectionViewHolder(ViewGroup parent, int viewType) {
      return null;
   }

   @Override public void onBindSectionView(RecyclerView.ViewHolder viewHolder, int sectionNumber) {

   }

   @Override public boolean lruCacheEnabled() {
      return false;
   }

   @Override public int getSectionCount() {
      return 0;
   }

   @Override public int getSectionIndex(int position) {
      return NOT_A_SECTION;
   }

   @Override public int getSectionPosition(int index) {
      return 0;
   }
}
