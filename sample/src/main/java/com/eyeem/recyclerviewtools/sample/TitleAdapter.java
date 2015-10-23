package com.eyeem.recyclerviewtools.sample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eyeem.recyclerviewtools.adapter.SimpleSectionAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by budius on 02.04.15.
 */
public class TitleAdapter extends SimpleSectionAdapter<TitleAdapter.Holder> {

   private LayoutInflater inflater;

   public TitleAdapter(int[] positions) {
      super(positions);
   }

   @Override public Holder onCreateSectionViewHolder(ViewGroup parent, int viewType) {
      if (inflater == null)
         inflater = LayoutInflater.from(parent.getContext());
      return new Holder(inflater.inflate(R.layout.section_adapter, parent, false));
   }

   @Override public void onBindSectionView(Holder viewHolder, int sectionNumber) {
      viewHolder.text.setText("Section " + sectionNumber);
   }

   public static class Holder extends RecyclerView.ViewHolder {
      @Bind(R.id.text) TextView text;

      public Holder(View itemView) {
         super(itemView);
         ButterKnife.bind(this, itemView);
      }
   }
}
