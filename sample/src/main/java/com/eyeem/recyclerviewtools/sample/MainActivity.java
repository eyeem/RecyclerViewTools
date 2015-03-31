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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eyeem.recyclerviewtools.OnItemClickListenerDetector;
import com.eyeem.recyclerviewtools.OnScrollListener;
import com.eyeem.recyclerviewtools.RecyclerViewTools;
import com.eyeem.recyclerviewtools.adapter.WrapHeaderFooterAdapter;
import com.eyeem.recyclerviewtools.scroll_controller.Builder;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends ActionBarActivity implements OnScrollListener.LoadMoreListener, SwipeRefreshLayout.OnRefreshListener, OnItemClickListenerDetector.OnItemClickListener {

   @InjectView(R.id.recycler) RecyclerView recycler;
   @InjectView(R.id.refresh) SwipeRefreshLayout refresh;
   @InjectView(R.id.frame) FrameLayout frame;

   @InjectView(R.id.floating_button) ImageButton floatingButton;
   @InjectView(R.id.overlay) LinearLayout overlay;

   private static final Object PICASSO_TAG = new Object();
   private OnScrollListener scrollListener;

   private Adapter adapter;
   private WrapHeaderFooterAdapter wrapAdapter;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      RecyclerViewTools.setLogLevel(Log.DEBUG);
      setContentView(R.layout.activity_main);
      ButterKnife.inject(this);
      LayoutInflater inflater = LayoutInflater.from(this);

      View header;

      adapter = new Adapter();
      wrapAdapter = new WrapHeaderFooterAdapter<>(adapter);

      // example using `LinearLayoutManager`
      if (true) {
         recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
      }

      // example using `GridLayoutManager`
      else {
         int numberOfColumns = 3;
         GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfColumns);
         gridLayoutManager.setSpanSizeLookup(wrapAdapter.generateSpanSizeLookup(numberOfColumns));
         recycler.setLayoutManager(gridLayoutManager);
         adapter.vertical = true;
      }

      // add footer n header AFTER `setLayoutManager`
      wrapAdapter.addHeader(header = inflater.inflate(R.layout.overlay_background, recycler, false));
      wrapAdapter.addHeader(inflater.inflate(R.layout.header, recycler, false));
      wrapAdapter.addFooter(inflater.inflate(R.layout.footer, recycler, false));

      // receive OnItemClick events, `true` removes the header and only get events from the wrapped adapter
      wrapAdapter.setOnItemClickListenerDetector(new OnItemClickListenerDetector(recycler, this, true));



      recycler.setAdapter(wrapAdapter);
      refresh.setOnRefreshListener(this);

      scrollListener = new OnScrollListener();
      scrollListener.setLoadMoreListener(this, 3);
      scrollListener.setPicassoTag(PICASSO_TAG);

      // FAB scroll down, quick return, snap to
      scrollListener.addListener(
         RecyclerViewTools.setup(floatingButton).down().quickReturn().snapTo().build());

      int example = 3;
      Builder b = RecyclerViewTools.setup(overlay).up();
      String t;

      // a few different versions of the header, just for testing/example
      switch (example) {
         case 1:
            b.normalReturn(header);
            t = "normal return";
            break;
         case 2:
            b.quickReturn();
            t = "quick return";
            break;
         case 3:
            b.normalReturn(header).minSizeResId(R.dimen.header_min_size);
            t = "normal with min size";
            break;
         default:
         case 0:
            b.quickReturn().snapTo().minSizeResId(R.dimen.header_min_size).cover(header);
            t = "quick return, snap to, cover background and min size";
            break;
      }

      reToast(t);
      scrollListener.addListener(b.build());

      recycler.setOnScrollListener(scrollListener);

   }

   @Override public void onLoadMore(RecyclerView recyclerView) {
      reToast("Loading more...");
      recyclerView.postDelayed(loadMoreRunnable, 500);
   }

   /*
    * Example calling wrapAdapter mirrored `_notify`
    */
   private Runnable loadMoreRunnable = new Runnable() {
      @Override public void run() {
         int size = adapter.getItemCount();
         int added = adapter.addEnd();
         //adapter.notifyItemRangeInserted(size, added);
         wrapAdapter._notifyItemRangeInserted(size, added);
         reToast("Added " + added);
      }
   };

   @Override public void onRefresh() {
      refresh.postDelayed(refreshRunnable, 500);
   }

   /*
    * Example calling directly on the original adapter
    */
   private Runnable refreshRunnable = new Runnable() {
      @Override public void run() {
         refresh.setRefreshing(false);
         int added = adapter.addStart();
         adapter.notifyItemRangeInserted(0, added);
         //wrapAdapter._notifyItemRangeInserted(0, added);
         reToast("Added " + added);
      }
   };

   /*
    * onItemClick
    */
   @Override public void onItemClick(RecyclerView parent, View view, int position, long id) {
      reToast("removing position " + position);
      adapter.remove(position);
      //adapter.notifyItemRemoved(position);
      wrapAdapter._notifyItemRemoved(position);
   }

   private Toast currentToast;

   private void reToast(String toast) {
      if (currentToast != null)
         currentToast.cancel();
      currentToast = Toast.makeText(this, toast, Toast.LENGTH_SHORT);
      currentToast.show();
   }
}
