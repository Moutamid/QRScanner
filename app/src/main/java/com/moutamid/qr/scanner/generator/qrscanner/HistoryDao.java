package com.moutamid.qr.scanner.generator.qrscanner;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface HistoryDao {

    @Insert
    void insert(History history);


    @Query("select * from History")
    LiveData<List<History>> getHistory();

    @Delete
    void deleteSingleItem(History history);

    @Query("DELETE FROM History")
    void deleteAllData();
}