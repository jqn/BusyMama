package io.jqn.busymama.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import io.jqn.busymama.ui.DashboardActivity;
import io.jqn.busymama.R;

public class NotificationUtils {

    private static final int EXPENSE_REMINDER_NOTIFICATION_ID = 1708;
    private static final int EXPENSE_REMINDER_PENDING_INTENT_ID = 1980;
    private static final String EXPENSE_REMINDER_NOTIFICATION_CHANNEL_ID = "reminder_notification_channel";

    public static void remindUserNearShop(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    EXPENSE_REMINDER_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, EXPENSE_REMINDER_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_local_grocery_store_black_24dp)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.expense_reminder_notification_title))
                .setContentText(context.getString(R.string.expense_reminder_notification_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.expense_reminder_notification_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        notificationManager.notify(EXPENSE_REMINDER_NOTIFICATION_ID, notificationBuilder.build());

    }

    private static PendingIntent contentIntent(Context context) {
        // Open up DashboardActivity when notification is clicked
        Intent startActivityIntent = new Intent(context, DashboardActivity.class);
        return  PendingIntent.getActivity(context, EXPENSE_REMINDER_PENDING_INTENT_ID, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    private static Bitmap largeIcon(Context context) {
        Resources res = context.getResources();

        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.ic_local_grocery_store_black_24dp);
        return largeIcon;
    }
}
