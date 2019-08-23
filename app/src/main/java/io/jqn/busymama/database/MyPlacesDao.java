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
public interface MyPlacesDao {
    @Query("SELECT * FROM `myplaces` ORDER BY id")
    LiveData<List<MyPlacesEntry>> loadAllPlaces();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMyPlace(MyPlacesEntry myPlacesEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMyPlace(MyPlacesEntry myPlacesEntry);

    @Delete
    void deleteMyPlace(MyPlacesEntry myPlacesEntry);

    @Query("SELECT * FROM `myplaces` WHERE id = :id")
    LiveData<MyPlacesEntry> loadMyPlacesById(int id);
}
