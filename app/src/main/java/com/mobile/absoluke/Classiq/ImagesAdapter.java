package com.mobile.absoluke.Classiq;

/**
 * Created by Yul Lucia on 01/01/2018.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<String> mImagesList;

    public ImagesAdapter(Context context, ArrayList<String> imagesList) {
        mContext = context;
        mImagesList = imagesList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycle_view_images, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(mContext).load(mImagesList.get(holder.getAdapterPosition())).placeholder(R.drawable.cancel).centerCrop().into(holder.getImage());
    }

    @Override
    public int getItemCount() {
        return mImagesList.size();
    }

    public ArrayList<String> getListImage(){
        return mImagesList;
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
