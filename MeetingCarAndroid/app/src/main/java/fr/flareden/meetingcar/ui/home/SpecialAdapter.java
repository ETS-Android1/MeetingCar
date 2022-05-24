package fr.flareden.meetingcar.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.flareden.meetingcar.R;
import fr.flareden.meetingcar.ui.mail.MailViewModel;
import fr.flareden.meetingcar.ui.mail.MessageViewModel;

public class SpecialAdapter extends RecyclerView.Adapter<SpecialAdapter.ViewHolder> implements Filterable {

    private ArrayList<IViewModel> data;
    private ArrayList<IViewModel> fullData;
    private Filter filter;
    private Type type;
    private Context context;

    public SpecialAdapter(ArrayList<IViewModel> d, Type type, Context context) {
        this.context = context;
        this.type = type;
        this.data = d;
        this.fullData = new ArrayList<>(d);
        this.filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                ArrayList<IViewModel> filteredList = new ArrayList<>();
                if (charSequence == null || charSequence.length() == 0) {
                    filteredList.addAll(fullData);
                } else {
                    String filterPattern = charSequence.toString().toLowerCase().trim();
                    for (IViewModel item : fullData) {
                        if (item.getSearchString().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                data.clear();
                data.addAll((ArrayList) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public SpecialAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder v = null;

        switch (this.type) {
            case Advert:
                v = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_recyclerview, parent, false));
                break;

            case Discussion:
                v = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.disc_recyclerview, parent, false));
                break;

            case Message:
                v = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_recyclerview, parent, false));
                break;
        }
        return v;
    }

    @Override
    public void onBindViewHolder(SpecialAdapter.ViewHolder holder, int position) {
        if (type == Type.Advert) {
            AdvertViewModel avm = (AdvertViewModel) data.get(position);
            holder.tv_title.setText(avm.getTitle());
            holder.tv_desc.setText(avm.getDesc());
            holder.tv_loc.setText(avm.getLoc());
            holder.tv_price.setText(avm.getPrice());
            holder.tv_type.setText((avm.getType() == AdvertViewModel.TYPE.RENT ? context.getResources().getString(R.string.rent) : context.getResources().getString(R.string.sell)));
        } else if (type == Type.Discussion) {
            MailViewModel mvm = (MailViewModel) data.get(position);
            holder.tv_title.setText(mvm.getTitle());
            holder.tv_name_contact.setText(mvm.getContactName());
        } else if (type == Type.Message) {
            MessageViewModel mvm = (MessageViewModel) data.get(position);
            holder.tv_author.setText(mvm.getAuthor());
            holder.tv_message.setText(mvm.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    @Override
    public Filter getFilter() {
        return this.filter;
    }

    public enum Type {
        Advert,
        Discussion,
        Message
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //Advert & Discussion
        TextView tv_title;
        //Advert
        TextView tv_desc, tv_loc, tv_price, tv_type;
        //Discussion
        TextView tv_name_contact;
        //Message
        TextView tv_author, tv_message;

        public ViewHolder(View itemView) {
            super(itemView);
            if (type == Type.Advert) {
                tv_title = itemView.findViewById(R.id.rv_tv_title);
                tv_desc = itemView.findViewById(R.id.rv_tv_desc);
                tv_loc = itemView.findViewById(R.id.rv_tv_loc);
                tv_price = itemView.findViewById(R.id.rv_tv_price);
                tv_type = itemView.findViewById(R.id.rv_tv_type);
            } else if (type == Type.Discussion) {
                tv_title = itemView.findViewById(R.id.disc_tv_announcetitle);
                tv_name_contact = itemView.findViewById(R.id.disc_tv_corresp);
            } else if (type == Type.Message) {
                tv_author = itemView.findViewById(R.id.msg_tv_author);
                tv_message = itemView.findViewById(R.id.msg_tv_msg);
            }

        }
    }
}
