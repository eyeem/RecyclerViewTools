package com.eyeem.recyclerviewtools.sample;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by budius on 23.03.16.
 */
public abstract class ListAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

   public final ArrayList<T> data;

   public ListAdapter(@NonNull ArrayList<T> data) {
      this.data = data;
   }

   protected T getItem(int position) {
      return data.get(position);
   }

   @Override public int getItemCount() {
      return data.size();
   }
}
