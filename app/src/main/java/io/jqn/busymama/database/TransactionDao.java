package io.jqn.busymama.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TransactionDao {

    @Query("SELECT * FROM `transaction` ORDER BY id")
    LiveData<List<TransactionEntry>> loadAllTransactions();

    @Insert
    void insertTransaction(TransactionEntry transactionEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTransaction(TransactionEntry transactionEntry);

    @Delete
    void deleteTransaction(TransactionEntry transactionEntry);
}
