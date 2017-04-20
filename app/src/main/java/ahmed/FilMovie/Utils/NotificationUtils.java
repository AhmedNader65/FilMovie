package ahmed.FilMovie.Utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import ahmed.FilMovie.Activities.MainActivity;
import ahmed.FilMovie.R;
import ahmed.FilMovie.data.FilMoviePreferences;

/**
 * Created by ahmed on 20/04/17.
 */

public class NotificationUtils {

    private static int WEATHER_NOTIFICATION_ID=300;

    public static void notifyUser(Context context){
        CharSequence notificationTitle = context.getString(R.string.app_name);
        CharSequence notificationText = "Check out most popular movies today!";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher))
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setAutoCancel(true);

            /*
             * This Intent will be triggered when the user clicks the notification. In our case,
             * we want to open Sunshine to the DetailActivity to display the newly updated weather.
             */
        Intent detailIntentForToday = new Intent(context, MainActivity.class);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,0, detailIntentForToday,PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

            /* WEATHER_NOTIFICATION_ID allows you to update or cancel the notification later on */
        notificationManager.notify(WEATHER_NOTIFICATION_ID, notificationBuilder.build());

        FilMoviePreferences.saveLastNotificationTime(context, System.currentTimeMillis());

    }
}
