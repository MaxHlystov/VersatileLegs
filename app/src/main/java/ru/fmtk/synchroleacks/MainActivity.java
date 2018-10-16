package ru.fmtk.synchroleacks;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import java.lang.ref.WeakReference;

import static android.text.TextUtils.isEmpty;

public class MainActivity extends AppCompatActivity {

    private static final int STATUS_MSG = 1;

    @Nullable
    private TextView tvText;

    @Nullable
    private VersatileLeg thLeft;

    @Nullable
    private VersatileLeg thRight;

    @Nullable
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart() {
        super.onStart();

        tvText = findViewById(R.id.tv_text);
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == STATUS_MSG) {
                    setText((String)msg.obj);
                }
                else {
                    super.handleMessage(msg);
                }
            }
        };

        Step currentStep = new Step(Step.StepSide.Left);
        thLeft = new VersatileLeg(Step.StepSide.Left, currentStep);
        thRight = new VersatileLeg(Step.StepSide.Right, currentStep);
        new Thread(thLeft).start();
        new Thread(thRight).start();
    }

    @Override
    protected void onStop() {
        if (thLeft != null) {
            thLeft.stopMe();
            thLeft = null;
        }
        if (thRight != null) {
            thRight.stopMe();
            thRight = null;
        }
        tvText = null;
        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        handler = null;
        super.onStop();
    }

    private void setText(@Nullable String text) {
        if (tvText != null && !isEmpty(text)) {
            String oldText = tvText.getText().toString();
            String newText = oldText;
            if (oldText.length() < 200) {
                newText += text;
            } else {
                int endOfLineIdx = newText.indexOf('\n');
                newText = newText.substring(endOfLineIdx + 1) + text;
            }
            System.out.println("Set tv to: " + text + "; from: " + Thread.currentThread().toString());
            tvText.setText(newText + '\n');
        }
    }

    private static class VersatileLeg implements Runnable {
        private final Step.StepSide side;
        private final Step current;

        private boolean isRunning = true;

        @Nullable
        private Handler handler;

        public VersatileLeg(Step.StepSide side, Step start) {
            this.side = side;
            this.current = start;
            this.handler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void run() {
            while (isRunning) {
                synchronized (current) {
                    if (current.get() == side) {
                        sendText(side.getName() + " step");
                        current.toggle();
                    }
                }
            }
            sendText(side.getName() + " leg stopped!");;
        }

        public void stopMe() {
            isRunning = false;
        }

        private void sendText(String text) {
            System.out.println("Call " + text + "; from: " + Thread.currentThread().toString());
            if (handler != null) {
                Message message = handler.obtainMessage(MainActivity.STATUS_MSG, text);
                handler.sendMessage(message);
            }
        }
    }
}
