package fr.flareden.meetingcar.metier;


import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Base64;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import fr.flareden.meetingcar.metier.entity.Annonce;
import fr.flareden.meetingcar.metier.entity.Image;
import fr.flareden.meetingcar.metier.entity.client.Client;
import fr.flareden.meetingcar.metier.entity.messagerie.Discussion;
import fr.flareden.meetingcar.metier.entity.messagerie.Message;
import fr.flareden.meetingcar.metier.listener.IAnnonceCreatedHandler;
import fr.flareden.meetingcar.metier.listener.IAnnonceLoaderHandler;
import fr.flareden.meetingcar.metier.listener.IClientLoadingHandler;
import fr.flareden.meetingcar.metier.listener.IConnectHandler;
import fr.flareden.meetingcar.metier.listener.IDiscussionCreatedHandler;
import fr.flareden.meetingcar.metier.listener.IImageReceivingHandler;
import fr.flareden.meetingcar.metier.listener.IListAnnonceLoaderHandler;
import fr.flareden.meetingcar.metier.listener.IMessageHandler;
import fr.flareden.meetingcar.metier.listener.IMessageNotRead;
import fr.flareden.meetingcar.metier.listener.IDiscussionReceiveHandler;
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

    // --- CLIENT ---
    public void inscription(Client c, String password, Uri imageURI, ContentResolver resolver, IRegisterHandler callback) {
        new Thread(() -> {
            IRegisterHandler.State state = IRegisterHandler.State.SERVER_ERROR;
            try {
                int imageID = uploadImage(imageURI, resolver);

                HttpsURLConnection connection = (HttpsURLConnection) new URL(BASE_URL + "inscription").openConnection();
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(2500);
                connection.setRequestMethod("POST");

                JSONObject send = c.toJsonObject();
                send.put("mot_de_passe", hash(password));
                send.put("image_id", imageID);

                try (OutputStream out = connection.getOutputStream()) {
                    out.write(send.toString().getBytes(StandardCharsets.UTF_8));
                    out.flush();
                }

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
                connection.setConnectTimeout(2500);

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
                        JSONObject json = new JSONObject(sb.toString().trim());
                        String error = json.optString("error", null);

                        if (error == null) {
                            token = json.getString("access_token");
                            retour = Client.fromJsonObject(json.getJSONObject("user"));

                            int idImage = json.getJSONObject("user").optInt("photo", -1);
                            //ASK IMAGE
                            if (idImage >= 0) {
                                HttpsURLConnection conn = (HttpsURLConnection) new URL(BASE_URL + "image/" + idImage).openConnection();
                                conn.setConnectTimeout(2500);
                                conn.setRequestMethod("GET");
                                try (InputStream in2 = conn.getInputStream()) {
                                    retour.setImage(new Image(idImage, Drawable.createFromStream(in2, null)));
                                }
                            }

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
                    connection.setConnectTimeout(2500);
                    connection.setRequestProperty("authorization", token);
                    connection.setRequestMethod("GET");
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                        String str = in.readLine();
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

    public void updateClient(Client c, Uri imageURI, ContentResolver resolver) {
        new Thread(() -> {
            IRegisterHandler.State state = IRegisterHandler.State.SERVER_ERROR;
            try {
                int imageID = -2;
                if (c.getImage() != null) {
                    imageID = c.getImage().getId();
                }
                if (imageURI != null) {
                    imageID = uploadImage(imageURI, resolver);
                }
                HttpsURLConnection connection = (HttpsURLConnection) new URL(BASE_URL + "update/client").openConnection();
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(2500);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("authorization", token);

                JSONObject send = c.toJsonObject();
                send.put("image_id", imageID);

                try (OutputStream out = connection.getOutputStream()) {
                    out.write(send.toString().getBytes(StandardCharsets.UTF_8));
                    out.flush();
                }

                try (BufferedReader in = new BufferedReader(new InputStreamReader(new BufferedInputStream(connection.getInputStream())))) {
                    System.out.println(in.readLine());
                }

                if (imageID >= 0) {
                    HttpsURLConnection conn = (HttpsURLConnection) new URL(BASE_URL + "image/" + imageID).openConnection();
                    conn.setConnectTimeout(2500);
                    try (InputStream in = conn.getInputStream()) {

                        c.setImage(new Image(imageID, Drawable.createFromStream(in, null)));
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Metier.getINSTANCE().setUtilisateur(c);
        }).start();
    }

    public void getClient(int id, IClientLoadingHandler callback) {
        new Thread(() -> {
            Client retour = null;
            try {
                HttpsURLConnection connection = (HttpsURLConnection) new URL(BASE_URL + "client/" + id).openConnection();
                connection.setConnectTimeout(2500);
                connection.setRequestMethod("GET");

                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                    StringBuilder sb = new StringBuilder();
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                    }
                    JSONObject json = new JSONObject(sb.toString().trim());
                    String error = json.optString("error", null);
                    if (error == null) {
                        retour = Client.fromJsonObject(json);
                        int idImage = json.getInt("photo");
                        //ASK IMAGE
                        if (idImage >= 0) {
                            HttpsURLConnection conn = (HttpsURLConnection) new URL(BASE_URL + "image/" + idImage).openConnection();
                            conn.setConnectTimeout(2500);
                            conn.setRequestMethod("GET");
                            try (InputStream in2 = conn.getInputStream()) {
                                retour.setImage(new Image(idImage, Drawable.createFromStream(in2, null)));
                            }
                        }
                    }
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            callback.onClientLoad(retour, false);
        });
    }

    // --- FIN CLIENT ---

    // --- IMAGES ---

    public void getImage(int id, IImageReceivingHandler callback) {
        if (callback != null && id >= 0) {
            new Thread(() -> {
                Image image = null;
                try {
                    HttpsURLConnection connection = (HttpsURLConnection) new URL(BASE_URL + "image/" + id).openConnection();
                    connection.setConnectTimeout(2500);
                    connection.setRequestMethod("GET");
                    try (InputStream in = connection.getInputStream()) {
                        image = new Image(id, Drawable.createFromStream(in, null));
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                callback.receiveImage(image);
            }).start();
        }
    }

    private int uploadImage(Uri imageURI, ContentResolver resolver) throws IOException, JSONException {
        int imageID = -1;

        if (imageURI != null) {

            Cursor cursor = resolver.query(imageURI, null, null, null, null);
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            cursor.moveToFirst();
            String name = cursor.getString(nameIndex);
            int size = cursor.getInt(sizeIndex);
            byte[] imageData = new byte[size];
            String extension = name.substring(name.lastIndexOf(".") + 1);
            String imageType = resolver.getType(imageURI);
            try (InputStream in = resolver.openInputStream(imageURI)) {
                in.read(imageData);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (imageType.trim().length() > 0 && imageData.length > 0) {
                HttpsURLConnection conn = (HttpsURLConnection) new URL(BASE_URL + "sendimage").openConnection();
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setConnectTimeout(2500);
                conn.setRequestMethod("POST");
                try (OutputStream out = conn.getOutputStream()) {
                    JSONObject jo = new JSONObject();
                    jo.put("extension", extension);
                    jo.put("data", Base64.encodeToString(imageData, Base64.NO_WRAP));
                    out.write(jo.toString().getBytes(StandardCharsets.UTF_8));
                    out.flush();
                }

                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                    JSONObject obj = new JSONObject(in.readLine());
                    imageID = obj.getInt("id");
                }
            }
        }
        return imageID;
    }

    // --- FIN IMAGE ---

    // --- ARTICLES ---

    public void createAnnonce(Annonce a, ArrayList<Uri> imagesURI, ContentResolver resolver, IAnnonceCreatedHandler callback) {
        new Thread(() -> {
            int id = -1;
            try {
                JSONArray array = new JSONArray();
                for (int i = 0, max = imagesURI.size(); i < max; i++) {
                    array.put(uploadImage(imagesURI.get(i), resolver));
                }

                HttpsURLConnection connection = (HttpsURLConnection) new URL(BASE_URL + "annonce/create").openConnection();
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(2500);
                connection.setRequestMethod("POST");

                connection.setRequestProperty("authorization", token);

                JSONObject send = a.toJsonObject();

                send.put("images_ids", (Object) array);

                try (OutputStream out = connection.getOutputStream()) {
                    out.write(send.toString().getBytes(StandardCharsets.UTF_8));
                    out.flush();
                }

                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null){
                        sb.append(line);
                    }
                    JSONObject obj = new JSONObject(sb.toString().trim());

                    id = obj.optInt("id", -1 );
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(callback != null){
                callback.onAnnonceCreated(id);
            }

        }).start();
    }

    public void updateAnnonce(Annonce a, ArrayList<Uri> imagesURI, ContentResolver resolver) {
        new Thread(() -> {
            try {
                /*JSONArray array = new JSONArray();
                if(imagesURI != null){
                    for (int i = 0, max = imagesURI.size(); i < max; i++) {
                        array.put(uploadImage(imagesURI.get(i), resolver));
                    }
                    ArrayList<Image> photos = (ArrayList<Image>) a.getPhotos().clone();
                    for (Image image : photos) {
                        if (image.isToDelete()) {
                            new Thread(() -> {
                                try {
                                    HttpsURLConnection connection = (HttpsURLConnection) new URL(BASE_URL + "removeimage/" + image.getId()).openConnection();
                                    connection.setConnectTimeout(2500);
                                    connection.setRequestMethod("GET");
                                    connection.setRequestProperty("authorization", token);
                                    try (BufferedReader in = new BufferedReader(new InputStreamReader(new BufferedInputStream(connection.getInputStream())))) {
                                        in.readLine();
                                    }
                                } catch (ProtocolException e) {
                                    e.printStackTrace();
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }).start();
                            a.getPhotos().remove(image);
                        }
                    }
                }*/

                System.out.println("UPDATE");

                HttpsURLConnection connection = (HttpsURLConnection) new URL(BASE_URL + "annonce/update").openConnection();
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(2500);
                connection.setRequestMethod("POST");

                connection.setRequestProperty("authorization", token);

                JSONObject send = a.toJsonObject();
                //send.put("images_ids", (Object) array);

                try (OutputStream out = connection.getOutputStream()) {
                    out.write(send.toString().getBytes(StandardCharsets.UTF_8));
                    out.flush();
                }
                System.out.println("WI");

                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null){
                        sb.append(line);
                    }
                    System.out.println(sb.toString());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void getAnnonce(int idAnnonce, @NonNull IAnnonceLoaderHandler callback) {
        if (idAnnonce >= 0) {
            new Thread(() -> {
                Annonce retour = null;
                try {
                    HttpsURLConnection connection = (HttpsURLConnection) new URL(BASE_URL + "annonce/get/" + idAnnonce).openConnection();
                    connection.setConnectTimeout(2500);
                    connection.setRequestMethod("GET");
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                        StringBuilder sb = new StringBuilder();
                        String line = "";
                        while ((line = in.readLine()) != null) {
                            sb.append(line);
                        }
                        JSONObject json = new JSONObject(sb.toString().trim());
                        retour = Annonce.fromJsonObject(json.optJSONObject("result"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                callback.onAnnonceLoad(retour);
            }).start();
        }
    }

    public void getAnnoncesVendeur(int idClient, int page, @NonNull IListAnnonceLoaderHandler callback) {
        if (page >= 0) {

            new Thread(() -> {
                ArrayList<Annonce> liste = new ArrayList<>();
                try {
                    HttpsURLConnection connection = (HttpsURLConnection) new URL(BASE_URL + "annonce/user/" + idClient + "/" + page).openConnection();
                    connection.setConnectTimeout(2500);
                    connection.setRequestMethod("GET");
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                        StringBuilder sb = new StringBuilder();
                        String line = "";
                        while ((line = in.readLine()) != null) {
                            sb.append(line);
                        }
                        JSONObject json = new JSONObject(sb.toString().trim());
                        JSONArray array = json.optJSONArray("result");
                        if (array != null) {
                            for (int i = 0, max = array.length(); i < max; i++) {
                                liste.add(Annonce.fromJsonObject(array.getJSONObject(i)));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                callback.onListAnnonceLoad(liste);
            }).start();
        }
    }

    public void getAnnoncesAcheteur(int idClient, int page, @NonNull IListAnnonceLoaderHandler callback) {
        if (page >= 0) {

            new Thread(() -> {
                ArrayList<Annonce> liste = new ArrayList<>();
                try {
                    HttpsURLConnection connection = (HttpsURLConnection) new URL(BASE_URL + "annonce/purchased/" + idClient + "/" + page).openConnection();
                    connection.setConnectTimeout(2500);
                    connection.setRequestMethod("GET");
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                        StringBuilder sb = new StringBuilder();
                        String line = "";
                        while ((line = in.readLine()) != null) {
                            sb.append(line);
                        }
                        JSONObject json = new JSONObject(sb.toString().trim());
                        JSONArray array = json.optJSONArray("result");
                        if (array != null) {
                            for (int i = 0, max = array.length(); i < max; i++) {
                                liste.add(Annonce.fromJsonObject(array.getJSONObject(i)));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                callback.onListAnnonceLoad(liste);
            }).start();
        }
    }

    public void loadImagesAnnonce(@NonNull Annonce a, IAnnonceLoaderHandler callback) {
        for (Image image : a.getPhotos()) {
            if (image.getDrawable() == null) {
                new Thread(() -> {
                    try {
                        HttpsURLConnection connection = (HttpsURLConnection) new URL(BASE_URL + "image/" + image.getId()).openConnection();
                        connection.setConnectTimeout(2500);
                        connection.setRequestMethod("GET");
                        try (InputStream in = connection.getInputStream()) {
                            image.setDrawable(Drawable.createFromStream(in, null));
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    callback.onImageAnnonceLoad(a, image);
                }).start();
            }
        }
    }

    public void getAnnonceListe(int page, IListAnnonceLoaderHandler callback) {
        if(page >= 0) {
            new Thread(() -> {
                ArrayList<Annonce> liste = new ArrayList<>();
                try {
                    HttpsURLConnection connection = (HttpsURLConnection) new URL(BASE_URL + "annonce/page/" + page).openConnection();
                    connection.setConnectTimeout(2500);
                    connection.setRequestMethod("GET");
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                        StringBuilder sb = new StringBuilder();
                        String line = "";
                        while ((line = in.readLine()) != null) {
                            sb.append(line);
                        }
                        JSONObject json = new JSONObject(sb.toString().trim());
                        JSONArray array = json.optJSONArray("result");
                        if (array != null) {
                            for (int i = 0, max = array.length(); i < max; i++) {
                                liste.add(Annonce.fromJsonObject(array.getJSONObject(i)));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                callback.onListAnnonceLoad(liste);
            }).start();
        }
    }

    public void addVisite(@NonNull Annonce a, Client c) {
        new Thread(() -> {

        }).start();
    }

    // --- FIN ARTICLES ---

    // --- MESSAGERIE ---

    public void getDiscussions(int page, IDiscussionReceiveHandler callback){
        new Thread(() -> {
            int id = -1;
            ArrayList<Discussion> liste = new ArrayList<>();
            try {
                HttpsURLConnection connection = (HttpsURLConnection) new URL(BASE_URL + "discussion/all/" + page).openConnection();
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(2500);
                connection.setRequestMethod("GET");

                connection.setRequestProperty("authorization", token);

                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null){
                        sb.append(line);
                    }
                    JSONObject json = new JSONObject(sb.toString().trim());
                    JSONArray array = json.optJSONArray("result");
                    if (array != null) {
                        for (int i = 0, max = array.length(); i < max; i++) {
                            liste.add(Discussion.fromJsonObject(array.getJSONObject(i)));
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(callback != null){
                callback.onDiscussionReceive(liste);
            }

        }).start();
    }

    public void getMessages(Discussion d, int page, IMessageHandler callback){
        new Thread(() -> {
            int id = -1;
            ArrayList<Message> liste = new ArrayList<>();
            try {
                HttpsURLConnection connection = (HttpsURLConnection) new URL(BASE_URL + "discussion/one/" + d.getId() + "/messages/" + page).openConnection();
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(2500);
                connection.setRequestMethod("GET");

                connection.setRequestProperty("authorization", token);

                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null){
                        sb.append(line);
                    }
                    JSONObject json = new JSONObject(sb.toString().trim());
                    JSONArray array = json.optJSONArray("result");
                    if (array != null) {
                        for (int i = 0, max = array.length(); i < max; i++) {
                            liste.add(Message.fromJsonObject(array.getJSONObject(i), d));
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(callback != null){
                callback.onMessagesReceive(d, liste);
            }

        }).start();
    }

    public void sendMessage(Discussion d, Message message, Uri image, IMessageHandler callback, ContentResolver resolver){
        new Thread(() -> {
            int id = -1;
            int image_id = -1;
            try {
                if(image != null){
                    image_id =  uploadImage(image, resolver);
                }

                HttpsURLConnection connection = (HttpsURLConnection) new URL(BASE_URL + "discussion/one/" + d.getId() + "/sendMessage").openConnection();
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(2500);
                connection.setRequestMethod("POST");

                connection.setRequestProperty("authorization", token);

                JSONObject send = message.toJsonObject();

                send.put("image_id", image_id);

                try (OutputStream out = connection.getOutputStream()) {
                    out.write(send.toString().getBytes(StandardCharsets.UTF_8));
                    out.flush();
                }

                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null){
                        sb.append(line);
                    }
                    JSONObject obj = new JSONObject(sb.toString().trim());

                    id = obj.optInt("result", -1);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if(id != -1){
                message.setId(id);
                if(message.getImage() != null){
                    message.getImage().setId(image_id);
                }
                if(callback != null){
                    callback.onMessageSend(d, message);
                }
            }
        }).start();
    }

    void isMessageNotRead(String horodatage, IMessageNotRead callback){
        new Thread(() -> {
            int nb = 0;
            try {
                HttpsURLConnection connection = (HttpsURLConnection) new URL(BASE_URL + "/").openConnection();
                connection.setConnectTimeout(2500);
                connection.setRequestProperty("authorization", token);
                connection.setRequestMethod("GET");
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                    String str = in.readLine();
                    JSONObject obj = new JSONObject(str);
                    nb = obj.getInt("nb_not_read");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            callback.numberMessageNotRead(nb);
        }).start();
    }

    void createDiscussion(Discussion d, IDiscussionCreatedHandler callback){
        new Thread(() -> {
            try {
                HttpsURLConnection connection = (HttpsURLConnection) new URL(BASE_URL + "discussion/create").openConnection();
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(2500);
                connection.setRequestMethod("POST");

                connection.setRequestProperty("authorization", token);

                JSONObject send = d.toJsonObject();

                try (OutputStream out = connection.getOutputStream()) {
                    out.write(send.toString().getBytes(StandardCharsets.UTF_8));
                    out.flush();
                }

                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null){
                        sb.append(line);
                    }
                    JSONObject obj = new JSONObject(sb.toString().trim());

                    d.setId(obj.optInt("id", -1 ));
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(callback != null){
                callback.onDiscussionCreated(d);
            }

        }).start();
    }

    // --- FIN MESSAGERIE ---

    // --- CRYPTAGE ---

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

    // --- FIN CRYPTAGE ---
}
