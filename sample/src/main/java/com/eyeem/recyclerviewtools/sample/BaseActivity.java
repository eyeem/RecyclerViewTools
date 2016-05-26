package com.eyeem.recyclerviewtools.sample;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * Created by budius on 23.03.16.
 */
public class BaseActivity extends AppCompatActivity {

   private Toast toast;

   @Override protected void onDestroy() {
      if (toast != null) {
         toast.cancel();
         toast = null;
      }
      super.onDestroy();
   }

   protected void toast(String msg) {
      if (toast != null) {
         toast.cancel();
      }
      toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
      toast.show();
   }

   protected void fancyAnimation(View view) {
      view.animate().setDuration(111).setInterpolator(new AccelerateInterpolator())
            .alpha(0.7f)
            .scaleX(1.2f)
            .scaleY(1.2f)
            .withEndAction(new AnimateBack(view))
            .start();
   }

   private static class AnimateBack implements Runnable {
      final WeakReference<View> weakView;

      private AnimateBack(View view) {
         this.weakView = new WeakReference<View>(view);
      }

      @Override public void run() {
         View view = weakView.get();
         if (view == null) return;
         view.animate().setDuration(111).setInterpolator(new DecelerateInterpolator())
               .alpha(1f)
               .scaleX(1f)
               .scaleY(1f)
               .start();
      }
   }

   protected void dismissRefresh(SwipeRefreshLayout refresh) {
      new DismissRefresh(refresh);
   }

   private static final class DismissRefresh implements
         SwipeRefreshLayout.OnRefreshListener, Runnable {

      private final SwipeRefreshLayout refresh;

      private DismissRefresh(SwipeRefreshLayout refresh) {
         this.refresh = refresh;
         this.refresh.setOnRefreshListener(this);
      }

      @Override public void onRefresh() {
         refresh.postDelayed(this, 666);
      }

      @Override public void run() {
         refresh.setRefreshing(false);
      }
   }
}
