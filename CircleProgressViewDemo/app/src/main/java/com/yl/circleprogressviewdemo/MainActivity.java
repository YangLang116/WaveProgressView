package com.yl.circleprogressviewdemo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.yl.waveprogresslib.WaveProgressView;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private static int[] ids = {R.id.id_1, R.id.id_2, R.id.id_3, R.id.id_4,
            R.id.id_5, R.id.id_5, R.id.id_7, R.id.id_8, R.id.id_9, R.id.id_10,
            R.id.id_11, R.id.id_12};
    private SeekBar[] mSeekBars = new SeekBar[ids.length];

    private WaveProgressView mWaveProgressView;
    private EditText mEdittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWaveProgressView = (WaveProgressView) findViewById(R.id.id_waveprogressview);
        mEdittext = (EditText) findViewById(R.id.id_contentText);
        for (int i = 0; i < ids.length; i++) {
            SeekBar seekBar = (SeekBar) findViewById(ids[i]);
            seekBar.setOnSeekBarChangeListener(this);
            mSeekBars[i] = seekBar;
        }
    }

    public void changeText(View view) {
        String mContent = mEdittext.getText().toString();
        mWaveProgressView.setmContentText(mContent);
    }

    public void changeImg(View view) {
        mWaveProgressView.setmBackgroudDraw(getResources().getDrawable(R.mipmap.ic_launcher));
    }

    public void startAnim(View view) {
        mWaveProgressView.mStartAnimation(true);
    }

    public void stopAnim(View view) {
        mWaveProgressView.mEndAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWaveProgressView.mEndAnimation(); //避免开启动画没有调用关闭方法
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int currentId = seekBar.getId();
        switch (currentId) {
            case R.id.id_1:
                mWaveProgressView.setmBackgroudColor(Color.argb(255,255,255,progress));
                break;
            case R.id.id_2:
                mWaveProgressView.setmCircleRingWidth(progress);
                break;
            case R.id.id_3:
                mWaveProgressView.setmCircleRingColor(Color.argb(255,progress,255,255));
                break;
            case R.id.id_4:
                mWaveProgressView.setmWaveColor(Color.argb(255,255,progress,255));
                break;
            case R.id.id_5:
                mWaveProgressView.setmMaxWaveNum(progress);
                break;
            case R.id.id_6:
                mWaveProgressView.setmWaveNumRange(progress);
                break;
            case R.id.id_7:
                mWaveProgressView.setmWaveHeight(progress);
                break;
            case R.id.id_8:
                mWaveProgressView.setmWaveHeightRange(progress);
                break;
            case R.id.id_9:
                mWaveProgressView.setmCurrentWaveHeightPercent(progress * 1.0f/ 100);
                break;
            case R.id.id_10:
                mWaveProgressView.setmContentTextSize(progress);
                break;
            case R.id.id_11:
                mWaveProgressView.setmContentTextColor(Color.argb(255,progress,255,255));
                break;
            case R.id.id_12:
                mWaveProgressView.setmContentTextPaading(progress);
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
