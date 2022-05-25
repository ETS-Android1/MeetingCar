package fr.flareden.meetingcar.ui.annonce;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import fr.flareden.meetingcar.R;
import fr.flareden.meetingcar.databinding.FragmentAnnonceBinding;
import fr.flareden.meetingcar.metier.CommunicationWebservice;
import fr.flareden.meetingcar.metier.Metier;
import fr.flareden.meetingcar.metier.entity.Annonce;
import fr.flareden.meetingcar.metier.entity.Image;
import fr.flareden.meetingcar.metier.entity.messagerie.Discussion;
import fr.flareden.meetingcar.metier.entity.messagerie.Message;
import fr.flareden.meetingcar.metier.listener.IAnnonceLoaderHandler;
import fr.flareden.meetingcar.metier.listener.IImageReceivingHandler;
import fr.flareden.meetingcar.metier.listener.IMessageHandler;

public class AnnonceFragment extends Fragment implements IImageReceivingHandler {

    private FragmentAnnonceBinding binding;
    private Annonce annonce = null;

    private ViewPagerAdapter adapter;

    private boolean following = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAnnonceBinding.inflate(inflater, container, false);
        Bundle args = this.getArguments();

        // FAB BINDING
        binding.announceFabEdit.setOnClickListener((View view) -> editAnnounce());
        binding.announceFabCheck.setOnClickListener((View view) -> checkEditAnnounce());
        binding.announceFabCancel.setOnClickListener((View view) -> cancelEditAnnounce());

        binding.clAnnonce.setVisibility(View.GONE);

        AnnonceFragment self = this;
        if (args == null) {
            NavController navController = NavHostFragment.findNavController(this);
            navController.popBackStack();
            navController.navigate(R.id.nav_home);
        } else {
            int idAnnonce = args.getInt("idAnnonce", -1);
            if (idAnnonce < 0) {
                NavController navController = NavHostFragment.findNavController(this);
                navController.popBackStack();
                navController.navigate(R.id.nav_home);
            } else {
                IImageReceivingHandler callback = this;
                CommunicationWebservice.getINSTANCE().getAnnonce(idAnnonce, new IAnnonceLoaderHandler() {
                    @Override
                    public void onAnnonceLoad(Annonce a) {
                        if (a != null) {
                            annonce = a;
                            CommunicationWebservice.getINSTANCE().isFollowing(a.getId(), follow -> {
                                following = follow;
                                if(follow) {
                                    getActivity().runOnUiThread(() -> {
                                        binding.butAnnonceFollow.setText(R.string.unfollow);
                                    });
                                }
                            });
                            getActivity().runOnUiThread(() -> {
                                binding.tvAnnonceTitre.setText(a.getTitle());
                                binding.tvAnnonceLoc.setText(a.getVendeur().getAdresse());
                                binding.tvAnnoncePrice.setText("" + a.getPrix());
                                binding.tvAnnonceType.setText((a.isLocation() ? getResources().getString(R.string.rent) : getResources().getString(R.string.sell)));
                                binding.tvAnnonceDesc.setText((a.getDesc()));
                                for (Image img : a.getPhotos()) {
                                    if (img.getDrawable() == null) {
                                        CommunicationWebservice.getINSTANCE().getImage(img.getId(), callback);
                                    }
                                }
                                adapter = new ViewPagerAdapter(getContext(), new ArrayList<>());
                                binding.vpAnnonceImages.setAdapter(adapter);
                                if(Metier.getINSTANCE().getUtilisateur() != null){
                                    if (Metier.getINSTANCE().getUtilisateur().getId() == annonce.getVendeur().getId()) {
                                        binding.announceFabEdit.setVisibility(View.VISIBLE);
                                        binding.butAnnonceFollow.setVisibility(View.GONE);
                                        binding.butAnnonceContactSeller.setVisibility(View.GONE);
                                        binding.butAnnonceBuy.setVisibility(View.GONE);
                                    } else {
                                        CommunicationWebservice.getINSTANCE().addVisite(annonce, Metier.getINSTANCE().getUtilisateur());
                                    }
                                } else {
                                    CommunicationWebservice.getINSTANCE().addVisite(annonce, null);
                                    binding.butAnnonceFollow.setVisibility(View.GONE);
                                    binding.butAnnonceBuy.setVisibility(View.GONE);
                                }
                                if(annonce.getAcheteur() != null){
                                    binding.butAnnonceBuy.setVisibility(View.GONE);
                                }
                                binding.clAnnonce.setVisibility(View.VISIBLE);
                            });
                        }
                    }

                    @Override
                    public void onImageAnnonceLoad(Annonce a, Image i) {
                    }
                });

                binding.butAnnonceBuy.setOnClickListener(view -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(R.string.areyousure).setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                        CommunicationWebservice.getINSTANCE().buy(annonce.getId());
                        //TODO partir de la page ?
                    }).setNegativeButton(R.string.no, null).show();
                });

                binding.butAnnonceContactSeller.setOnClickListener(view -> {

                    if(Metier.getINSTANCE().getUtilisateur() != null){
                        CommunicationWebservice.getINSTANCE().discussionExist(annonce.getId(), idDiscussion -> {
                            if(idDiscussion >= 0){
                                CommunicationWebservice.getINSTANCE().getDiscussion(idDiscussion, discussions -> {
                                    if(discussions.size() > 0){
                                        Bundle b = new Bundle();
                                        b.putSerializable("discussion", discussions.get(0));

                                        NavController navController = NavHostFragment.findNavController(self);
                                        navController.popBackStack();

                                        navController.navigate(R.id.nav_discussion, b);
                                    }
                                });
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                EditText message = new EditText(getContext());
                                message.setHint(R.string.message);
                                getActivity().runOnUiThread(() -> {
                                    builder.setView(message).setPositiveButton(R.string.send, (dialogInterface, i) -> {
                                        String msg = message.getText().toString();
                                        if (msg.trim().length() > 0) {
                                            CommunicationWebservice.getINSTANCE().createDiscussion(new Discussion(-1, Metier.getINSTANCE().getUtilisateur(), annonce.getVendeur(), annonce, new ArrayList<>()), d -> {
                                                Date date = new Date(System.currentTimeMillis());
                                                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                                                CommunicationWebservice.getINSTANCE().sendMessage(d, new Message(msg, null, format.format(date), d), null, new IMessageHandler() {
                                                    @Override
                                                    public void onMessagesReceive(Discussion d, ArrayList<Message> messages) {
                                                    }

                                                    @Override
                                                    public void onMessageSend(Discussion d, Message s) {
                                                        getActivity().runOnUiThread(() -> {
                                                            Toast.makeText(getContext(), R.string.message_sent, Toast.LENGTH_SHORT).show();
                                                        });
                                                    }
                                                }, null);
                                            });
                                        }
                                    }).setNegativeButton(R.string.cancel, null).show();
                                });
                            }
                        });

                    } else {
                        //Not connected
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        LinearLayout layout = new LinearLayout(getContext());
                        layout.setOrientation(LinearLayout.VERTICAL);
                        EditText email = new EditText(getContext());
                        email.setHint(R.string.prompt_email);
                        layout.addView(email);
                        EditText message = new EditText(getContext());
                        message.setHint(R.string.message);
                        layout.addView(message);
                        builder.setView(layout).setPositiveButton(R.string.send, (dialogInterface, i) -> {
                            String mail = email.getText().toString();
                            String msg = message.getText().toString();
                            if(mail.trim().length() > 0 && msg.trim().length() > 0) {
                                CommunicationWebservice.getINSTANCE().createDiscussion(new Discussion(-1, mail, annonce.getVendeur(), annonce, new ArrayList<>()), d -> {
                                    Date date = new Date(System.currentTimeMillis());
                                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                                    CommunicationWebservice.getINSTANCE().sendMessage(d, new Message(msg, null, format.format(date), d), null, new IMessageHandler() {
                                        @Override
                                        public void onMessagesReceive(Discussion d, ArrayList<Message> messages) {
                                        }

                                        @Override
                                        public void onMessageSend(Discussion d, Message s) {
                                            getActivity().runOnUiThread(() -> {
                                                Toast.makeText(getContext(), R.string.message_sent, Toast.LENGTH_SHORT).show();
                                            });
                                        }
                                    }, null);
                                });
                            }
                        }).setNegativeButton(R.string.cancel, null).show();
                    }
                });

                binding.butAnnonceFollow.setOnClickListener(view -> {
                    CommunicationWebservice.getINSTANCE().setFollow(annonce.getId(), !following);
                    following = !following;
                    getActivity().runOnUiThread(() -> {
                        binding.butAnnonceFollow.setText((following ? R.string.follow: R.string.unfollow));
                    });
                });
            }
        }

        return binding.getRoot();
    }


    public void editAnnounce() {
        if (Metier.getINSTANCE().getUtilisateur().getId() == annonce.getVendeur().getId()) {
            // GONE
            binding.tvAnnonceTitre.setVisibility(View.GONE);
            binding.tvAnnoncePrice.setVisibility(View.GONE);
            binding.tvAnnonceType.setVisibility(View.GONE);
            binding.tvAnnonceDesc.setVisibility(View.GONE);
            binding.announceFabEdit.setVisibility(View.GONE);

            // VISIBLE
            binding.announceEditTitle.setVisibility(View.VISIBLE);
            binding.announceEditPrice.setVisibility(View.VISIBLE);
            binding.announceEditType.setVisibility(View.VISIBLE);
            binding.announceEditDesc.setVisibility(View.VISIBLE);
            binding.announceFabCheck.setVisibility(View.VISIBLE);
            binding.announceFabCancel.setVisibility(View.VISIBLE);

            // HINTS
            binding.announceEditTitle.setText(this.annonce.getTitle());
            binding.announceEditPrice.setText(this.annonce.getPrix() + "");

            String[] types = {getResources().getString(R.string.sell), getResources().getString(R.string.rent)};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, types);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.announceEditType.setAdapter(adapter);
            binding.announceEditType.setSelection(this.annonce.isLocation() ? 1 : 0);

            binding.announceEditDesc.setText(this.annonce.getDesc());

        }
    }

    public void cancelEditAnnounce() {
        // GONE
        binding.announceEditTitle.setVisibility(View.GONE);
        binding.announceEditPrice.setVisibility(View.GONE);
        binding.announceEditType.setVisibility(View.GONE);
        binding.announceEditDesc.setVisibility(View.GONE);
        binding.announceFabCheck.setVisibility(View.GONE);
        binding.announceFabCancel.setVisibility(View.GONE);

        // VISIBLE
        binding.tvAnnonceTitre.setVisibility(View.VISIBLE);
        binding.tvAnnoncePrice.setVisibility(View.VISIBLE);
        binding.tvAnnonceType.setVisibility(View.VISIBLE);
        binding.tvAnnonceDesc.setVisibility(View.VISIBLE);
        binding.announceFabEdit.setVisibility(View.VISIBLE);
    }

    public void checkEditAnnounce() {
        if (Metier.getINSTANCE().getUtilisateur().getId() == annonce.getVendeur().getId()) {
            // GET STRING
            String title = binding.announceEditTitle.getText().toString().trim();
            String price = binding.announceEditPrice.getText().toString().trim();
            Boolean type = binding.announceEditType.getSelectedItemPosition() == 1;
            String desc = binding.announceEditDesc.getText().toString().trim();

            // UPDATE USER
            if (title.length() > 0) {
                annonce.setTitle(title);
            }
            if (price.length() > 0) {
                try {
                    float priceF = Float.parseFloat(price);
                    annonce.setPrix(priceF);
                } catch (NumberFormatException e) {
                }
            }
            annonce.setLocation(type);
            if (desc.length() > 0) {
                annonce.setDesc(desc);
            }

            cancelEditAnnounce();
            CommunicationWebservice.getINSTANCE().updateAnnonce(annonce, null, null);
            binding.tvAnnonceTitre.setText(annonce.getTitle());
            binding.tvAnnoncePrice.setText("" + annonce.getPrix());
            binding.tvAnnonceType.setText((annonce.isLocation() ? getResources().getString(R.string.rent) : getResources().getString(R.string.sell)));
            binding.tvAnnonceDesc.setText((annonce.getDesc()));
        }
    }

    @Override
    public void receiveImage(Image d) {
        if (d.getDrawable() != null) {
            getActivity().runOnUiThread(() -> {
                this.adapter.addImage(d);
                this.adapter.notifyDataSetChanged();
            });
        }
    }
}
