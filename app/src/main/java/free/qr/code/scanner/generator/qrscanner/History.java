package free.qr.code.scanner.generator.qrscanner;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class History {

    @PrimaryKey(autoGenerate = true)
    private int id=0;

    private final String data;

    private final String type;

    public History(String data, String type) {
        this.data = data;
        this.type = type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public String getType() {
        return type;
    }
}
