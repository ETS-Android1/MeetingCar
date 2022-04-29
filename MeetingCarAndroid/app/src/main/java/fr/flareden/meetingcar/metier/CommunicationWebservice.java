package fr.flareden.meetingcar.metier;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;

import fr.flareden.meetingcar.metier.entity.client.Client;
import fr.flareden.meetingcar.metier.listener.IConnectHandler;
import fr.flareden.meetingcar.metier.listener.IRegisterHandler;

public class CommunicationWebservice {
    public static boolean CONNECTED = false;
    private static String BASE_URL = "https://www.flareden.fr:9000/";
    private static CommunicationWebservice INSTANCE = null;
    private String token = "";

    private CommunicationWebservice() {
    }

    public static CommunicationWebservice getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new CommunicationWebservice();
        }
        return INSTANCE;
    }

    public void inscription(Client c, String password, IRegisterHandler callback) {
        new Thread(() -> {
            IRegisterHandler.State state = IRegisterHandler.State.SERVER_ERROR;
            try {
                HttpsURLConnection connection = (HttpsURLConnection) new URL(BASE_URL + "inscription").openConnection();
                JSONObject send = c.toJsonObject();
                send.put("mot_de_passe", hash(password));
                connection.setRequestProperty("object", send.toString());

                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                    JSONObject obj = new JSONObject(in.readLine());

                    switch (obj.getString("result").toLowerCase()) {
                        case "ok":
                            state = IRegisterHandler.State.OK;
                            break;
                        case "email_already_use":
                            state = IRegisterHandler.State.EMAIL_ALREADY_ON_USE;
                            break;
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            callback.onRegisterClient(state, c.getEmail(), password);
        }).start();
    }

    public void connect(String username, String password, IConnectHandler callback, boolean isAutoConnect) {
        new Thread(() -> {
            boolean success = false;
            boolean unknown = false;

            Client retour = null;
            String pass = password;
            if (!isAutoConnect) {
                pass = hash(password);
            }
            try {

                HttpsURLConnection connection = (HttpsURLConnection) new URL(BASE_URL + "connection").openConnection();
                connection.setRequestProperty("username", username);
                connection.setRequestProperty("password", pass);

                connection.setRequestMethod("GET");
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                    StringBuilder sb = new StringBuilder();
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                    }
                    try {
                        System.out.println("JSON : " + sb.toString().trim());
                        JSONObject json = new JSONObject(sb.toString().trim());
                        String error = json.optString("error", null);

                        if (error == null) {
                            token = json.getString("access_token");
                            retour = Client.fromJsonObject(json.getJSONObject("user"));
                            success = true;
                            CONNECTED = true;
                        } else {
                            unknown = error.compareToIgnoreCase("unknown") == 0;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (callback != null) {
                if (success) {
                    callback.onConnectionSuccess(retour, pass, isAutoConnect);
                } else {
                    callback.onConnectionFail(unknown);
                }
            }
        }).start();
    }

    public void disconnect() {
        this.token = "";
        CONNECTED = false;
    }

    public void isLogin(IConnectHandler callback) {
        if (this.token.trim().length() <= 0) {
            callback.askIsLogin(false);
        } else {
            new Thread(() -> {
                boolean reponse = false;
                try {
                    HttpsURLConnection connection = (HttpsURLConnection) new URL(BASE_URL + "isLogin").openConnection();
                    connection.setRequestProperty("authorization", token);
                    connection.setRequestMethod("GET");
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                        String str = in.readLine();
                        System.out.println(str);
                        JSONObject obj = new JSONObject(str);
                        reponse = obj.getBoolean("islog");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                CONNECTED = reponse;
                callback.askIsLogin(reponse);
            }).start();
        }
    }

    private String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(password.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
