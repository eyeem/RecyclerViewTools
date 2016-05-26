package com.eyeem.recyclerviewtools.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by budius on 26.05.16.
 */
public class ChooserActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

   @Bind(R.id.toolbar) Toolbar toolbar;
   @Bind(R.id.useCoordinatorLayout) Switch useCoordinatorLayout;
   @Bind(R.id.useSections) Switch useSections;
   @Bind(R.id.useHeader) Switch useHeader;
   @Bind(R.id.useLoadMore) Switch useLoadMore;
   @Bind(R.id.useOnItemClick) Switch useOnItemClick;
   @Bind(R.id.layoutManager) Spinner layoutManager;
   @Bind(R.id.layoutManagerSpan) Spinner layoutManagerSpan;

   private Config config;

   @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.chooser);
      ButterKnife.bind(this);

      toolbar.inflateMenu(R.menu.chooser_menu);
      toolbar.setOnMenuItemClickListener(this);

      layoutManager.setAdapter(new Adapter(this,
         Arrays.asList(
            "Linear Layout Manager",
            "Grid Layout Manager",
            "Staggered Grid Layout Manager")));

      layoutManagerSpan.setAdapter(new Adapter(this,
         Arrays.asList(
            "2 spans",
            "3 spans",
            "4 spans"
         )));

      if (savedInstanceState == null) {
         config = new Config();
      } else {
         config = (Config) savedInstanceState.getSerializable(Config.TAG);
         useCoordinatorLayout.setChecked(config.useCoordinatorLayout);
         useSections.setChecked(config.useSections);
         useHeader.setChecked(config.useHeader);
         useLoadMore.setChecked(config.useLoadMore);
         useOnItemClick.setChecked(config.useOnItemClick);
         layoutManager.setSelection(config.layoutManager);
         layoutManagerSpan.setSelection(config.layoutManagerSpan - 2);
      }

      useCoordinatorLayout.setOnCheckedChangeListener(this);
      useSections.setOnCheckedChangeListener(this);
      useHeader.setOnCheckedChangeListener(this);
      useLoadMore.setOnCheckedChangeListener(this);
      useOnItemClick.setOnCheckedChangeListener(this);

      layoutManager.setOnItemSelectedListener(this);
      layoutManagerSpan.setOnItemSelectedListener(this);
      layoutManagerSpan.setEnabled(config.layoutManager != 0);

   }

   @Override protected void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      outState.putSerializable(Config.TAG, config);
   }

   @Override public boolean onMenuItemClick(MenuItem item) {
      Intent i = new Intent(this, SampleActivity.class);
      i.putExtra(Config.TAG, config);
      startActivity(i);
      return true;
   }

   @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
      switch (buttonView.getId()) {
         case R.id.useCoordinatorLayout:
            config.useCoordinatorLayout = isChecked;
            break;
         case R.id.useSections:
            config.useSections = isChecked;
            break;
         case R.id.useHeader:
            config.useHeader = isChecked;
            break;
         case R.id.useLoadMore:
            config.useLoadMore = isChecked;
            break;
         case R.id.useOnItemClick:
            config.useOnItemClick = isChecked;
            break;
      }
   }

   @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      switch (parent.getId()) {
         case R.id.layoutManager:
            config.layoutManager = position;
            layoutManagerSpan.setEnabled(config.layoutManager != 0);
            break;
         case R.id.layoutManagerSpan:
            config.layoutManagerSpan = position + 2;
            break;
      }
   }

   @Override public void onNothingSelected(AdapterView<?> parent) {
      onItemSelected(parent, null, 0, 0);
   }

   private static class Adapter extends ArrayAdapter<String> {

      public Adapter(Context context, List<String> objects) {
         super(context, android.R.layout.simple_spinner_dropdown_item, objects);
      }
   }

}
