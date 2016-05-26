package com.eyeem.recyclerviewtools.sample;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eyeem.recyclerviewtools.LoadMoreOnScrollListener;
import com.eyeem.recyclerviewtools.OnItemClickListener;
import com.eyeem.recyclerviewtools.adapter.WrapAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
   SwipeRefreshLayout.OnRefreshListener,
   OnItemClickListener,
   LoadMoreOnScrollListener.Listener {

   @Bind(R.id.recycler) RecyclerView recycler;
   @Bind(R.id.refresh) SwipeRefreshLayout refresh;
   @Bind(R.id.toolbar) Toolbar toolbar;

   private static final Object PICASSO_TAG = new Object();

   private Adapter adapter;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      WrapAdapter.setLogging(true);
      setContentView(R.layout.activity_main);
      ButterKnife.bind(this);

      toolbar.setLogo(R.drawable.ic_action_bar);
      toolbar.setTitle("RecyclerViewTools");

      adapter = new Adapter();
      refresh.setOnRefreshListener(this);

      recycler.addOnScrollListener(new LoadMoreOnScrollListener(this));  // auto calls-back to load more views at end

      WrapAdapter wrapAdapter;

      // example with sections
      if (true) {
         wrapAdapter = new WrapAdapter(adapter,
            new TitleAdapter(new int[]{0, 6, 9, 14, 19, 23}));
      }

      // example without any section
      else {
         wrapAdapter = new WrapAdapter(adapter);
      }

      wrapAdapter.setOnItemClickListener(recycler, this); // simple `onItemClick` for RecyclerView

      // example using `LinearLayoutManager`
      if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("flag_linear_grid", false)) {
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
      View v;
      v = get("HEADER 1");
      v.setOnClickListener(new RemoveHeaderClickListener(v, wrapAdapter));
      wrapAdapter.addHeader(v);

      v = get("HEADER 2");
      v.getLayoutParams().height *=2;
      v.setOnClickListener(new RemoveHeaderClickListener(v, wrapAdapter));
      wrapAdapter.addHeader(v);

      v = get("HEADER 3");
      v.setOnClickListener(new RemoveHeaderClickListener(v, wrapAdapter));
      wrapAdapter.addHeader(v);
      wrapAdapter.addFooter(inflater.inflate(R.layout.footer, recycler, false));

      recycler.setAdapter(wrapAdapter);
   }

   private LayoutInflater layoutInflater;

   private TextView get(String message) {
      if (layoutInflater == null) {
         layoutInflater = LayoutInflater.from(this);
      }
      TextView t = (TextView) layoutInflater.inflate(R.layout.header, recycler, false);
      t.setText(message);
      return t;
   }

   private static class RemoveHeaderClickListener implements View.OnClickListener {
      private final WrapAdapter wrapAdapter;
      private final View header;

      public RemoveHeaderClickListener(View header, WrapAdapter wrapAdapter) {
         this.wrapAdapter = wrapAdapter;
         this.header = header;
      }

      @Override public void onClick(View v) {
         wrapAdapter.removeHeader(header, true);
      }
   }

   @Override protected void onDestroy() {
      ButterKnife.unbind(this);
      super.onDestroy();
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
   public void onItemClick(RecyclerView parent, View view, int position, long id, RecyclerView.ViewHolder viewHolder) {
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

   @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (requestCode == 42 && resultCode == RESULT_OK) {
         startActivity(new Intent(this, MainActivity.class));
         finish();
         overridePendingTransition(0, 0);
      }
   }
}
