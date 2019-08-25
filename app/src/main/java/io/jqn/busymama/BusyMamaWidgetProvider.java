package io.jqn.busymama;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import io.jqn.busymama.ui.LoginActivity;

/**
 * Implementation of App Widget functionality.
 */
public class BusyMamaWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, double transactionAmount,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_placeholder_amount);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.busy_mama_widget_provider);

        views.setTextViewText(R.id.transaction_amount, Double.toString(transactionAmount));

        // Create an intent to launch LoginActivity when clicked
        Intent intent = new Intent(context, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Widgets allow click handlers to only launch pending intents
//        views.setOnClickPendingIntent(R.id.transaction_container, pendingIntent);
        views.setOnClickPendingIntent(R.id.appwidget_title, pendingIntent);

        Intent lastTransactionIntent = new Intent(context, LatestTransactionService.class);
        lastTransactionIntent.setAction(LatestTransactionService.ACTION_GET_LAST_TRANSACTION);
        PendingIntent lastTransactionPendingIntent = PendingIntent.getService(context, 0, lastTransactionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.transaction_amount, lastTransactionPendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Start the intent service update widget action, the service takes care of updating the widgets UI
        LatestTransactionService.startActionLatestTransaction(context);
    }


    public static void updateBusyMamaWidgets(Context context, AppWidgetManager appWidgetManager, double transactionAmount, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, transactionAmount, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

