package fr.flareden.meetingcar.metier.listener;

import java.util.ArrayList;

import fr.flareden.meetingcar.metier.entity.messagerie.Discussion;
import fr.flareden.meetingcar.metier.entity.messagerie.Message;

public interface IDiscussionReceiveHandler {
    public void onDiscussionReceive(ArrayList<Discussion> discussions);
}
