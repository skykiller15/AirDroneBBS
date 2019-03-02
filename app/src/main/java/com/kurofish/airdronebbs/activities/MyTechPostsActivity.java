package com.kurofish.airdronebbs.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.kurofish.airdronebbs.R;
import com.kurofish.airdronebbs.data.BbsPost;
import com.kurofish.airdronebbs.utils.SpaceItemDecoration;

public class MyTechPostsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tech_posts);
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(R.string.my_tech_posts);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(true);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.myTechPostsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyTechPostsActivity.this));
        showMyPosts();
    }

    private void showMyPosts() {
        String userName = mAuth.getCurrentUser().getDisplayName();
        Query query = db.collection(getString(R.string.tech_collection_id)).whereEqualTo("author", userName).orderBy("time", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<BbsPost> response = new FirestoreRecyclerOptions.Builder<BbsPost>()
                .setQuery(query, BbsPost.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<BbsPost, MyTechPostsActivity.CardViewHolder>(response) {
            @SuppressLint("RestrictedApi")
            @Override
            public void onBindViewHolder(@NonNull MyTechPostsActivity.CardViewHolder holder, int position, @NonNull BbsPost model) {
                holder.mainTitleTV.setText(model.getMain_title());
                holder.subTitleTV.setText(model.getSub_title());
                holder.loveFAB.setVisibility(View.GONE);
                final BbsPost post = model;

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Query query1 = db.collection(getString(R.string.tech_collection_id)).whereEqualTo("time", post.getTime());
                        query1.get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        String docID = queryDocumentSnapshots.getDocuments().get(0).getReference().getId();
                                        queryDocumentSnapshots.getDocuments().get(0).getReference().update("click", post.getClick() + 1);
                                        Intent intent = new Intent(MyTechPostsActivity.this, PostDetailActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putParcelable("post", post);
                                        bundle.putString("collectionID", getString(R.string.tech_collection_id));
                                        bundle.putString("documentID", docID);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                });
                    }
                });
            }

            @NonNull
            @Override
            public MyTechPostsActivity.CardViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_bbs_post, group, false);

                return new MyTechPostsActivity.CardViewHolder(view);
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
        private TextView mainTitleTV;
        private TextView subTitleTV;
        private FloatingActionButton loveFAB;

        public CardViewHolder(View itemView) {
            super(itemView);
            mainTitleTV = itemView.findViewById(R.id.bpMainTitleText);
            subTitleTV = itemView.findViewById(R.id.bpSubTitleText);
            loveFAB = itemView.findViewById(R.id.bpLoveFAB);
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
