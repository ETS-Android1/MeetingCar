package fr.flareden.meetingcar.metier.entity;

import android.graphics.drawable.Drawable;
import android.provider.MediaStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.flareden.meetingcar.metier.entity.client.Client;

public class Annonce {
    private int id;
    private String title;
    private String desc;
    private float prix;
    private Client vendeur;
    private ArrayList<Image> photos;
    private boolean disponible;
    private Client acheteur;
    private ArrayList<Visite> visites;
    private boolean location;
    private boolean renforcer;

    // CONSTRUCTOR
    public Annonce(int id, String title, String desc, float prix, Client vendeur, ArrayList<Image> photos, boolean disponible, Client acheteur, ArrayList<Visite> visites, boolean location, boolean renforcer) {
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

    public static Annonce fromJsonObject(JSONObject json) throws JSONException {
        JSONObject vendeur = json.getJSONObject("vendeur");
        JSONObject acheteur = json.optJSONObject("acheteur");
        JSONArray photosID = json.getJSONArray("photos");
        ArrayList<Image> photos = new ArrayList<>();

        for(int i = 0, max = photosID.length(); i < max ; i++){
            photos.add(new Image(photosID.getInt(i)));
        }
        return new Annonce(
                json.getInt("id"),
                json.getString("titre"),
                json.getString("description"),
                (float)json.getDouble("prix"),
                new Client(vendeur.getInt("id"), vendeur.getString("nom"), vendeur.optString("prenom", ""), new Image(vendeur.optInt("photo",-1))),
                photos,
                json.getBoolean("disponible"),
                (acheteur == null ? null : new Client(vendeur.getInt("id"), vendeur.getString("nom"), vendeur.optString("prenom", ""), new Image(vendeur.optInt("photo",-1)))),
                new ArrayList<>(),
                json.getBoolean("location"),
                json.getBoolean("renforcer")
        );
    }

    // METHODS
    public void modifier() {
        // TODO
    }

    public void supprimer() {
        // TODO
    }

    public void renforcer() {
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

    public ArrayList<Image> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Image> photos) {
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

    public JSONObject toJsonObject() throws JSONException {
        JSONObject retour = new JSONObject();
        retour.put("id", this.id);
        retour.put("titre", this.title);
        retour.put("description", this.desc);
        retour.put("prix", this.prix);
        retour.put("vendeur", this.vendeur.getId());
        if (acheteur != null) {
            retour.put("acheteur",(acheteur != null) ? this.acheteur.getId() : null);
        }
        retour.put("disponible", this.disponible);
        retour.put("location", this.location);
        retour.put("renforcer", this.renforcer);

        return retour;
    }
}
