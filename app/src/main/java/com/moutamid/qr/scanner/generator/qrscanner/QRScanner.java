package com.moutamid.qr.scanner.generator.qrscanner;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = History.class, version = 1, exportSchema = false)
public abstract class QRScanner extends RoomDatabase {

    private static QRScanner INSTANCE;
    public abstract HistoryDao historyDao();
    private static final int NUMBER_OF_THREADS = 10;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static synchronized QRScanner getInstance(Context context){

        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    QRScanner.class, QRScanner.class.getName())
                    .fallbackToDestructiveMigration()
                    .enableMultiInstanceInvalidation()
                    .build();
        }

        return INSTANCE;
    }
}
