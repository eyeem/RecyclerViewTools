package com.eyeem.recyclerviewtools.sample.data;

import android.content.Context;

import com.eyeem.recyclerviewtools.sample.R;

import java.util.Random;

/**
 * Created by budius on 23.03.16.
 */
public class Data {

   public static final int TYPE_PHOTO = 0;
   public static final int TYPE_TEXT = 1;

   public final int type;
   public final String name;
   public final String profileUrl;
   public final String photoUrl;
   public final String text;
   public final int size;

   public Data(int type, String name, String profileUrl, String photoUrl, String text, int size) {
      this.type = type;
      this.name = name;
      this.profileUrl = profileUrl;
      this.photoUrl = photoUrl;
      this.text = text;
      this.size = size;
   }

   public static Data newRandom(Context context) {
      int size = context.getResources().getDimensionPixelSize(R.dimen.profile_size);
      return new Data(
            RANDOM.nextFloat() > 0.6f ? TYPE_PHOTO : TYPE_TEXT,
            Name.get(),
            Profile.get(size),
            Photo.get(),
            Text.get(),
            size
      );
   }

   public static final Random RANDOM = new Random();
}