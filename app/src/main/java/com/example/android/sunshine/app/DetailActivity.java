package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class DetailActivity extends ActionBarActivity {


    private ShareActionProvider mShareActionProvider;
    private String forecastString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView textView = (TextView) findViewById(R.id.detail_text);
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String forecastString = intent.getStringExtra(Intent.EXTRA_TEXT);
            textView.setText(forecastString);
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, forecastString);
        return shareIntent;
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        getMenuInflater().inflate(R.menu.share, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if(mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        } else {
            Log.e("LOIDJF", "mShareActionProvider is null");
        }
        return true;
    }

    private void setShareIntent(Intent intent) {
        if(mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(intent);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_location) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String lat = sharedPreferences.getString(getString(R.string.pref_location_key_lat), getString(R.string.pref_location_default_lat));
            String lon = sharedPreferences.getString(getString(R.string.pref_location_key_long), getString(R.string.pref_location_default_long));

            intent.setData(Uri.parse("geo:" + lat + "," + lon));
            startActivity(intent);
            return true;
        } else if (id == R.id.action_share) {
            createShareIntent();
        }

        return super.onOptionsItemSelected(item);
    }
}
