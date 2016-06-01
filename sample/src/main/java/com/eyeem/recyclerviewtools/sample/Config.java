package com.eyeem.recyclerviewtools.sample;

import java.io.Serializable;

/**
 * Created by budius on 26.05.16.
 */
public class Config implements Serializable {

   public static final String TAG = "config.tag";

   public boolean useCoordinatorLayout = false;
   public boolean useSections = false;
   public boolean useHeader = false;
   public boolean useLoadMore = false;
   public boolean useOnItemClick = false;
   public boolean useItemOffsetDecoration = false;

   public static final int LINEAR_LAYOUT_MANAGER = 0;
   public static final int GRID_LAYOUT_MANAGER = 1;
   public static final int STAGGERED_GRID_LAYOUT_MANAGER = 2;
   public int layoutManager = LINEAR_LAYOUT_MANAGER;
   public int layoutManagerSpan = 2;


}
