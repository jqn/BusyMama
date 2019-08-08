package io.jqn.busymama.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import timber.log.Timber;

@Database(entities = {TransactionEntry.class}, version = 2, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class BusyMamaDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "transactionlist";
    private static BusyMamaDatabase sInstance;

    public static BusyMamaDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Timber.d("Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        BusyMamaDatabase.class, BusyMamaDatabase.DATABASE_NAME)
                        // Queries should be done in a separate thread to avoid locking the UI
                        // We will allow this ONLY TEMPORALLY to see that our DB is working
                        .allowMainThreadQueries()
                        .build();
            }
        }
        Timber.d("Getting the database instance");
        return sInstance;
    }

    public abstract TransactionDao transactionDao();

}
