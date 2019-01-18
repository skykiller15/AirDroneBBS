package com.kurofish.airdronebbs.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kurofish.airdronebbs.R;
import com.kurofish.airdronebbs.activities.PostDetailActivity;
import com.kurofish.airdronebbs.data.BbsPost;
import com.kurofish.airdronebbs.data.VideoItem;
import com.kurofish.airdronebbs.utils.SpaceItemDecoration;

import java.util.Objects;

public class TeachingFragment extends Fragment {
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private RecyclerView videoRecyclerView;
    private FirestoreRecyclerAdapter adapter;
    private StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_teaching, container, false);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        videoRecyclerView = view.findViewById(R.id.videoRecyclerView);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        showVideos();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    void showVideos() {
        Query query = db.collection(getString(R.string.video_collection_id)).orderBy("id", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<VideoItem> response = new FirestoreRecyclerOptions.Builder<VideoItem>()
                .setQuery(query, VideoItem.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<VideoItem, TeachingFragment.CardViewHolder>(response) {
            @Override
            public void onBindViewHolder(@NonNull TeachingFragment.CardViewHolder holder, int position, @NonNull VideoItem model) {
                holder.videoTitleTV.setText(model.getTitle());
                holder.videoIntroTV.setText(model.getIntro());
                String click = Long.toString(model.getClick());
                holder.videoClickTV.setText(click);
                holder.videoLengthTV.setText(model.getLength());
                String imagePath = "video_thumbnails/" + model.getThumbnail() + ".png";
                StorageReference imageRef = storageReference.child(imagePath);
                Glide.with(Objects.requireNonNull(getContext()))
                        .load(imageRef)
                        .into(holder.thumbnailIV);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }

            @NonNull
            @Override
            public TeachingFragment.CardViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_teaching, group, false);

                return new TeachingFragment.CardViewHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        videoRecyclerView.addItemDecoration(new SpaceItemDecoration(0, 16));
        videoRecyclerView.setAdapter(adapter);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnailIV;
        private TextView videoTitleTV;
        private TextView videoIntroTV;
        private TextView videoClickTV;
        private TextView videoLengthTV;

        public CardViewHolder(View itemView) {
            super(itemView);
            thumbnailIV = itemView.findViewById(R.id.thumbnailImageView);
            videoTitleTV = itemView.findViewById(R.id.videoTitleTextView);
            videoIntroTV = itemView.findViewById(R.id.videoIntroTextView);
            videoClickTV = itemView.findViewById(R.id.videoClickTextView);
            videoLengthTV = itemView.findViewById(R.id.videoLengthTextView);
        }
    }
}
