package com.mobile.absoluke.travelie;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import dataobject.Notification;

/**
 * Created by tranminhquan on 01/04/2018.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.RecyclerViewHolder> {
    private ArrayList<Notification> list = new ArrayList<>();
    private Context context;

    public NotificationAdapter(Context mContext, ArrayList<Notification> mList) {
        context = mContext;
        list = mList;
    }

    @Override
    public NotificationAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.likelist_item, viewGroup, false);
        return new NotificationAdapter.RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.RecyclerViewHolder holder, int position) {
        String content = list.get(position).getSenderName();
//        switch (list.get(position).getType().toString()){
//            case "LIKE":
//                content+=" " + R.string.notify_like;
//                break;
//            case "COMMETN":
//                content+=" " + R.string.notify_cmt;
//                break;
//            case "SHARE":
//                content+=" " + R.string.notify_shared;
//                break;
//        }

        holder.tvUsername.setText(content + " thích bài viết của bạn");
        Uri link = Uri.parse(list.get(position).getAvatarLink());
        Picasso.with(context).load(link).into(holder.roundImageAvatar);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private ImageView roundImageAvatar;
        private TextView tvUsername;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            roundImageAvatar = itemView.findViewById(R.id.roundImageAvatar);
            tvUsername = itemView.findViewById(R.id.tvUsername);
        }
    }
}
