package com.randomsegment.apn.ismshuttle;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.ThemeSingleton;
import com.afollestad.materialdialogs.util.DialogUtils;

import java.io.File;


public class Setting extends AppCompatActivity implements
        FolderSelectorDialog.FolderSelectCallback, ColorChooserDialog.Callback {

        int refresh_time = 0;
        private Toast mToast;
        private Thread mThread;

    private void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void startThread(Runnable run) {
        if (mThread != null)
            mThread.interrupt();
        mThread = new Thread(run);
        mThread.start();
    }

    private void showToast(@StringRes int message) {
        showToast(getString(message));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Version 2 part
        /*
        findViewById(R.id.theme).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomColorChooser();
            }
        });

        */

        // Save the refresh time



        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        refresh_time = preferences.getInt("Refresh",refresh_time);

        findViewById(R.id.refresh_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSingleChoice();
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mThread != null && !mThread.isInterrupted() && mThread.isAlive())
            mThread.interrupt();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }


    static int selectedColorIndex = -1;

    private void showCustomColorChooser() {
        new ColorChooserDialog().show(this, selectedColorIndex);
    }


    public void onColorSelection(int index, int color, int darker) {
        selectedColorIndex = index;
        //noinspection ConstantConditions
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
        ThemeSingleton.get().positiveColor = DialogUtils.getActionTextStateList(this, color);
        ThemeSingleton.get().neutralColor = DialogUtils.getActionTextStateList(this, color);
        ThemeSingleton.get().negativeColor = DialogUtils.getActionTextStateList(this, color);
        ThemeSingleton.get().widgetColor = color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(darker);
            getWindow().setNavigationBarColor(color);
        }
    }

    private void showSingleChoice() {
        new MaterialDialog.Builder(this)
                .title(R.string.refresh)
                .items(R.array.refresh_time)
                .itemsCallbackSingleChoice(refresh_time, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        showToast(which + ": " + text);
                        refresh_time = which;
                        return true; // allow selection
                    }
                })
                .positiveText(R.string.choose)
                .show();
        // Store
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("Refresh",refresh_time);
        editor.apply();
    }

    @Override
    public void onFolderSelection(File folder)  {
        showToast(folder.getAbsolutePath());
    }
}
