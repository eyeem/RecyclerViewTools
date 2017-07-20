package com.eyeem.recyclerviewtools.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.eyeem.recyclerviewtools.ItemOffsetDecoration;
import com.eyeem.recyclerviewtools.LoadMoreOnScrollListener;
import com.eyeem.recyclerviewtools.OnItemClickListener;
import com.eyeem.recyclerviewtools.RecyclerViewTools;
import com.eyeem.recyclerviewtools.StaggeredLayoutManagerUtils;
import com.eyeem.recyclerviewtools.adapter.WrapAdapter;
import com.eyeem.recyclerviewtools.sample.data.Data;
import com.eyeem.recyclerviewtools.sample.data.Photo;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by budius on 26.05.16.
 */
public class SampleActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener, LoadMoreOnScrollListener.Listener, OnItemClickListener {

   @BindView(R.id.toolbar) Toolbar toolbar;
   @BindView(R.id.recycler) RecyclerView recycler;
   @Nullable @BindView(R.id.refresh) SwipeRefreshLayout refresh;
   @Nullable @BindView(R.id.header_image) ImageView header;

   private View customView;
   private DataAdapter adapter;
   private WrapAdapter wrapAdapter;

   @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      Config config = (Config) getIntent().getSerializableExtra(Config.TAG);

      // inflate and bind
      setContentView(config.useCoordinatorLayout ? R.layout.activity_main_swipe : R.layout.activity_main);
      ButterKnife.bind(this);

      // Toolbar
      toolbar.setTitle("Hello Droids!");
      toolbar.inflateMenu(R.menu.menu);
      toolbar.setOnMenuItemClickListener(this);

      // SwipeRefreshLayout
      if (refresh != null && header != null) {
         Picasso.with(this)
               .load(Photo.HEADER(getResources().getDisplayMetrics().widthPixels))
               .fit().centerCrop().into(header);

         dismissRefresh(refresh);
      }

      // Adapter
      adapter = DataAdapter.generateRandom(this);
      if (config.useSections) {
         wrapAdapter = new WrapAdapter(adapter, new Sections());
      } else {
         wrapAdapter = new WrapAdapter(adapter);
         wrapAdapter.setCustomViewBehavior(WrapAdapter.BEHAVIOR_ALLOW_HEADER_FOOTER);
      }

      // Layout manager
      RecyclerView.LayoutManager layoutManager;
      switch (config.layoutManager) {
         case Config.GRID_LAYOUT_MANAGER:
            layoutManager = new GridLayoutManager(this, config.layoutManagerSpan);
            ((GridLayoutManager) layoutManager).setSpanSizeLookup(wrapAdapter.createSpanSizeLookup(config.layoutManagerSpan));
            break;
         case Config.STAGGERED_GRID_LAYOUT_MANAGER:
            layoutManager = new StaggeredGridLayoutManager(config.layoutManagerSpan, StaggeredGridLayoutManager.VERTICAL);
            break;
         case Config.LINEAR_LAYOUT_MANAGER:
         default:
            layoutManager = new LinearLayoutManager(this);
            break;
      }
      recycler.setLayoutManager(layoutManager);
      recycler.setAdapter(wrapAdapter);

      // Header
      if (config.useHeader) {
         wrapAdapter.addHeader(new Header(this));
      }

      // Load more scroller
      if (config.useLoadMore) {
         recycler.addOnScrollListener(new LoadMoreOnScrollListener(this));
      }

      // OnItemClickListener
      if (config.useOnItemClick) {
         wrapAdapter.setOnItemClickListener(recycler, this);
      }

      if (config.useItemOffsetDecoration) {
         float dpi = getResources().getDisplayMetrics().density;
         int internalOffset = (int) dpi * 4;
         int externalOffset = (int) dpi * 24;
         recycler.addItemDecoration(new ItemOffsetDecoration(
               // around the item views - similar to recyclerView.setPadding(...)
               externalOffset,
               externalOffset / 2,
               externalOffset,
               0,
               // in-between views - similar to holder.itemView.setPadding(...)
               internalOffset,
               internalOffset,
               internalOffset,
               internalOffset,
               true
         ));
      }
   }

   @Override public boolean onMenuItemClick(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.menu_item_scroll_to_top:
            RecyclerViewTools.fastScrollToTop(recycler);
            toast("Fast Scroll to Top");
            return true;
         case R.id.menu_item_toggle_custom_view:
            item.setChecked(!item.isChecked());
            item.setIcon(item.isChecked() ?
                  R.drawable.ic_check_box_white_24dp :
                  R.drawable.ic_check_box_outline_blank_white_24dp);
            setCustomView(item.isChecked());
         default:
            return false;
      }
   }

   @Override public void onLoadMore(RecyclerView recyclerView) {
      toast("Load more");

      recyclerView.postDelayed(new Runnable() {
         @Override public void run() {

            for (int i = 0; i < 5; i++) {
               adapter.data.add(Data.newRandom(SampleActivity.this));
            }

            if (recycler.getLayoutManager() instanceof StaggeredGridLayoutManager) {
               StaggeredLayoutManagerUtils.onItemChanged(recycler, 0);
            }
            adapter.notifyDataSetChanged();

         }
      }, 1000);
   }

   @Override
   public void onItemClick(RecyclerView parent, View view, int position, long id, RecyclerView.ViewHolder viewHolder) {
      popView(view, position);
   }

   private void popView(View view, int position) {
      fancyAnimation(view);
      toast("Clicked position " + position);
   }

   private void setCustomView(boolean visible) {
      if (visible && customView == null) {
         customView = LayoutInflater.from(this)
               .inflate(R.layout.custom_view, recycler, false);
      }
      wrapAdapter.setCustomView(visible ? customView : null);
      if (visible) {
         toast("Custom view on WrapAdapter");
      }

   }

}
