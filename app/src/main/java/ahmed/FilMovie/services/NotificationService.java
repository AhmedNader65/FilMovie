package ahmed.FilMovie.services;

import android.app.IntentService;
import android.content.Intent;

import ahmed.FilMovie.Utils.FilMovieTasks;

/**
 * Created by ahmed on 20/04/17.
 */

public class NotificationService extends IntentService {

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        FilMovieTasks.notificationTask(this);
    }
}