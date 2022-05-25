package fr.flareden.meetingcar.metier.entity;

import org.json.JSONException;
import org.json.JSONObject;

import fr.flareden.meetingcar.metier.entity.client.Client;

public class Visite {
    private int id;
    private Client client;
    private String horodatage;

    public Visite(int id, Client client, String horodatage) {
        this.id = id;
        this.client = client;
        this.horodatage = horodatage;
    }

    public static Visite fromJsonObject(JSONObject jsonObject) throws JSONException {
        return new Visite(
                jsonObject.getInt("id"),
                null,
                jsonObject.getString("horodatage"));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getHorodatage() {
        return horodatage;
    }

    public void setHorodatage(String horodatage) {
        this.horodatage = horodatage;
    }
}
