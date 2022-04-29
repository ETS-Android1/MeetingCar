package fr.flareden.meetingcar.metier.entity.client;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class Client {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String datenaissance;
    private ArrayList<MoyenCommunication> communications;
    private String adresse;

    public Client(int id, String nom, String prenom, String email, String telephone, String datenaissance, String adresse, ArrayList<MoyenCommunication> communications) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.datenaissance = datenaissance;
        this.adresse = adresse;
        this.communications = communications;

    }

    public Client(int id, String nom, String prenom, String email, String telephone, String datenaissance,String adresse) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.datenaissance = datenaissance;
        this.adresse = adresse;

        this.communications = null;
    }

    public Client() {
    }

    public void addMoyenCommunication(MoyenCommunication com){
        if(this.communications == null){
            this.communications = new ArrayList<>();
        }
        this.communications.add(com);
    }

    public void removeMoyenCommunication(MoyenCommunication mc) {
        if(this.communications != null){
            this.communications.remove(mc);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getDatenaissance() {
        return datenaissance;
    }

    public void setDatenaissance(String datenaissance) {
        this.datenaissance = datenaissance;
    }

    public ArrayList<MoyenCommunication> getCommunications() {
        return communications;
    }

    public static Client jsonReaderToClient(JsonReader in) throws IOException {
        int id = -1;
        String email = "", nom= "", prenom = "", telephone = "", date_naissance = "", adresse = "";
        in.beginObject();
        while (in.hasNext()){
            String subName = in.nextName();
            switch(subName){
                case "id":
                    id = in.nextInt();
                    break;
                case "email":
                    email = in.nextString();
                    break;
                case "nom":
                    nom = in.nextString();
                    break;
                case "prenom":
                    if(in.peek() != JsonToken.NULL){
                        prenom = in.nextString();
                    } else {
                        in.nextNull();
                    }
                    break;
                case "telephone":
                    if(in.peek() != JsonToken.NULL){
                        telephone = in.nextString();
                    } else {
                        in.nextNull();
                    }
                    break;
                case "date_naissance":
                    if(in.peek() != JsonToken.NULL){
                        date_naissance = in.nextString();
                    } else {
                        in.nextNull();
                    }
                    break;
                case "adresse":
                    if(in.peek() != JsonToken.NULL){
                        adresse = in.nextString();
                    } else {
                        in.nextNull();
                    }
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }
        in.endObject();
        Client retour = null;
        if(id >= 0){
            retour = new Client(id, nom, prenom, email, telephone, date_naissance, adresse);
        }
        return retour;
    }

    @NonNull
    @Override
    public String toString() {
        return "Client " + this.id + " : " + this.email + " | " + this.nom + " " + this.prenom + " | " + this.telephone + " | " + this.datenaissance;
    }
//    public Client(int id, String nom, String prenom, String email, String telephone,String adresse, String datenaissance) {
    public static Client fromJsonObject(JSONObject obj) throws JSONException {
        return new Client(
                obj.getInt("id"),
                obj.getString("nom"),
                obj.getString("prenom"),
                obj.getString("email"),
                obj.getString("telephone"),
                obj.getString("date_naissance"),
                obj.getString("adresse")
        );
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("id", this.id);
        result.put("email", this.email);
        result.put("nom", this.nom);
        result.put("prenom",this.prenom);
        result.put("telephone", this.telephone);
        result.put("date_naissance", this.datenaissance);
        result.put("adresse", this.adresse);
        return result;
    }

}
