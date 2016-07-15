# RecyclerViewTools 
Collection of tools for RecyclerView. [ ![Download](https://api.bintray.com/packages/eyeem/maven/recyclerview-tools/images/download.svg) ](https://bintray.com/eyeem/maven/recyclerview-tools/_latestVersion)

This collection was developed with two main goals: easy to code (very simple API) & robust (it really works).
Includes:

 - Headers, Footers & Sections for `RecyclerView`. Based on a common "wrap" adapter.
 - Extended `SwipeRefreshLayout` to use when Recycler is not direct child (e.g. on CoordinatorLayout)
 - Implementation of OnItemClickListener for RecyclerView (works standalone or in conjunction with headers, footers & sections).
 - FastScrollToTop function.

# Usage
Seriously couldn't be simpler:

### Add to Gradle build script the lib

```Java
dependencies {

    ... your other dependencies

    compile 'com.eyeem.recyclerviewtools:library:{latest}'
}
```

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

### ScrollListeners
``` Java
// callback to this activity when recycler is at the end (needs to load more)
recycler.addOnScrollListener(new LoadMoreOnScrollListener(this));

// auto call Picasso.pauseTag and resumeTag for smooth scrolling with ImageViews
recycler.addOnScrollListener(new PicassoOnScrollListener(PICASSO_TAG));
```

### OnItemClickListener
If using a `WrapAdapter` you can just pass to a normal setter, or for `RecyclerView.Adapter` just call `.setOnClickListener(detector);` during `onCreateViewHolder` on every `View`.

`position` and `id` get automatically offset, to exclude header, footer and section clicks.
```Java
wrapAdapter.setOnItemClickListenerDetector(
 new OnItemClickListenerDetector(
  recycler, // recycler view
   new OnItemClickListenerDetector.OnItemClickListener() { // the detector
    @Override public void onItemClick(RecyclerView parent, View view, int position, long id, RecyclerView.ViewHolder viewHolder) { // the callback
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

# Caveats

the methods `notifyItem*` from `RecyclerView.Adapter` were made `final` by Google.
So it is impossible to override them on the `WrapAdapter` to properly adjust their positions before passing to the super class.

So, DO NOT use any `notify` method from `WrapAdapter`. Just call from the original adapter.
