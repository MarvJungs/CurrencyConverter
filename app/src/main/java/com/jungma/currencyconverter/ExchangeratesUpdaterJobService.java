package com.jungma.currencyconverter;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import java.time.LocalTime;

public class ExchangeratesUpdaterJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        new Thread(() -> {
           Log.d("JobInfo", "Fetching currencies at " + LocalTime.now().toString());
            ExchangeRateUpdateRunnable exchangeRateUpdateRunnable = new ExchangeRateUpdateRunnable(getApplicationContext());
            exchangeRateUpdateRunnable.run();
        }).start();
        jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("JobInfo", "Job finished");
        return false;
    }
}
