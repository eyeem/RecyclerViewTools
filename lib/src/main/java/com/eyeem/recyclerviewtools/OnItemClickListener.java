package com.eyeem.recyclerviewtools;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by budius on 04.08.15.
 */
public interface OnItemClickListener {
   public abstract void onItemClick(RecyclerView parent, View view, int position, long id, RecyclerView.ViewHolder viewHolder);
}
