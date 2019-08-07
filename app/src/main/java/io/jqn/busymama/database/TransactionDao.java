package io.jqn.busymama.database;

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
    List<TransactionEntry> loadAllTasks();

    @Insert
    void insertTask(TransactionEntry transactionEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(TransactionEntry transactionEntry);

    @Delete
    void deleteTask(TransactionEntry transactionEntry);
}
