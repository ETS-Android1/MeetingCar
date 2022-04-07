package fr.flareden.meetingcar;

public class AdvertViewModel {

    public enum TYPE {
        RENT,
        SELL
    }

    // ATTRIBUTES
    private String title, desc, loc, price;
    private TYPE type;
    // private images?

    // CONSTRUCTOR
    AdvertViewModel(String title, String desc, String loc, String price, TYPE type) {
        this.title = title;
        this.desc = desc;
        this.loc = loc;
        this.price = price;
        this.type = type;
    }

    // GETTERS
    public String getTitle() {
        return this.title;
    }

    public String getDesc() {
        return this.desc;
    }

    public String getLoc() {
        return this.loc;
    }

    public String getPrice() {
        return this.price;
    }

    public TYPE getType() {
        return this.type;
    }

    // SETTERS
    public void getTitle(String s) {
        this.title = s;
    }

    public void getDesc(String s) {
        this.desc = s;
    }

    public void getLoc(String s) {
        this.loc = s;
    }

    public void getPrice(String s) {
        this.price = s;
    }

    public void getType(TYPE t) {
        this.type = t;
    }

    // SEARCH
    public String getSearchString() {
        return this.title + " " + this.desc + " " + this.loc;
    }
}
