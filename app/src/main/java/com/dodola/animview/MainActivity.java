package com.dodola.animview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private MetaballView metaballView;
    private MetaballDebugView debugMetaballView;
    private SeekBar seekBar, seekBar2, seekBar3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        metaballView = (MetaballView) this.findViewById(R.id.metaball);
        debugMetaballView = (MetaballDebugView) findViewById(R.id.debug_metaball);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
        seekBar3 = (SeekBar) findViewById(R.id.seekBar3);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar2.setOnSeekBarChangeListener(this);
        seekBar3.setOnSeekBarChangeListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_fill) {
            metaballView.setPaintMode(1);
            debugMetaballView.setPaintMode(1);
            return true;
        } else if (id == R.id.action_strock) {
            metaballView.setPaintMode(0);
            debugMetaballView.setPaintMode(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.seekBar:
                debugMetaballView.setMaxDistance(progress);
                break;
            case R.id.seekBar2:
                debugMetaballView.setMv(progress / 100f);
                break;
            case R.id.seekBar3:
                debugMetaballView.setHandleLenRate(progress / 100f);
                break;
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
