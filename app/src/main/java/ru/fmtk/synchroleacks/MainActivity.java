package ru.fmtk.synchroleacks;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private static final String logTag = "MyApp";

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

        TextView tvText = findViewById(R.id.tv_text);
        Step currentStep = new Step(Step.StepSide.Left);
        thLeft = new VersatileLeg(Step.StepSide.Left, currentStep, tvText);
        thRight = new VersatileLeg(Step.StepSide.Right, currentStep, tvText);
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
        super.onStop();
    }

    private static class VersatileLeg implements Runnable {
        private final Step.StepSide side;
        private final Step current;

        private boolean isRunning = true;
        private WeakReference<TextView> wtv;

        public VersatileLeg(Step.StepSide side, Step start, TextView tv) {
            this.side = side;
            this.current = start;
            this.wtv = new WeakReference<>(tv);
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
            TextView textView = wtv.get();
            if(textView != null) {
                textView.post(() -> {
                    TextView tv = wtv.get();
                    if(tv != null) {
                        String oldText = tv.getText().toString();
                        String newText = oldText;
                        if(oldText.length() < 200) {
                            newText += text;
                        }
                        else {
                            int endOfLineIdx = newText.indexOf('\n');
                            newText = newText.substring(endOfLineIdx+1) + text;
                        }
                        System.out.println("Set tv to: " + text + "; from: " + Thread.currentThread().toString());
                        tv.setText(newText + '\n');
                    }
                });
            }
        }

        public void stopMe() {
            isRunning = false;
        }
    }
}
