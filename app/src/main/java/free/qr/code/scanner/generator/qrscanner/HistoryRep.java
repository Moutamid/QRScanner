package free.qr.code.scanner.generator.qrscanner;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class HistoryRep {

    private final HistoryDao historyDao;
    private final LiveData<List<History>> historyData;

    public HistoryRep(Application application) {
        QRScanner qrScanner = QRScanner.getInstance(application);
        historyDao = qrScanner.historyDao();
        historyData = historyDao.getHistory();
    }

    public LiveData<List<History>> getHistoryData(){
        return historyData;
    }

    public void insertHistory(History history){
        QRScanner.databaseWriteExecutor.execute(() -> historyDao.insert(history));
    }

    public void deleteSingleItem(History history){
        QRScanner.databaseWriteExecutor.execute(() -> historyDao.deleteSingleItem(history));
    }

    public void deleteAllHistory(){
        QRScanner.databaseWriteExecutor.execute(historyDao::deleteAllData);

    }

}
