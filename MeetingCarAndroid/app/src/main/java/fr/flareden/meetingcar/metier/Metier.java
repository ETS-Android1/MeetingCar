package fr.flareden.meetingcar.metier;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

import fr.flareden.meetingcar.metier.entity.client.Client;
import fr.flareden.meetingcar.metier.entity.messagerie.Messagerie;
import fr.flareden.meetingcar.metier.listener.IClientChangeHandler;
import fr.flareden.meetingcar.metier.listener.IConnectHandler;

public class Metier {
    public static Metier INSTANCE = null;

    public static Metier getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new Metier();
        }
        return INSTANCE;
    }

    private Client utilisateur;
    private Messagerie messagerie;
    private CommunicationWebservice cws;
    private ArrayList<IClientChangeHandler> clientChangeHandlerList;

    public Metier() {
        this.utilisateur = null;
        cws = CommunicationWebservice.getINSTANCE();
        clientChangeHandlerList = new ArrayList<>();
    }

    public void connection(String username, String pass, IConnectHandler callback, boolean isAutoConnect) {
        cws.connect(username, pass, callback, isAutoConnect);
    }


    public void setUtilisateur(Client c) {
        this.utilisateur = c;
        for (IClientChangeHandler ich : clientChangeHandlerList) {
            ich.onClientChange(c);
        }
    }

    public void isLogin(IConnectHandler callback) {
        if (this.utilisateur == null) {
            callback.askIsLogin(false);
        } else {
            cws.isLogin(callback);
        }
    }

    public void auto_connect(Activity activity) {
        SharedPreferences sp = activity.getSharedPreferences("auto_connect", Context.MODE_PRIVATE);
        String email = sp.getString("email", null);
        if (email != null) {
            String password = sp.getString("password", null);
            if (password != null) {
                CommunicationWebservice.getINSTANCE().connect(email, password, null, true);
            }
        }

    }

    public void disconnect() {
        this.utilisateur = null;
        cws.disconnect();
    }

    public Client getUtilisateur() {
        return utilisateur;
    }

    public void addOnClientChange(IClientChangeHandler callback){
        this.clientChangeHandlerList.add(callback);
    }
    public void removeOnClientChange(IClientChangeHandler callback){
        this.clientChangeHandlerList.remove(callback);
    }
}
