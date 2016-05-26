package com.eyeem.recyclerviewtools.sample.data;

/**
 * Created by budius on 22.03.16.
 */
public class Profile {

   public static String get(int size) {
      return Photo.URLS[Data.RANDOM.nextInt(Photo.URLS.length)].replace("/h/100", "/h/" + size);
   }
}
