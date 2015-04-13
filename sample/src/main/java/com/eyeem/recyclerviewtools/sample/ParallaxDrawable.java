package com.eyeem.recyclerviewtools.sample;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by budius on 10.04.15.
 */
public class ParallaxDrawable extends BitmapDrawable {

   private final int backgroundColor;

   public ParallaxDrawable(Resources resources, Bitmap bitmap) {
      super(resources, bitmap);
      backgroundColor = resources.getColor(R.color.green) + resources.getColor(R.color.transparency);
   }

   @Override protected boolean onLevelChange(int level) {
      invalidateSelf();
      return true;
   }

   @Override public void draw(Canvas canvas) {
      float val = ((float) getLevel()) / 1.5f;
      canvas.save();
      canvas.translate(0, val);
      super.draw(canvas);
      canvas.drawColor(backgroundColor);
      canvas.restore();
   }
}
