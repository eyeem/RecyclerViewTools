# RecyclerViewTools
Collection of tools for RecyclerView.

This collection was developed with two main goals: easy to code (very simple API) & robust (it works).
Includes:

 - Header and Footer for `RecyclerView`. Can be extended or wrapped and works with `GridLayoutManager`
 - Multi-scroll listener. One scroll listener to dispatch to several listeners
 - Animation controllers for common material patterns like:
  - FloatingActionButton (FAB)
  - ToolBar
  - QuickReturn header
 - Extended `SwipeRefreshLayout` to be able to use it with quick return headers
 - ViewVisibility detector. That can be used in parallax effect.
 - Implementation of OnItemClickListener for RecyclerView that auto detect headers and footers.

# Usage
Seriously couldn't be simpler...


### Headers and Footers
adapter with header and footer
```Java
// create your recycler adapter normally, then wrap it on the wrapper
AbstractHeaderFooterRecyclerAdapter adapter = new WrapHeaderFooterAdapter<>(new Adapter());
adapter.addHeader(inflater.inflate(R.layout.header1, recycler, false));
adapter.addFooter(inflater.inflate(R.layout.footer1, recycler, false));
recycler.setAdapter(adapter);
```

### ScrollListener utils
``` Java
// create a new com.eyeem.recyclerviewtools.OnScrollListener and add to the recycler
scrollListener = new OnScrollListener();
recycler.setOnScrollListener(scrollListener);

 // callback to this activity when there's 3 views to the end of the adapter
scrollListener.setLoadMoreListener(this, 3);

// auto call Picasso.pauseTag and resumeTag for smooth scrolling with ImageViews
scrollListener.setPicassoTag(PICASSO_TAG);
```

### Floating views and quick return headers
To configure floating views, build your layouts as normal, with any floating element where it would actually be on screen.
```XML
<com.eyeem.recyclerviewtools.SmarterSwipeRefreshLayout
   android:id="@+id/refresh"
   xmlns:android="http://schemas.android.com/apk/res/android"
   xmlns:app="http://schemas.android.com/apk/res-auto"
   android:layout_width="match_parent"
   android:layout_height="match_parent"
   app:target="@+id/recycler"> <!-- indicates to do the swipe processing on the RecyclerView -->

   <FrameLayout
      android:id="@+id/frame"
      android:layout_width="match_parent"
      android:layout_height="match_parent">

      <android.support.v7.widget.RecyclerView
         android:id="@+id/recycler"
         android:layout_width="match_parent"
         android:layout_height="match_parent"/>

      <!-- top aligned, width=match_parent and height=some_fixed_value (e.g. @dimen/header_height) -->
      <include layout="@layout/quick_return_header"/>

      <!-- right|bottom aligned, right and bottom margins and wrap_content -->
      <include layout="@layout/floating_action_button"/>

   </FrameLayout>
</com.eyeem.recyclerviewtools.SmarterSwipeRefreshLayout>
```

then just configure the floating views with the builders
```Java
// add empty space to be behind the quick return header, with same height (e.g. R.dimen.header_height)
adapter.addHeader(header = inflater.inflate(R.layout.header2, recycler, false));

// add controllers for the floating views
scrollListener.addListener(
   // FAB will scroll down, quick return, animate to snap to position
   RecyclerViewTools.setup(floatingButton).down().quickReturn().snapTo().build());

// one option for the header
scrollListener.addListener(
   // header will scroll up and show only atop the recycler
   RecyclerViewTools.setup(quickReturnHeader).up().normalReturn(header).build());

// other options for the header
scrollListener.addListener(
   RecyclerViewTools.setup(quickReturnHeader) // header wil:
    .up() // scroll up,
    .quickReturn() // quick return,
    .snapTo() // animate to snap to position
    .cover(header) // always cover the background,
    .minSizeResId(R.dimen.header_min_size) // and keep a minimun size visible on screen
    .build());
```

### OnItemClickListener

using a `AbstractHeaderFooterRecyclerAdapter` you can just pass to a normal setter, or for `RecyclerView.Adapter` just call `.setOnClickListener(detector);` during `onCreateViewHolder` on every `View`.

Example using the headerFooter recycler
```Java
wrapAdapter.setOnItemClickListenerDetector(
 new OnItemClickListenerDetector(
  recycler, // recycler view
   new OnItemClickListenerDetector.OnItemClickListener() { // the detector
    @Override public void onItemClick(RecyclerView parent, View view, int position, long id) { // the callback
     // code here ...
    }
   },
   true)); // passing `true` will discount the header/footer from `position` and not call for headers or footers
```

### Header or Footers with `GridLayoutManager`

There are two versions of `generateSpanSizeLookup`.
One that generates from the default 1 column per item and one that can wrap a different lookup

```Java
GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfColumns);
gridLayoutManager.setSpanSizeLookup(wrapAdapter.generateSpanSizeLookup(numberOfColumns));
recycler.setLayoutManager(gridLayoutManager);
```

# Caveats

the methods `notifyItem*` from `RecyclerView.Adapter` were made `final` by Google.
So it is impossible to override them to properly adjust their positions before passing to the super class.

so the `AbstractHeaderFooterRecyclerAdapter` provides alternative "mirrored" methods for notifying with an underscore symbol `_` at the beginning, they're:
 - _notifyItemChanged(int position)
 - _notifyItemInserted(int position)
 - _notifyItemMoved(int fromPosition, int toPosition)
 - _notifyItemRangeChanged(int positionStart, int itemCount)
 - _notifyItemRangeInserted(int positionStart, int itemCount)
 - _notifyItemRangeRemoved(int positionStart, int itemCount)
 - _notifyItemRemoved(int position)

those will properly adjust the wrapped positions and call to the super class methods.
That means you have two ways of properly coding. See the example:

```Java
// 1. call on the original adapter
// the WrapHeaderFooterAdapter subscribe to the original adapter and properly calls the mirrored methods
originalAdapter.notifyItemRangeInserted(0, added);

// 2. call the mirrored method directly on the wrapAdapter
wrapAdapter._notifyItemRangeInserted(0, added);
```