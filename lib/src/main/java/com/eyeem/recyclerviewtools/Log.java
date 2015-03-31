package com.eyeem.recyclerviewtools;

/**
 * Created by budius on 25.03.15.
 */
public class Log {

   protected static int DEBUG = -1;

   public static void d(Object tag, String message) {
      if (DEBUG > 0) {
         if (tag instanceof String)
            android.util.Log.println(DEBUG, (String) tag, message);
         else
            android.util.Log.println(DEBUG, tag.getClass().getSimpleName(), message);
      }
   }
}
