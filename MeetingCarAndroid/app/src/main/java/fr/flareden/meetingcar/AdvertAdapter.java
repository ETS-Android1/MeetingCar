package fr.flareden.meetingcar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdvertAdapter extends RecyclerView.Adapter<AdvertAdapter.ViewHolder> implements Filterable {

    private ArrayList<AdvertViewModel> data;
    private ArrayList<AdvertViewModel> fullData;
    private Filter filter;

    AdvertAdapter(ArrayList<AdvertViewModel> d) {
        this.data = d;
        this.fullData = new ArrayList<>(d);
        this.filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                ArrayList<AdvertViewModel> filteredList = new ArrayList<>();
                if (charSequence == null || charSequence.length() == 0) {
                    filteredList.addAll(fullData);
                } else {
                    String filterPattern = charSequence.toString().toLowerCase().trim();
                    for (AdvertViewModel item : fullData) {
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_desc, tv_loc, tv_price, tv_type;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.rv_tv_title);
            tv_desc = itemView.findViewById(R.id.rv_tv_desc);
            tv_loc = itemView.findViewById(R.id.rv_tv_loc);
            tv_price = itemView.findViewById(R.id.rv_tv_price);
            tv_type = itemView.findViewById(R.id.rv_tv_type);
        }
    }

    @Override
    public AdvertAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_recyclerview, parent, false));
    }

    @Override
    public void onBindViewHolder(AdvertAdapter.ViewHolder holder, int position) {
        holder.tv_title.setText(data.get(position).getTitle());
        holder.tv_desc.setText(data.get(position).getDesc());
        holder.tv_loc.setText(data.get(position).getLoc());
        holder.tv_price.setText(data.get(position).getPrice());
        holder.tv_type.setText((data.get(position).getType() == AdvertViewModel.TYPE.RENT ? "RENT" : "SELL"));
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    @Override
    public Filter getFilter() {
        return this.filter;
    }
}
