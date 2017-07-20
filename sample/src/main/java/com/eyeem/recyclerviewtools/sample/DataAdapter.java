package com.eyeem.recyclerviewtools.sample;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eyeem.recyclerviewtools.sample.data.Data;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by budius on 23.03.16.
 */
public class DataAdapter extends ListAdapter<Data, DataAdapter.BaseHolder> {

   public static final int SIZE = 260;

   public static DataAdapter generateRandom(Context context) {
      ArrayList<Data> data = new ArrayList<>(SIZE);
      for (int i = 0; i < SIZE; i++) {
         data.add(Data.newRandom(context));
      }
      return new DataAdapter(data);
   }

   private LayoutInflater inflater;

   public DataAdapter(@NonNull ArrayList<Data> data) {
      super(data);
      setHasStableIds(true);
   }

   @Override public int getItemViewType(int position) {
      return getItem(position).type;
   }

   @Override public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      if (inflater == null) {
         inflater = LayoutInflater.from(parent.getContext());
      }
      return (viewType == Data.TYPE_PHOTO) ?
            new ImageHolder(inflater.inflate(R.layout.item_photo, parent, false)) :
            new TextHolder(inflater.inflate(R.layout.item_text, parent, false));
   }

   @Override public long getItemId(int position) {
      return getItem(position).hashCode();
   }

   @Override public void onBindViewHolder(BaseHolder holder, int position) {
      holder.bind(getItem(position));
   }

   public static abstract class BaseHolder extends RecyclerView.ViewHolder {
      @BindView(R.id.profile) protected CircleImageView profile;
      @BindView(R.id.name) protected TextView name;

      public BaseHolder(View itemView) {
         super(itemView);
      }

      protected void bind(Data data) {
         Picasso.with(itemView.getContext())
               .load(data.profileUrl)
               .resize(data.size, data.size)
               .noFade()
               .placeholder(placeholder(data.profileUrl))
               .into(profile);
         name.setText(data.name);
      }
   }

   public static Drawable placeholder(String url) {
      Drawable d = new ColorDrawable(Color.BLACK);
      int alpha = (url == null) ? 73 : 64 + Math.abs(url.hashCode()) % 64;
      d.setAlpha(alpha);
      return d;
   }

   static class ImageHolder extends BaseHolder {

      @BindView(R.id.photo) ImageView photo;
      final int size;

      public ImageHolder(View itemView) {
         super(itemView);
         ButterKnife.bind(this, itemView);
         size = itemView.getContext().getResources().getDisplayMetrics().widthPixels;
      }

      @Override protected void bind(Data data) {
         super.bind(data);
         Picasso.with(itemView.getContext())
               .load(data.photoUrl.replace("/h/100", "/h/" + size))
               .fit()
               .centerCrop()
               .placeholder(placeholder(data.photoUrl))
               .into(photo);
      }
   }

   static class TextHolder extends BaseHolder {

      @BindView(R.id.text) TextView text;

      public TextHolder(View itemView) {
         super(itemView);
         ButterKnife.bind(this, itemView);
      }

      @Override protected void bind(Data data) {
         super.bind(data);
         text.setText(data.text);
      }
   }
}
