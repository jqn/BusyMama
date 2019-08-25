package io.jqn.busymama;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import io.jqn.busymama.database.BusyMamaDatabase;
import io.jqn.busymama.database.TransactionEntry;
import timber.log.Timber;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class LatestTransactionService extends IntentService {
    // Member variables
    private BusyMamaDatabase mDatabase;

    public static String ACTION_GET_LAST_TRANSACTION = "io.jqn.busymama.get_last_transaction";

    public LatestTransactionService() {
        super("LatestTransactionService");
    }

    /**
     * Starts this service to perform getLastTransaction action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionLatestTransaction(Context context) {
        Intent intent = new Intent(context, LatestTransactionService.class);
        intent.setAction(ACTION_GET_LAST_TRANSACTION);
        context.startService(intent);
    }

    /**
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("LatestTransactionService started");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_LAST_TRANSACTION.equals(action)) {
                handleGetLastTransaction();
            }
        }
    }


    /**
     * Handle action WaterPlant in the provided background thread with the provided
     * parameters.
     */
    private void handleGetLastTransaction() {
        // Initialize database member variable
        mDatabase = BusyMamaDatabase.getInstance(getApplicationContext());
//        long timeNow = System.currentTimeMillis();

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                TransactionEntry transaction = mDatabase.transactionDao().loadTransactionByMaxId();
                if (transaction != null) {
                    updateWidgetsAmount(transaction.getAmount());
                }

            }
        });

    }

    private void updateWidgetsAmount(double transactionAmount) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int [] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BusyMamaWidgetProvider.class));
        // Update all widgets
        BusyMamaWidgetProvider.updateBusyMamaWidgets(this, appWidgetManager, transactionAmount, appWidgetIds);
    }
}
