package ru.fmtk.synchroleacks;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


import java.lang.ref.WeakReference;

import static android.text.TextUtils.isEmpty;

public class MainActivity extends AppCompatActivity {

    private static final int STATUS_MSG = 1;

    @Nullable
    private Handler handler;

    @Nullable
    private TextView tvText;

    @Nullable
    private VersatileLeg thLeft;

    @Nullable
    private VersatileLeg thRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart() {
        super.onStart();

        tvText = findViewById(R.id.tv_text);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == STATUS_MSG) {
                    setText((String)msg.obj);
                }
            }
        };

        Step currentStep = new Step(Step.StepSide.Left);
        thLeft = new VersatileLeg(Step.StepSide.Left, currentStep, handler);
        thRight = new VersatileLeg(Step.StepSide.Right, currentStep, handler);
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
        if(handler != null) handler.removeCallbacksAndMessages(null);
        tvText = null;
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
        private final WeakReference<Handler> weakHandler;

        public VersatileLeg(Step.StepSide side, Step start, @NonNull Handler handler) {
            this.side = side;
            this.current = start;
            this.weakHandler = new WeakReference<>(handler);
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
            sendText(side.getName() + " leg stopped!");
        }

        public void sendText(String text) {
            System.out.println("Call " + text + "; from: " + Thread.currentThread().toString());
            Handler handler = weakHandler.get();
            if (handler != null) {
                handler.sendMessage(
                        handler.obtainMessage(MainActivity.STATUS_MSG, text));
            }
        }

        public void stopMe() {
            isRunning = false;
        }
    }
}
