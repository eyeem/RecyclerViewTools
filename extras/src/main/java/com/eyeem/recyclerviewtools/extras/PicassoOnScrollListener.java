package com.eyeem.recyclerviewtools.extras;

import android.support.v7.widget.RecyclerView;

import com.squareup.picasso.Picasso;

/**
 * Created by budius on 13.04.15.
 * <p/>
 * RecyclerView.OnScrollListener to control Picasso pauseTag/resumeTag interactions
 */
public class PicassoOnScrollListener extends RecyclerView.OnScrollListener {

   private final Object picassoTag;

   /**
    * Standard constructor
    *
    * @param picassoTag tag to call for {@link com.squareup.picasso.Picasso#pauseTag(Object)} and
    *                   {@link com.squareup.picasso.Picasso#resumeTag(Object)}
    */
   public PicassoOnScrollListener(Object picassoTag) {
      this.picassoTag = picassoTag;
   }

   @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
      switch (newState) {
         case RecyclerView.SCROLL_STATE_DRAGGING:
         case RecyclerView.SCROLL_STATE_SETTLING:
            Picasso.with(recyclerView.getContext()).pauseTag(picassoTag);
            break;
         case RecyclerView.SCROLL_STATE_IDLE:
         default:
            Picasso.with(recyclerView.getContext()).resumeTag(picassoTag);
            break;
      }
   }
}
