package fr.flareden.meetingcar.ui.mail;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.flareden.meetingcar.metier.CommunicationWebservice;
import fr.flareden.meetingcar.metier.entity.messagerie.Discussion;
import fr.flareden.meetingcar.metier.entity.messagerie.Message;
import fr.flareden.meetingcar.metier.listener.IMessageHandler;
import fr.flareden.meetingcar.ui.home.HomeFragment;
import fr.flareden.meetingcar.ui.home.SpecialAdapter;

public class DiscussionFragment extends HomeFragment {

    protected void queryData(Discussion d) {
        if (d != null) {
            //TODO recupéré la discussion envoyé par le bundle;

            CommunicationWebservice.getINSTANCE().getMessages(d, 0, new IMessageHandler() {
                @Override
                public void onMessagesReceive(Discussion d, ArrayList<Message> messages) {

                }
                @Override
                public void onMessageSend(Discussion d, Message s) {

                }
            });
        }
    }

    @Override
    protected SpecialAdapter generateAdapter(){
        return new SpecialAdapter(data, SpecialAdapter.Type.Message);
    }
    @Override
    protected void touchListener() {
    }
}
