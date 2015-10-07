package com.prchoe.timer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TimerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = TimerActivity.class.getSimpleName();
    private Button mStartButton, mPauseButton, mResetButton;
    private TextView mTimeTextView;

    private double mTimerNumber = 0.000;

    private TimerThread mThread;

    private Message mMsg;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                mTimeTextView.setText("00:00:00:000");
            } else {
                Log.d(TAG, "" + msg.obj);
                mTimeTextView.setText(getTime((long) msg.obj));
            }

        }

        private String getTime(long milliSeconds) {
            String result = "";

            // 초
            int time = (int) milliSeconds / 1000;

            // 시간
            int hour = time / 3600;
            // 분
            int minute = (time - (hour * 3600)) / 60;
            // 초
            int second = time - ((hour * 3600) + (minute * 60));
            // 밀리초
            long milli = milliSeconds - (second * 1000);

            return (getTimeToString(hour).equals("") ? "00:" : getTimeToString(hour) + ":") +
                    (getTimeToString(minute).equals("") ? "00:" : getTimeToString(minute) + ":")
                    + (getTimeToString(second).equals("") ? "00:" : getTimeToString(second) + ":") +
                    milli;
        }

        private String getTimeToString(int time) {
            String result = "";

            if (time == 0) {
                result = "";
            } else if (time < 10) {
                result = "0" + String.valueOf(time);
            } else {
                result = String.valueOf(time);
            }

            return result;
        }

        private String stayMilliFormat(long milliSeconds) {
            String result = "";

            if (milliSeconds < 100) {
                result = "0" + milliSeconds;
            }


            return result;
        }
    };

    public class TimerThread extends Thread {

        private boolean runningState = true;
        private long milliSeconds = 0;

        @Override
        public void run() {
            while (true) {
                if (runningState) {
                    Log.d(TAG, "state : run");
                    milliSeconds++;
                    mMsg = new Message();
                    mMsg.obj = milliSeconds;
                    mHandler.sendMessage(mMsg);

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        break;
                    }

                } else {
                    Log.d(TAG, "state : paused");

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }

        public void changeRunningState(boolean state) {
            runningState = state;
        }

        public void reset() {
            milliSeconds = 0;
            mMsg = new Message();
            mMsg.obj = 0;
            mMsg.arg1 = 1;
            mHandler.sendMessage(mMsg);
            Log.d(TAG, "state : reset");

        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        mTimeTextView = (TextView) findViewById(R.id.time_tv);

        mStartButton = (Button) findViewById(R.id.start_btn);
        mPauseButton = (Button) findViewById(R.id.pause_btn);
        mResetButton = (Button) findViewById(R.id.reset_btn);

        mStartButton.setOnClickListener(this);
        mPauseButton.setOnClickListener(this);
        mResetButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_btn:
                if (mThread == null) {
                    mThread = new TimerThread();
                    mThread.start();
                } else {
                    mThread.changeRunningState(true);
                }

                if (mThread.getState().equals(Thread.State.TERMINATED)) {
                    mThread = new TimerThread();
                    mThread.start();
                }
                break;
            case R.id.pause_btn:
                if (mThread != null) {
                    mThread.changeRunningState(false);
                }
                break;
            case R.id.reset_btn:
                if (mThread != null) {
                    if (mThread.isInterrupted() == false) {
                        mThread.reset();
                        mThread.interrupt();
                    }
                }
                break;

        }

    }


}
