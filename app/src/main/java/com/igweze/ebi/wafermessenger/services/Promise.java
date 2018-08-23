package com.igweze.ebi.wafermessenger.services;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.igweze.ebi.wafermessenger.Functions.Consumer;
import com.igweze.ebi.wafermessenger.Functions.Supplier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Promise<T> {

    private T result;
    private String errorMessage;
    private boolean isSuccess = false;
    private boolean isCompleted = false;
    private List<Consumer<T>> successCallbacks = new ArrayList<>();
    private List<Consumer<String>> failureCallbacks = new ArrayList<>();

    public Promise(Supplier<T> supplier) {
        this.startExecution(supplier);
    }

    private void startExecution(Supplier<T> action) {
        // get the executor instance
        PromiseExecutors instance = PromiseExecutors.getInstance();
        // execute action in background thread
        instance.getBackgroundThread().execute(() -> {
            // completion action to be
            // executed on main thread
            Runnable completion;
            try {
                result = action.get();
                this.isSuccess = true;
                this.isCompleted = true;
                // call success handlers
                completion = () -> {
                    for (Consumer<T> successHandler : successCallbacks) {
                        successHandler.accept(result);
                    }
                };
            }
            catch (Exception ex) {
                this.isSuccess = false;
                this.isCompleted = true;
                this.errorMessage = ex.getMessage();
                // call failure handlers
                completion = () -> {
                    for (Consumer<String> handler : failureCallbacks) {
                        handler.accept(this.errorMessage);
                    }
                };
            }

            // execute completion on main thread
            instance.getMainThread().execute(completion);
        });
    }

    public Promise<T> onsuccess(Consumer<T> thenable) {
        if (isCompleted && isSuccess) thenable.accept(result);
        else this.successCallbacks.add(thenable);

        return this;
    }

    public Promise<T> onfailure(Consumer<String> catcher) {
        if (isCompleted && !isSuccess) catcher.accept(errorMessage);
        else this.failureCallbacks.add(catcher);

        return this;
    }

}


class PromiseExecutors {

    private final static String TAG = PromiseExecutors.class.getSimpleName();
    private final static Object LOCK = new Object();
    private static PromiseExecutors sInstance = null;

    private Executor mainThread;
    private Executor backgroundThread;

    public static PromiseExecutors getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Creating a new PromiseExecutor Instance");
                sInstance = new PromiseExecutors(new MainThreadExecutor(), Executors.newFixedThreadPool(3));
            }
        }

        return sInstance;
    }

    private PromiseExecutors(Executor mainThread, Executor backgroundThread) {
        this.mainThread = mainThread;
        this.backgroundThread = backgroundThread;
    }

    public Executor getMainThread() {
        return mainThread;
    }

    public Executor getBackgroundThread() {
        return backgroundThread;
    }

    static class MainThreadExecutor implements Executor {

        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}