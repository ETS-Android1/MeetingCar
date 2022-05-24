package fr.flareden.meetingcar.metier.listener;

import java.util.ArrayList;

import fr.flareden.meetingcar.metier.entity.messagerie.Discussion;
import fr.flareden.meetingcar.metier.entity.messagerie.Message;

public interface IMessageHandler {
    public void onMessagesReceive(Discussion d, ArrayList<Message> messages);
    public void onMessageSend(Discussion d, Message s);
}
