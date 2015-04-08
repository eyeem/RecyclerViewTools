# RecyclerViewTools
Collection of tools for RecyclerView.

This collection was developed with two main goals: easy to code (very simple API) & robust (it really works).
Includes:

 - Headers, Footers & Sections for `RecyclerView`. Based on a common "wrap" adapter.
 - Multi-scroll listener. One scroll listener dispatch to several listeners
 - Animation controllers for common material patterns like:
  - FloatingActionButton (FAB)
  - ToolBar
  - QuickReturn header
 - Extended `SwipeRefreshLayout` to be able to use it with quick return headers
 - ViewVisibility detector. That can be used in parallax effect.
 - Implementation of OnItemClickListener for RecyclerView (works standalone or in conjunction with headers, footers & sections).

# Usage
Seriously couldn't be simpler:

### Headers & Footers
```Java
// create your recycler adapter normally, then wrap it on the wrapper
adapter = new Adapter();
WrapAdapter wrapAdapter = new WrapAdapter(adapter);
// add headers & footers
wrapAdapter.addHeader(header = inflater.inflate(R.layout.overlay_background, recycler, false));
wrapAdapter.addHeader(inflater.inflate(R.layout.header, recycler, false));
wrapAdapter.addFooter(inflater.inflate(R.layout.footer, recycler, false));
```

### Sections
pass a `AbstractSectionAdapter` when creating the wrap adapter. The section adapter API is mirrored from RecyclerView (`onCreateViewHolder` and `onBindViewHolder`). Feel free to extend from the SimpleSectionAdapter.
```Java
SimpleSectionAdapter sections =
    new SimpleSectionAdapter(
        new int[]{0, 6, 9, 14, 19, 23}) { // those are the section positions

    // override here onCreateSectionViewHolder and onBindSectionView
    
}
WrapAdapter wrapAdapter = new WrapAdapter(adapter, sections);
```

### Notify data changed
Just call from the original adapter, internally the calls get offset to the proper positions
```Java
adapter.notifyItemRangeInserted(start, count); // that's your original adapter
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

### OnItemClickListener
If using a `WrapAdapter` you can just pass to a normal setter, or for `RecyclerView.Adapter` just call `.setOnClickListener(detector);` during `onCreateViewHolder` on every `View`.

`position` and `id` get automatically offset, to exclude header, footer and section clicks.
```Java
wrapAdapter.setOnItemClickListenerDetector(
 new OnItemClickListenerDetector(
  recycler, // recycler view
   new OnItemClickListenerDetector.OnItemClickListener() { // the detector
    @Override public void onItemClick(RecyclerView parent, View view, int position, long id) { // the callback
     // code here ...
    }
   }));
```

### Using with `GridLayoutManager`

Auto generate or wrap the SpanSizeLookup. Default uses 1 span per item. Header, footer and sections take the whole spanCount.

```Java
gridLayoutManager.setSpanSizeLookup(
  wrapAdapter.createSpanSizeLookup(spanCount)); // auto-generate SpanSizeLookup
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

# Caveats

the methods `notifyItem*` from `RecyclerView.Adapter` were made `final` by Google.
So it is impossible to override them on the `WrapAdapter` to properly adjust their positions before passing to the super class.

So, DO NOT use any `notify` method from `WrapAdapter`. Just call from the original adapter.
