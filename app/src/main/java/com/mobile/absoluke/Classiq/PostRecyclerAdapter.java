package com.mobile.absoluke.Classiq;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dataobject.INTERACTION_TYPE;
import dataobject.Like;
import dataobject.Notification;
import dataobject.Post;
import dataobject.UserInfo;
import tool.Tool;

/**
 * Created by tranminhquan on 11/15/2017.
 */

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.RecyclerViewHolder>{

    private List<Post> listPost = new ArrayList<>();
    private Context context;
    private DatabaseReference mDatabase;
    private UserInfo userInfo;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;


    public PostRecyclerAdapter(Context context, List<Post> lstPost){
        this.context = context;
        this.listPost = lstPost;

        mDatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        mDatabase.child("users_info").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userInfo = dataSnapshot.getValue(UserInfo.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listPost.size();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.feed_item, viewGroup, false);
        return new RecyclerViewHolder(itemView);
    }

    // Dùng để bind dữ liệu từ dataobject Post vào components feed_item
    @Override
    public void onBindViewHolder(final RecyclerViewHolder viewHolder, final int position) {
//        userinfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                userInfo = dataSnapshot.getValue(UserInfo.class);
//                Picasso.with(context).load(userInfo.getAvatarLink()).into(viewHolder.roundedImageAvatar);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
        //Lấy like

        mDatabase.child("interactions/likes").child(listPost.get(position).getPostid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                viewHolder.tvCount.setText(String.valueOf(dataSnapshot.getChildrenCount()) + " votes");
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Like l = data.getValue(Like.class);
                    if (l.getUserid().equals(currentUser.getUid()))
                        viewHolder.imgbtnLike.setLiked(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Uri link = Uri.parse(listPost.get(position).getAvatarLink());
        Picasso.with(context).load(link).into(viewHolder.roundedImageAvatar);
        viewHolder.tvUseranme.setText(listPost.get(position).getUsername());
        viewHolder.tvContent.setText(listPost.get(position).getContent());
        viewHolder.tvContent.setText(listPost.get(position).getContent());

        //Chuyển đổi timstamp sang format của date
        Date date = new Date(listPost.get(position).getTimestamp());
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String dateFormatted = formatter.format(date);
        viewHolder.tvTimestamp.setText(dateFormatted);

        viewHolder.roundedImageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("ID", listPost.get(position).getUserid());
                Tool.pushDataAndChangeActivity(context, ProfileActivity.class, bundle);
            }
        });

        switch(listPost.get(position).getRating()){
            case 1:
                viewHolder.ivRating.setImageResource(R.drawable.terrible);
                break;
            case 2:
                viewHolder.ivRating.setImageResource(R.drawable.bad);
                break;
            case 3:
                viewHolder.ivRating.setImageResource(R.drawable.ok);
                break;
            case 4:
                viewHolder.ivRating.setImageResource(R.drawable.good);
                break;
            case 5:
                viewHolder.ivRating.setImageResource(R.drawable.great);
                break;
        }

        // Lấy ảnh nếu có
        if (!listPost.get(position).getImageLinks().get(0).equals("noimage")){
            viewHolder.imgAdapter = new ImageLinkAdapter(viewHolder.itemView.getContext(), listPost.get(position).getImageLinks());
            viewHolder.recyclerViewImages.setAdapter(viewHolder.imgAdapter);
        }

        viewHolder.imgbtnLike.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

                //Toast.makeText(context, "You like post " + listPost.get(position).getContent(), Toast.LENGTH_SHORT).show();
                String likeid = mDatabase.child("interactions/likes").child(listPost.get(position).getPostid()).push().getKey();
                Like newLike = new Like();
                newLike.setLikeid(likeid);
                newLike.setPostid(listPost.get(position).getPostid());
                newLike.setTimestamp(Calendar.getInstance().getTimeInMillis());
                newLike.setUserid(currentUser.getUid());
                newLike.setUsername(currentUser.getDisplayName());

                //listPost.get(position).increaseLike();
                //int count = listPost.get(position).getLikeCount();
                //tăng like
                //DatabaseReference postRef = mDatabase.child("interactions/posts").child(listPost.get(position).getUserid())
                //        .child(listPost.get(position).getPostid());

                //postRef.child("likeCount").setValue(listPost.get(position).getLikeCount());

                //Push to database
                mDatabase.child("interactions/likes").child(listPost.get(position).getPostid()).child(likeid).setValue(newLike);
                // Đồng thời đầy sang notifycations nếu là người khác like

                // ++Kiểm tra xem có tự sướng không?
                //if (currentUser.getUid().equals(listPost.get(position).getUserid()))
                //    return;

                // ++Nếu ko thì thêm vào notification của người tạo post
                Notification newNoti = new Notification();
                newNoti.setAvatarLink(userInfo.getAvatarLink());
                newNoti.setSenderName(currentUser.getDisplayName());
                newNoti.setType(INTERACTION_TYPE.LIKE);
                newNoti.setPostid(listPost.get(position).getPostid());
                String nid = mDatabase.child("notifications").child(listPost.get(position).getUserid()).push().getKey();
                newNoti.setId(nid);

                // Push to notification
                mDatabase.child("notifications").child(listPost.get(position).getUserid()).child(nid).setValue(newNoti);
            }

            @Override
            public void unLiked(LikeButton likeButton) {

                //Toast.makeText(context, "You unliked post " + listPost.get(position).getContent(), Toast.LENGTH_SHORT).show();
                // Tìm id của người gửi trong likes để xóa
                String idToRemove = currentUser.getUid();
                mDatabase.child("interactions/likes")
                        .child(listPost.get(position).getPostid())
                        .orderByChild("userid").equalTo(idToRemove)
                        .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Like rlike = dataSnapshot.getValue(Like.class);
                        //Toast.makeText(context, "Your like id is " + rlike.getLikeid(), Toast.LENGTH_SHORT).show();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            mDatabase.child("interactions/likes")
                                    .child(listPost.get(position).getPostid())
                                    .child(data.getKey()).removeValue();
                            viewHolder.imgbtnLike.setLiked(false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // Giảm like
                DatabaseReference postRef = mDatabase.child("interactions/posts").child(listPost.get(position).getUserid())
                        .child(listPost.get(position).getPostid());
//                postRef.runTransaction(new Transaction.Handler() {
//                    @Override
//                    public Transaction.Result doTransaction(MutableData mutableData) {
//                        Post post = mutableData.getValue(Post.class);
//                        if (post == null)
//                            return Transaction.success(mutableData);
//
//                        post.decreaseLike();
//                        mutableData.setValue(post);
//
//                        String path = null;
//                        switch (post.getType()){
//                            case ENTERTAINMENT:
//                                path = "newsfeed/entertainment";
//                                break;
//                            case FOOD:
//                                path = "newsfeed/food";
//                                break;
//                            case HOTEL:
//                                path = "newsfeed/hotel";
//                                break;
//                        }
//                        mDatabase.child("newsfeed/general").child(post.getPostid()).setValue(post);
//                        mDatabase.child(path).child(post.getPostid()).setValue(post);
//                        return Transaction.success(mutableData);
//                    }
//
//                    @Override
//                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
//
//                    }
//                });

            }
        });

        viewHolder.imgbtnCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //--TO DO: Popup comment

            }
        });
    }


    public void addItem(Post post){
        for(int i=0; i<listPost.size(); i++){
            if (post.getPostid().equals(listPost.get(i).getPostid()))
                return;
        }
        listPost.add(post);
        notifyItemInserted(listPost.size() - 1);
    }

    public void updateList(List<Post> lstPost){
        this.listPost = lstPost;
        notifyDataSetChanged();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        //Declare components
        ImageView roundedImageAvatar; //--TODO: change to NetworkImageView
        TextView tvUseranme;
        TextView tvTimestamp;
        TextView tvContent;
        TextView tvCount;
        //TextView tvLink;
        //ImageView imgvwPhoto; //--TODO: change to FeedImageView
        ImageButton imgbtnCmt;
        ImageButton imgbtnShare;
        LikeButton imgbtnLike;
        ImageView ivRating;
        RecyclerView recyclerViewImages;
        ImageLinkAdapter imgAdapter;
        GridLayoutManager gridLayoutManager;


        public RecyclerViewHolder(final View itemView) {
            super(itemView);

            //match components
            roundedImageAvatar = itemView.findViewById(R.id.roundImageAvatar);
            tvUseranme = itemView.findViewById(R.id.tvUsername);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvContent = itemView.findViewById(R.id.tvContent);
            imgbtnCmt = itemView.findViewById(R.id.imgbtnCmt);
            imgbtnShare = itemView.findViewById(R.id.imgbtnShare);
            imgbtnLike = itemView.findViewById(R.id.imgbtnLike);
            ivRating = itemView.findViewById(R.id.ivRating);
            recyclerViewImages = itemView.findViewById(R.id.recycler_view_images);
            gridLayoutManager = new GridLayoutManager(itemView.getContext(), 2);
            recyclerViewImages.setHasFixedSize(true);
            recyclerViewImages.setLayoutManager(gridLayoutManager);
            tvCount = itemView.findViewById(R.id.tvCount);

//            tvLink = itemView.findViewById(R.id.tvLink);
//            fimgvwPhoto = itemView.findViewById(R.id.fimgvwPhoto);
        }

    }
}

