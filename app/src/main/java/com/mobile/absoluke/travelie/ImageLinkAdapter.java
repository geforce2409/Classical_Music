package com.mobile.absoluke.travelie;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by tranminhquan on 01/04/2018.
 */

public class ImageLinkAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder>{

    private Context mContext;
    private ArrayList<String> listLink;

    public ImageLinkAdapter(Context context, ArrayList<String> mlistLink) {
        mContext = context;
        listLink = mlistLink;
    }

    @Override
    public ImagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImagesAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycle_view_images, parent, false));
    }

    @Override
    public void onBindViewHolder(ImagesAdapter.ViewHolder holder, int position) {
        Uri link = Uri.parse(listLink.get(position));
        Picasso.with(mContext).load(link).fit().centerCrop().into(holder.getImage());
    }

    @Override
    public int getItemCount() {
        return listLink.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;

        public ViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.image_view);
        }

        public ImageView getImage() {
            return image;
        }

    }
}
