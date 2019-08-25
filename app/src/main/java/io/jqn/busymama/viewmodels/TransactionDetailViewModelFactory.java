package io.jqn.busymama.viewmodels;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import io.jqn.busymama.database.BusyMamaDatabase;

public class TransactionDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    // Member variables
    private final BusyMamaDatabase mDb;
    private final int mTransactionId;

    public TransactionDetailViewModelFactory(BusyMamaDatabase database, int transactionId) {
        mDb = database;
        mTransactionId = transactionId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new TransactionDetailViewModel(mDb, mTransactionId);
    }
}
