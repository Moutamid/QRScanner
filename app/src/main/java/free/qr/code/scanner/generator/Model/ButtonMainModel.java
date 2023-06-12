package free.qr.code.scanner.generator.Model;

public class ButtonMainModel {

    private final String btName;
    private final int img;

    public ButtonMainModel(String btName, int img) {
        this.btName = btName;
        this.img = img;
    }

    public String getBtName() {
        return btName;
    }

    public int getImg() {
        return img;
    }
}
