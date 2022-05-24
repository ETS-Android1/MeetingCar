package fr.flareden.meetingcar.metier.listener;

import fr.flareden.meetingcar.metier.entity.messagerie.Discussion;

public interface IDiscussionCreatedHandler {
    public void onDiscussionCreated(Discussion d);
}
