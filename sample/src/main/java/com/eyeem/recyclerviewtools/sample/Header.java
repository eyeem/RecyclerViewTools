package com.eyeem.recyclerviewtools.sample;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by budius on 23.03.16.
 */
public class Header extends ImageView {

   private boolean scrim = true;

   public Header(Context context) {
      super(context);
      scrim = false;
      DisplayMetrics dm = context.getResources().getDisplayMetrics();
      ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
         ViewGroup.LayoutParams.MATCH_PARENT,
         dm.heightPixels / 3);
      setLayoutParams(lp);
      setScaleType(ScaleType.CENTER_CROP);
      Picasso.with(context)
         .load(R.drawable.header)
         .fit()
         .centerCrop()
         .into(this);
   }

   public Header(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public Header(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   @TargetApi(Build.VERSION_CODES.LOLLIPOP)
   public Header(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
   }

   @Override public void setLayoutParams(ViewGroup.LayoutParams params) {
      super.setLayoutParams(params);
   }

   @Override protected void dispatchDraw(Canvas canvas) {
      super.dispatchDraw(canvas);
      if (scrim)
         canvas.drawARGB(128,
            0, 0, 0);
   }
}
