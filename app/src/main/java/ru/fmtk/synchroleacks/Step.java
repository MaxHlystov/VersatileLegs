package ru.fmtk.synchroleacks;

import android.support.annotation.NonNull;

public class Step {
    public enum StepSide {
        Left("Left"),
        Right("Right");

        @NonNull
        private final String name;

        StepSide(@NonNull String name) { this.name = name; }

        @NonNull
        public String getName() {
            return this.name;
        }
    }

    private StepSide current;

    public Step(StepSide start) {
        this.current = start;
    }

    @NonNull
    public StepSide get() { return current; }

    public void toggle() {
        if(current == StepSide.Left) {
            current = StepSide.Right;
        }
        else {
            current = StepSide.Left;
        }
    }
}
