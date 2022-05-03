package fr.flareden.meetingcar.metier.entity;

import android.graphics.drawable.Drawable;

public class Image {
    private int id;
    private Drawable drawable;
    private boolean toDelete = false;

    public Image() {
        this.id = -1;
        this.drawable = null;
    }

    public Image(int id) {
        this.id = id;
        this.drawable = null;
    }

    public Image(int id, Drawable drawable) {
        this.id = id;
        this.drawable = drawable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public boolean isToDelete() {
        return toDelete;
    }

    public void setToDelete(boolean toDelete) {
        this.toDelete = toDelete;
    }
}
