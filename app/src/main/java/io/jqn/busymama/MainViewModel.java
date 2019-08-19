package io.jqn.busymama;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.jqn.busymama.database.BusyMamaDatabase;
import io.jqn.busymama.database.TransactionEntry;
import timber.log.Timber;

public class MainViewModel extends AndroidViewModel {
    private LiveData<List<TransactionEntry>> transactions;

    public MainViewModel(Application application) {
        super(application);
        BusyMamaDatabase database = BusyMamaDatabase.getInstance(this.getApplication());
        Timber.d( "Actively retrieving transactions from the DataBase");
        transactions = database.transactionDao().loadAllTransactions();
    }

    public LiveData<List<TransactionEntry>> getTransactions() {
        return transactions;
    }
}
