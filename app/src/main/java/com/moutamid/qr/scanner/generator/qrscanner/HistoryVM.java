package com.moutamid.qr.scanner.generator.qrscanner;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class HistoryVM extends AndroidViewModel {

    private final HistoryRep historyRep;
    private final LiveData<List<History>> getHistoryData;


    public HistoryVM(@NonNull Application application) {
        super(application);
        historyRep = new HistoryRep(application);
        getHistoryData = historyRep.getHistoryData();
    }

    public void insertHistory(History history){
        historyRep.insertHistory(history);
    }

    public LiveData<List<History>> getHistoryData(){
        return getHistoryData;
    }

    public void deleteSingleItem(History history){
        historyRep.deleteSingleItem(history);
    }

    public void deleteAllHistory(){
        historyRep.deleteAllHistory();
    }
}
