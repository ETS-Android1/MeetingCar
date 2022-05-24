package fr.flareden.meetingcar.ui.mail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.flareden.meetingcar.databinding.FragmentDiscussionBinding;
import fr.flareden.meetingcar.metier.CommunicationWebservice;
import fr.flareden.meetingcar.metier.Metier;
import fr.flareden.meetingcar.metier.entity.client.Client;
import fr.flareden.meetingcar.metier.entity.messagerie.Discussion;
import fr.flareden.meetingcar.metier.entity.messagerie.Message;
import fr.flareden.meetingcar.metier.listener.IConnectHandler;
import fr.flareden.meetingcar.metier.listener.IMessageHandler;
import fr.flareden.meetingcar.ui.home.IViewModel;
import fr.flareden.meetingcar.ui.home.SpecialAdapter;

public class DiscussionFragment extends Fragment {

    protected FragmentDiscussionBinding binding;

    // RECYCLER VIEW
    protected RecyclerView recycler;
    protected SpecialAdapter adapter;

    protected ArrayList<IViewModel> data = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDiscussionBinding.inflate(inflater, container, false);

        data.clear();

        Discussion disc = (Discussion) getArguments().getSerializable("discussion");
        if (disc != null) {
            queryData(disc);
        }

        // RECYCLER VIEW INIT
        recycler = binding.msgRvListmsg;
        adapter = new SpecialAdapter(data, SpecialAdapter.Type.Message);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false));

        Metier.getINSTANCE().isLogin(new IConnectHandler() {
            @Override
            public void onConnectionSuccess(Client c, String hashedPassword, boolean isAutoConnect) {

            }

            @Override
            public void onConnectionFail(boolean unknown) {

            }

            @Override
            public void askIsLogin(boolean isLogin) {

            }
        });

        return binding.getRoot();
    }

    protected void queryData(Discussion d) {
        if (d != null) {
            CommunicationWebservice.getINSTANCE().getMessages(d, 0, new IMessageHandler() {
                @Override
                public void onMessagesReceive(Discussion d, ArrayList<Message> messages) {
                    for (Message m : messages) {
                        data.add(new MessageViewModel(m));
                    }
                }

                @Override
                public void onMessageSend(Discussion d, Message s) {
                    data.add(new MessageViewModel(s));
                }
            });
        }
    }
}
