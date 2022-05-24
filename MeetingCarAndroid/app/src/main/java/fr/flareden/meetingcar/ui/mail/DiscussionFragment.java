package fr.flareden.meetingcar.ui.mail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

public class DiscussionFragment extends Fragment implements IMessageHandler{

    protected FragmentDiscussionBinding binding;

    // RECYCLER VIEW
    protected RecyclerView recycler;
    protected SpecialAdapter adapter;
    protected Discussion discussion;

    protected ArrayList<IViewModel> data = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDiscussionBinding.inflate(inflater, container, false);

        data.clear();

        discussion = (Discussion) getArguments().getSerializable("discussion");
        if (discussion != null) {
            queryData(discussion);
        }

        // RECYCLER VIEW INIT
        recycler = binding.msgRvListmsg;
        adapter = new SpecialAdapter(data, SpecialAdapter.Type.Message, getContext());
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

        IMessageHandler callback = this;
        binding.msgButtonSend.setOnClickListener(view -> {
            String msg = binding.msgEditMessage.getText().toString();
            if(msg.trim().length() > 0){
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                Message m = new Message(msg, Metier.getINSTANCE().getUtilisateur(), format.format(date).toString(), discussion);
                CommunicationWebservice.getINSTANCE().sendMessage(discussion, m, null, callback, getContext().getContentResolver());
                binding.msgEditMessage.setText("");
            }
        });
        return binding.getRoot();
    }

    protected void queryData(Discussion d) {
        if (d != null) {
            CommunicationWebservice.getINSTANCE().getMessages(d, 0, this);
        }
    }

    @Override
    public void onMessagesReceive(Discussion d, ArrayList<Message> messages) {
        for (Message m : messages) {
            MessageViewModel mvm = new MessageViewModel(m);
            data.add(mvm);

        }
        getActivity().runOnUiThread(() -> {
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onMessageSend(Discussion d, Message s) {
        System.out.println("SENDED");
        System.out.println(s.getContenu());
        data.add(new MessageViewModel(s));
        getActivity().runOnUiThread(() -> {
            adapter.notifyDataSetChanged();
        });
    }
}
