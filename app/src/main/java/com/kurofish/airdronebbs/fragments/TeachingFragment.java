package com.kurofish.airdronebbs.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
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
import com.lapism.searchview.Search;
import com.lapism.searchview.widget.SearchView;

import java.util.Objects;

import static android.support.v4.content.ContextCompat.getSystemService;


public class TeachingFragment extends Fragment {
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private RecyclerView videoRecyclerView;
    private RecyclerView videoRecyclerView2;
    private FirestoreRecyclerAdapter adapter;
    private FirestoreRecyclerAdapter adapter2;
    private StorageReference storageReference;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_teaching, container, false);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        videoRecyclerView = view.findViewById(R.id.videoRecyclerView);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        videoRecyclerView.addItemDecoration(new SpaceItemDecoration(0, 16));
        videoRecyclerView2 = view.findViewById(R.id.videoRecyclerView2);
        videoRecyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));
        videoRecyclerView2.addItemDecoration(new SpaceItemDecoration(0, 16));
        videoRecyclerView2.setVisibility(View.GONE);
        showVideos();
        showVideos2("md5");
        searchView = view.findViewById(R.id.teachingSearchView);
        searchView.setOnQueryTextListener(new Search.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(CharSequence query) {
                if (query.equals("b")) {
                    showVideos();
                    videoRecyclerView.setVisibility(View.VISIBLE);
                    videoRecyclerView2.setVisibility(View.GONE);
                } else {
                    showVideos2(query.toString());
                    videoRecyclerView2.setVisibility(View.VISIBLE);
                    videoRecyclerView.setVisibility(View.GONE);
                    //Toast.makeText(getContext(), query.toString(), Toast.LENGTH_SHORT).show();
                }
                searchView.clearFocus();
                return true;
            }

            @Override
            public void onQueryTextChange(CharSequence newText) {

            }
        });
        adapter.notifyDataSetChanged();
        videoRecyclerView.setAdapter(adapter);
        adapter2.notifyDataSetChanged();
        videoRecyclerView2.setAdapter(adapter2);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        adapter2.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        adapter2.stopListening();
    }

    void showVideos() {
        Query query;
        query = db.collection(getString(R.string.video_collection_id)).orderBy("id", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<VideoItem> response = new FirestoreRecyclerOptions.Builder<VideoItem>()
                .setQuery(query, VideoItem.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<VideoItem, TeachingFragment.CardViewHolder>(response) {
            @Override
            public void onBindViewHolder(@NonNull TeachingFragment.CardViewHolder holder, int position, @NonNull final VideoItem model) {
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
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Query query1 = db.collection(getString(R.string.video_collection_id)).whereEqualTo("title", model.getTitle());
                        query1.get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        String md5 = queryDocumentSnapshots.getDocuments().get(0).getString("md5");
                                        ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData mClipData = ClipData.newPlainText("Label", md5);
                                        cm.setPrimaryClip(mClipData);
                                        Toast.makeText(getContext(), "Copy Item ID Succeed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        return false;
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


    }

    void showVideos2(String sQuery) {
        Query query;
        query = db.collection(getString(R.string.video_collection_id)).whereEqualTo("md5", sQuery);

        FirestoreRecyclerOptions<VideoItem> response = new FirestoreRecyclerOptions.Builder<VideoItem>()
                .setQuery(query, VideoItem.class)
                .build();

        adapter2 = new FirestoreRecyclerAdapter<VideoItem, TeachingFragment.CardViewHolder>(response) {
            @Override
            public void onBindViewHolder(@NonNull TeachingFragment.CardViewHolder holder, int position, @NonNull final VideoItem model) {
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
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Query query1 = db.collection(getString(R.string.video_collection_id)).whereEqualTo("title", model.getTitle());
                        query1.get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        String md5 = queryDocumentSnapshots.getDocuments().get(0).getString("md5");
                                        ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData mClipData = ClipData.newPlainText("Label", md5);
                                        cm.setPrimaryClip(mClipData);
                                        Toast.makeText(getContext(), "Copy Item ID Succeed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        return false;
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
