package com.eyeem.recyclerviewtools.scroll_controller;

import android.support.annotation.DimenRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.eyeem.recyclerviewtools.Tools;

/**
 * Created by budius on 30.03.15.
 * This builder creates properly configured configuration object for the controllers to operate.
 */
public class Builder {

   // those flags are used as configure each view controller
   static final int FLAG_NORMAL_RETURN = 0x1;
   static final int FLAG_QUICK_RETURN = 0x2;
   static final int FLAG_SNAP_TO = 0x4;
   static final int FLAG_MIN_SIZE = 0x8;
   static final int FLAG_UP = 0x10;
   static final int FLAG_DOWN = 0x20;
   static final int FLAG_COVER = 0x40;

   private final Config config;

   public Builder(View view) {
      config = new Config();
      config.view = view;
      config.flags = FLAG_UP | FLAG_QUICK_RETURN;
   }

   public RecyclerView.OnScrollListener build() {
      return new MainController(config);
   }

   public Builder normalReturn(View reference) {
      reset(FLAG_QUICK_RETURN | FLAG_SNAP_TO);
      set(FLAG_NORMAL_RETURN);
      config.reference = reference;
      return this;
   }

   public Builder quickReturn() {
      reset(FLAG_NORMAL_RETURN);
      set(FLAG_QUICK_RETURN);
      config.reference = null;
      return this;
   }

   public Builder snapTo() {
      if (!config.is(FLAG_QUICK_RETURN))
         throw new IllegalArgumentException("snapTo must be quick return");
      set(FLAG_SNAP_TO);
      return this;
   }

   public Builder up() {
      reset(FLAG_DOWN);
      set(FLAG_UP);
      return this;
   }

   public Builder down() {
      reset(FLAG_UP);
      set(FLAG_DOWN);
      return this;
   }

   public Builder cover(View reference) {
      if (!config.is(FLAG_SNAP_TO))
         throw new IllegalArgumentException("coverBackground must be snapTo");
      set(FLAG_COVER);
      config.reference = reference;
      return this;
   }

   public Builder minSizeResId(@DimenRes int minSizeResId) {
      return minSize(config.view.getResources().getDimensionPixelOffset(minSizeResId));
   }

   public Builder minSize(int pixels) {
      if (pixels > 0) {
         config.minSize = pixels;
         set(FLAG_MIN_SIZE);
      } else {
         reset(FLAG_MIN_SIZE);
      }
      return this;
   }

   private void set(int flag) {
      config.flags = config.flags | flag;
   }

   private void reset(int flag) {
      config.flags = config.flags & ~flag; // Java bitwise NOT is ~
   }

   // ==============================================================================================

   /**
    * Holds all the necessary flags, values and views for the controllers to properly operate
    */
   static class Config {
      View view;
      View reference;
      int flags;
      int minSize;

      ViewGroup getParent() {
         return ((ViewGroup) view.getParent());
      }

      /**
       * Calculates the maximum value our view should be scrolled to
       *
       * @return the maximum value to call {@link android.view.View#setTranslationY(float)} on this view
       */
      int getLimit() {
         if (is(FLAG_DOWN)) {
            return getLimitDown();
         } else {
            return getLimitUp();
         }
      }

      private int getLimitDown() {
         return getParent().getHeight() - view.getTop() - minSize;
      }

      private int getLimitUp() {
         return -view.getBottom() + minSize;
      }

      /**
       * Compares the supplied translationY with this config limit, apply the limit to it and
       * apply to the view translationY
       *
       * @param translationY proposed new TranslationY to be used on the view
       * @return true, if the view translation was changed
       */
      boolean limit(int translationY) {
         int limit;
         if (is(FLAG_DOWN)) {
            limit = getLimitDown();
            if (translationY > limit)
               translationY = limit;
            else if (translationY < 0)
               translationY = 0;
         } else {
            limit = getLimitUp();
            if (translationY < limit)
               translationY = limit;
            if (translationY > 0)
               translationY = 0;
         }

         return Tools.setTranslationY(view, translationY);
      }

      /**
       * Process the flag value
       *
       * @param flag flag to test
       * @return true if the flag is 1, false otherwise
       */
      boolean is(int flag) {
         return (flag & flags) == flag;
      }
   }
}