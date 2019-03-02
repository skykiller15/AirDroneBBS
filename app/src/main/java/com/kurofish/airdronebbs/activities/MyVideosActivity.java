package com.kurofish.airdronebbs.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kurofish.airdronebbs.R;
import com.kurofish.airdronebbs.data.VideoItem;
import com.kurofish.airdronebbs.fragments.TeachingFragment;
import com.kurofish.airdronebbs.utils.SpaceItemDecoration;

import java.util.Objects;

import static java.security.AccessController.getContext;

public class MyVideosActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_videos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle(R.string.my_videos);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(true);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        recyclerView = findViewById(R.id.myVideosRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyVideosActivity.this));
        showMyVideos();
    }

    private void showMyVideos() {
        String userName = mAuth.getCurrentUser().getDisplayName();
        Query query;
        query = db.collection(getString(R.string.video_collection_id)).whereEqualTo("uploader", userName).orderBy("id", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<VideoItem> response = new FirestoreRecyclerOptions.Builder<VideoItem>()
                .setQuery(query, VideoItem.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<VideoItem, MyVideosActivity.CardViewHolder>(response) {
            @Override
            public void onBindViewHolder(@NonNull MyVideosActivity.CardViewHolder holder, int position, @NonNull final VideoItem model) {
                holder.videoTitleTV.setText(model.getTitle());
                holder.videoIntroTV.setText(model.getIntro());
                String click = Long.toString(model.getClick());
                holder.videoClickTV.setText(click);
                holder.videoLengthTV.setText(model.getLength());
                String imagePath = "video_thumbnails/" + model.getThumbnail() + ".png";
                StorageReference imageRef = storageReference.child(imagePath);
                Glide.with(MyVideosActivity.this)
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
                                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData mClipData = ClipData.newPlainText("Label", md5);
                                        cm.setPrimaryClip(mClipData);
                                        Toast.makeText(MyVideosActivity.this, "Copy Item ID Succeed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        return false;
                    }
                });
            }

            @NonNull
            @Override
            public MyVideosActivity.CardViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_teaching, group, false);

                return new MyVideosActivity.CardViewHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.addItemDecoration(new SpaceItemDecoration(0, 16));
        recyclerView.setAdapter(adapter);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
