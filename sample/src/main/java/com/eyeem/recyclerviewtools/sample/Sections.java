package com.eyeem.recyclerviewtools.sample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eyeem.recyclerviewtools.adapter.SimpleSectionAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by budius on 22.03.16.
 */
public class Sections extends SimpleSectionAdapter<Sections.SectionHolder> {

   private LayoutInflater inflater;

   public Sections() {
      super(SECTIONS_AT);
   }

   @Override
   public SectionHolder onCreateSectionViewHolder(ViewGroup parent, int viewType) {
      if (inflater == null) {
         inflater = LayoutInflater.from(parent.getContext());
      }
      return new SectionHolder(inflater.inflate(R.layout.item_section, parent, false));
   }

   @Override public void onBindSectionView(SectionHolder viewHolder, int sectionNumber) {
      viewHolder.text.setText(SECTION_TEXT[sectionNumber]);
   }

   static class SectionHolder extends RecyclerView.ViewHolder {
      @BindView(R.id.text) TextView text;

      SectionHolder(View view) {
         super(view);
         ButterKnife.bind(this, view);
      }
   }

   private static final String[] SECTION_TEXT = {
      "Alpha",
      "Beta",
      "Cupcake",
      "Donut",
      "Eclair",
      "Froyo",
      "Gingerbread",
      "Honeycomb",
      "IceCreamSandwich",
      "JellyBean",
      "KitKat",
      "Lollipop",
      "Marshmallow",
      "Nougat",
      "Oreo",
      "Pancakes",
      "Q... ?",
      "Raspberry Pi",
      "Scone",
      "Truffle",
      "U... ?",
      "Vanilla Pudding",
      "Waffle",
      "X... ?",
      "Z... ?"
   };

   private static final int[] SECTIONS_AT = {
      0,
      10,
      20,
      30,
      40,
      50,
      60,
      70,
      80,
      90,
      100,
      110,
      120,
      130,
      140,
      150,
      160,
      170,
      180,
      190,
      200,
      210,
      220,
      230,
      240
   };
}
