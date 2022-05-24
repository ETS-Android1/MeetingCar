package fr.flareden.meetingcar.ui.annonce;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.flareden.meetingcar.R;
import fr.flareden.meetingcar.metier.entity.Image;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

    Context context;
    ArrayList<Image> images;
    LayoutInflater mLayoutInflater;

    public ViewPagerAdapter(Context context,  ArrayList<Image> images) {
        this.context = context;
        this.images = images;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ViewPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewPagerAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerAdapter.ViewHolder holder, int position) {
        holder.imageView.setImageDrawable(images.get(position).getDrawable());
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_view);
        }
    }

    public void addImage(Image img){
        this.images.add(img);
    }
}
