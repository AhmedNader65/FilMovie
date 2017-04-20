package ahmed.FilMovie.Utils;

import android.content.Context;
import android.text.format.DateUtils;

import ahmed.FilMovie.data.FilMoviePreferences;

/**
 * Created by ahmed on 20/04/17.
 */

public class FilMovieTasks {

    synchronized public static void notificationTask(Context context){
        boolean notificationsEnabled = FilMoviePreferences.areNotificationsEnabled(context);

                /*
                 * If the last notification was shown was more than 1 day ago, we want to send
                 * another notification to the user that the weather has been updated. Remember,
                 * it's important that you shouldn't spam your users with notifications.
                 */
        long timeSinceLastNotification = FilMoviePreferences
                .getEllapsedTimeSinceLastNotification(context);

        boolean oneDayPassedSinceLastNotification = false;

        if (timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS) {
            oneDayPassedSinceLastNotification = true;
        }

                /*
                 * We only want to show the notification if the user wants them shown and we
                 * haven't shown a notification in the past day.
                 */
        if (notificationsEnabled && oneDayPassedSinceLastNotification) {
            NotificationUtils.notifyUser(context);
        }
    }
}
