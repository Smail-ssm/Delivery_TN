package com.xdev.deliverytn;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    //LocaleConfigurationUtil.class
    static int seekBarValue;
    static TextView val;

    public static Context adjustFontSize(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        // This will apply to all text like -> Your given text size * fontScale
        configuration.fontScale = seekBarValue * 1.0f;

        return context.createConfigurationContext(configuration);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsActivity.adjustFontSize(this);
        setContentView(R.layout.settings_activity);
        SeekBar seekBar = findViewById(R.id.seekBar);
        val = findViewById(R.id.val);
        // initiate the Seek bar
        val.setText(String.valueOf((int) getApplicationContext().getResources().getConfiguration().fontScale));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                val.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });

        int maxValue = seekBar.getMax(); // get maximum value of the Seek bar
        seekBarValue = seekBar.getProgress(); // get progress value from the Seek bar
    }

}