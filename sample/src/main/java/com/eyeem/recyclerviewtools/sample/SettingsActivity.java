package com.eyeem.recyclerviewtools.sample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.settings_activity);
      setResult(RESULT_CANCELED);
   }

   @Override protected void onStart() {
      super.onStart();
      PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
   }

   @Override protected void onStop() {
      PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
      super.onStop();
   }

   @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
      setResult(RESULT_OK);
   }
}
