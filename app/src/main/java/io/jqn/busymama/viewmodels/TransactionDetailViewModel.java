package io.jqn.busymama.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import io.jqn.busymama.database.BusyMamaDatabase;
import io.jqn.busymama.database.TransactionEntry;

public class TransactionDetailViewModel extends ViewModel {

    // member variable
    private LiveData<TransactionEntry> transaction;
    private LiveData<TransactionEntry> latestTransaction;

    public TransactionDetailViewModel(BusyMamaDatabase database, int transactionId) {
        transaction = database.transactionDao().loadTransactionById(transactionId);

    }


    public LiveData<TransactionEntry> getTransaction() {
        return transaction;
    }
}
