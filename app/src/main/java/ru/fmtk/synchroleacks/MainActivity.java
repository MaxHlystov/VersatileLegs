package ru.fmtk.synchroleacks;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
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

public class MainActivity extends AppCompatActivity implements IDataView {

    @Nullable
    private TextView tvText;

    @Nullable
    private AsyncTaskLeg asyncLeft;

    @Nullable
    private AsyncTaskLeg asyncRight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart() {
        super.onStart();

        tvText = findViewById(R.id.tv_text);

        Step currentStep = new Step(Step.StepSide.Left);
        asyncLeft = new AsyncTaskLeg(Step.StepSide.Left, currentStep, this);
        asyncRight = new AsyncTaskLeg(Step.StepSide.Right, currentStep, this);
        //startMyTask(asyncLeft);
        //startMyTask(asyncRight);
        asyncLeft.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        asyncRight.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onStop() {
        tvText = null;
        asyncLeft.cancel(false);
        asyncRight.cancel(false);
        asyncLeft = null;
        asyncRight = null;
        super.onStop();
    }

    public void setText(@Nullable String text) {
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
}
