package com.davinci.android.data;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.davinci.android.R;
import com.davinci.android.ui.MainActivity;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.davinci.android.util.Constants.API_WORK;
import static com.davinci.android.util.Constants.API_WORK_NOTIF;
import static com.davinci.android.util.Constants.API_WORK_NOTIF_ID;
import static com.davinci.android.util.Constants.MORNING_PATH;
import static com.davinci.android.util.Constants.NIGHT;
import static com.davinci.android.util.Constants.NIGHT_PATH;

public class ApiWorker extends Worker {

    private Gson gson;
    private Context context;
    private final Result[] result = {Result.retry()};

    public ApiWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.gson = new Gson();
    }

    @NonNull
    @Override
    public Result doWork() {
        Generator.initClient().getClasses((Generator.getTurn(context) == NIGHT)
                ? NIGHT_PATH
                : MORNING_PATH)
                .subscribe(classes -> {
                    String api = gson.toJson(classes);
                    if (!api.contentEquals(Generator.getSavedClasses(context))) {
                        showNotification();
                        Generator.saveClasses(context, gson.toJson(classes));
                    }
                    result[0] = Result.success();
                }, error -> result[0] = Result.failure())
                .dispose();
        return result[0];
    }

//    private void repeatTask() {
//        OneTimeWorkRequest one = new OneTimeWorkRequest.Builder(ApiWorker.class)
//                .setInitialDelay(30, TimeUnit.SECONDS)
//                .setConstraints(new Constraints.Builder()
//                        .setRequiredNetworkType(NetworkType.CONNECTED)
//                        .build())
//                .build();
//
//        WorkManager.getInstance().enqueueUniqueWork(API_WORK, ExistingWorkPolicy.REPLACE, one);
//    }

    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(API_WORK_NOTIF_ID, API_WORK, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, API_WORK_NOTIF_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.da_vinci))
                .setContentText(context.getString(R.string.new_content))
                .setContentIntent(intent)
                .setAutoCancel(true);

        notificationManager.notify(API_WORK_NOTIF, notification.build());
    }
}