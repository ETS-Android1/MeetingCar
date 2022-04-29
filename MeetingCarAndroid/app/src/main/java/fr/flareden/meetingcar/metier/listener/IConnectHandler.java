package fr.flareden.meetingcar.metier.listener;

import fr.flareden.meetingcar.metier.entity.client.Client;

public interface IConnectHandler {
    public void onConnectionSuccess(Client c, String hashedPassword, boolean isAutoConnect);
    public void onConnectionFail(boolean unknown);
    public void askIsLogin(boolean isLogin);
}
