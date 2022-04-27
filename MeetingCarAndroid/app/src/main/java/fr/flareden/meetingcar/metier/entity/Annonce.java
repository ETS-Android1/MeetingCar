package fr.flareden.meetingcar.metier.entity;

import android.provider.MediaStore;

import java.util.ArrayList;

import fr.flareden.meetingcar.metier.entity.client.Client;

public class Annonce {
    private int id;
    private String title;
    private String desc;
    private float prix;
    private Client vendeur;
    private ArrayList<MediaStore.Images> photos;
    private boolean disponible;
    private Client acheteur;
    private ArrayList<Visite> visites;
    private boolean location;
    private boolean renforcer;

    // CONSTRUCTOR
    public Annonce(int id, String title, String desc, float prix, Client vendeur, ArrayList<MediaStore.Images> photos, boolean disponible, Client acheteur, ArrayList<Visite> visites, boolean location, boolean renforcer) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.prix = prix;
        this.vendeur = vendeur;
        this.photos = photos;
        this.disponible = disponible;
        this.acheteur = acheteur;
        this.visites = visites;
        this.location = location;
        this.renforcer = renforcer;
    }

    // METHODS
    public void modifier(){
        // TODO
    }
    public void supprimer(){
        // TODO
    }
    public void renforcer(){
        // TODO
    }

    // GETTER & SETTER
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public float getPrix() {
        return prix;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }

    public Client getVendeur() {
        return vendeur;
    }

    public void setVendeur(Client vendeur) {
        this.vendeur = vendeur;
    }

    public ArrayList<MediaStore.Images> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<MediaStore.Images> photos) {
        this.photos = photos;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public Client getAcheteur() {
        return acheteur;
    }

    public void setAcheteur(Client acheteur) {
        this.acheteur = acheteur;
    }

    public ArrayList<Visite> getVisites() {
        return visites;
    }

    public void addVisite(Visite v) {
        this.visites.add(v);
    }

    public boolean isLocation() {
        return location;
    }

    public void setLocation(boolean location) {
        this.location = location;
    }

    public boolean isRenforcer() {
        return renforcer;
    }

    public void setRenforcer(boolean renforcer) {
        this.renforcer = renforcer;
    }
}
