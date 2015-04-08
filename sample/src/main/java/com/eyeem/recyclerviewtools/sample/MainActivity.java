package com.eyeem.recyclerviewtools.sample;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eyeem.recyclerviewtools.OnScrollListener;
import com.eyeem.recyclerviewtools.RecyclerViewTools;
import com.eyeem.recyclerviewtools.adapter.OnItemClickListenerDetector;
import com.eyeem.recyclerviewtools.adapter.WrapAdapter;
import com.eyeem.recyclerviewtools.scroll_controller.Builder;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends ActionBarActivity implements OnScrollListener.LoadMoreListener, SwipeRefreshLayout.OnRefreshListener, OnItemClickListenerDetector.OnItemClickListener {

   @InjectView(R.id.recycler) RecyclerView recycler;
   @InjectView(R.id.refresh) SwipeRefreshLayout refresh;

   @InjectView(R.id.floating_button) ImageButton floatingButton;
   @InjectView(R.id.overlay) LinearLayout overlay;

   private static final Object PICASSO_TAG = new Object();
   private OnScrollListener scrollListener;

   private Adapter adapter;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      RecyclerViewTools.setLogLevel(Log.DEBUG);
      setContentView(R.layout.activity_main);
      ButterKnife.inject(this);

      adapter = new Adapter();
      refresh.setOnRefreshListener(this);

      scrollListener = new OnScrollListener();
      scrollListener.setLoadMoreListener(this, 3); // auto calls-back to load more views at end
      scrollListener.setPicassoTag(PICASSO_TAG); // calls Picasso.pauseTag/resumeTag automatically
      recycler.setOnScrollListener(scrollListener);

      WrapAdapter wrapAdapter = new WrapAdapter(adapter,
         new TitleAdapter(new int[]{0, 6, 9, 14, 19, 23}));

      wrapAdapter.setOnItemClickListenerDetector(
         new OnItemClickListenerDetector(recycler, this)); // simple `onItemClick` for RecyclerView

      // example using `LinearLayoutManager`
      if (false) {
         recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
      }

      // example using `GridLayoutManager`
      else {
         int numberOfColumns = 2;
         GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfColumns);
         gridLayoutManager.setSpanSizeLookup(
            wrapAdapter.createSpanSizeLookup(numberOfColumns)); // auto-generate SpanSizeLookup
         recycler.setLayoutManager(gridLayoutManager);
         adapter.vertical = true;
      }

      // header and footers
      LayoutInflater inflater = LayoutInflater.from(this);
      View header;
      wrapAdapter.addHeader(header = inflater.inflate(R.layout.overlay_background, recycler, false));
      wrapAdapter.addHeader(inflater.inflate(R.layout.header, recycler, false));
      wrapAdapter.addFooter(inflater.inflate(R.layout.footer, recycler, false));

      recycler.setAdapter(wrapAdapter);

      // configure the floating header and FAB
      int example = 0;
      Builder b = RecyclerViewTools.setup(overlay).up();
      String t;

      // a few different versions of the header, just for testing/example
      switch (example) {
         case 1:
            // shows atop `header` view that was included on the adapter
            b.normalReturn(header);
            t = "normal return";
            break;
         case 2:
            // quick return, the type you see on most places
            b.quickReturn();
            t = "quick return";
            break;
         case 3:
            // just like case 1, but there will be min height of `overlay` always visible on-screen
            b.normalReturn(header).minSizeResId(R.dimen.header_min_size);
            t = "normal with min size";
            break;
         default:
         case 0:
            // quick return, but `overlay` will always "snap to" (or animate to)
            // hidden or shown position when there's no more user interaction
            // also, if `header` is still visible, behaves like `normalReturn`
            // and have a min size visible
            b.quickReturn().snapTo().minSizeResId(R.dimen.header_min_size).cover(header);
            t = "quick return, snap to, cover background and min size";
            break;
      }

      reToast(t);
      scrollListener.addListener(b.build());

      // FAB scroll down, quick return, snap to
      scrollListener.addListener(
         RecyclerViewTools.setup(floatingButton).down().quickReturn().snapTo().build());
   }

   // callbacks
   // =====================
   /*
    * onLoadMore
    */
   @Override public void onLoadMore(RecyclerView recyclerView) {
      reToast("Loading more...");
      recyclerView.postDelayed(loadMoreRunnable, 500);
   }

   /*
    * onRefresh
    */
   @Override public void onRefresh() {
      refresh.postDelayed(refreshRunnable, 500);
   }

   /*
    * onItemClick
    */
   @Override public void onItemClick(RecyclerView parent, View view, int position, long id) {
      reToast("removing position " + position);
      adapter.remove(position);
      adapter.notifyItemRemoved(position);
   }

   // delayed runnables
   // =====================
   private Runnable refreshRunnable = new Runnable() {
      @Override public void run() {
         refresh.setRefreshing(false);
         int added = adapter.addStart();
         adapter.notifyItemRangeInserted(0, added);
         reToast("Added " + added);
      }
   };

   private Runnable loadMoreRunnable = new Runnable() {
      @Override public void run() {
         int size;
         int added;
         size = adapter.getItemCount();
         added = adapter.addEnd();
         adapter.notifyItemRangeInserted(size, added);
         reToast("Added " + added);
      }
   };

   // toast cancel logic
   // =====================
   private Toast currentToast;

   private void reToast(String toast) {
      if (currentToast != null)
         currentToast.cancel();
      currentToast = Toast.makeText(this, toast, Toast.LENGTH_SHORT);
      currentToast.show();
   }
}
