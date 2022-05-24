package fr.flareden.meetingcar.ui.home;

import fr.flareden.meetingcar.metier.entity.Annonce;

public class AdvertViewModel implements IViewModel{

    public enum TYPE {
        RENT,
        SELL
    }

    // ATTRIBUTES
    private String title, desc, loc, price;
    private TYPE type;
    private int id;
    // private images?

    // CONSTRUCTOR
    public AdvertViewModel(int id, String title, String desc, String loc, String price, TYPE type) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.loc = loc;
        this.price = price;
        this.type = type;
    }
    public AdvertViewModel(Annonce annonce){

    }


    // GETTERS
    public int getId() {
        return this.id;
    }

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
    @Override
    public String getSearchString() {
        return this.title + " " + this.desc + " " + this.loc;
    }
}
