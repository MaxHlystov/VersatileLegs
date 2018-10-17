package ru.fmtk.synchroleacks;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

public class AsyncTaskLeg extends AsyncTask<Void, String, Void> {

    private final Step.StepSide side;
    private final Step current;

    @Nullable
    private WeakReference<IDataView> weakViewer;

    public AsyncTaskLeg(Step.StepSide side, Step start) {
        this.side = side;
        this.current = start;
        this.weakViewer = null;
    }

    public void setViewer(IDataView viewer) {
        this.weakViewer = new WeakReference<>(viewer);
    }

    public void unlinkViewer() {
        this.weakViewer = null;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while (!isCancelled()) {
            synchronized (current) {
                if (current.get() == side) {
                    current.toggle();
                    publishProgress(side.getName() + " step");
                }
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        sendText(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        sendText(side.getName() + " leg stopped!");
    }

    private void sendText(String text) {
        System.out.println("Call " + text + "; from: " + Thread.currentThread().toString());
        if(weakViewer != null) {
            IDataView viewer = weakViewer.get();
            if (viewer != null) {
                viewer.setText(text);
            }
        }
    }
}
