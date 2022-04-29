package fr.flareden.meetingcar.metier;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import fr.flareden.meetingcar.MainActivity;
import fr.flareden.meetingcar.login.LoginActivity;
import fr.flareden.meetingcar.metier.entity.client.Client;
import fr.flareden.meetingcar.metier.entity.messagerie.Messagerie;
import fr.flareden.meetingcar.metier.listener.IConnectHandler;

public class Metier  {
    public static Metier INSTANCE = null;

    public static Metier getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new Metier();
        }
        return INSTANCE;
    }

    Client utilisateur;
    Messagerie messagerie;
    CommunicationWebservice cws;

    public Metier() {
        this.utilisateur = null;
        cws = CommunicationWebservice.getINSTANCE();
    }

    public void connection(String username, String pass, IConnectHandler callback, boolean isAutoConnect) {
        cws.connect(username, pass, callback,isAutoConnect);
    }


    public void setUtilisateur(Client c) {
        this.utilisateur = c;
    }

    public void isLogin(IConnectHandler callback){
        if(this.utilisateur == null){
            callback.askIsLogin(false);
        } else {
            cws.isLogin(callback);
        }
    }

    public void auto_connect(Activity activity){
        SharedPreferences sp = activity.getSharedPreferences("auto_connect", Context.MODE_PRIVATE);
        String email = sp.getString("email", null);
        if(email != null){
            String password = sp.getString("password", null);
            if(password != null){
                CommunicationWebservice.getINSTANCE().connect(email, password,null, true);
            }
        }

    }

    public void launch_connection(Context context, Class<?> classe){
        Intent loginIntent = new Intent(context, LoginActivity.class);
        loginIntent.putExtra("calling-activity", classe);
        context.startActivity(loginIntent);
    }
}
