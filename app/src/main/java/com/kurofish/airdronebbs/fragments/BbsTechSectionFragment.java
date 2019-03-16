package com.kurofish.airdronebbs.fragments;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.kurofish.airdronebbs.data.BbsPost;
import com.kurofish.airdronebbs.activities.PostDetailActivity;
import com.kurofish.airdronebbs.R;
import com.kurofish.airdronebbs.utils.SpaceItemDecoration;

import java.util.Random;

public class BbsTechSectionFragment extends Fragment {
    private RecyclerView recyclerView;

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;

    private long isLoved = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_bbs_section, container, false);

        db = FirebaseFirestore.getInstance();
        ProgressBar progressBar = view.findViewById(R.id.bProgressBar);
        recyclerView = view.findViewById(R.id.bsRecyclerView);

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        showPosts();
        progressBar.setVisibility(View.GONE);
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


    private void showPosts() {
        Query query = db.collection(getString(R.string.tech_collection_id)).orderBy("time", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<BbsPost> response = new FirestoreRecyclerOptions.Builder<BbsPost>()
                .setQuery(query, BbsPost.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<BbsPost, BbsTechSectionFragment.CardViewHolder>(response) {
            @Override
            public void onBindViewHolder(@NonNull BbsTechSectionFragment.CardViewHolder holder, int position, @NonNull BbsPost model) {
                holder.mainTitleTV.setText(model.getMain_title());
                holder.subTitleTV.setText(model.getSub_title());
                holder.authorTV.setText(model.getAuthor());
                final BbsPost post = model;

                String author = post.getAuthor();
                String avatar = author.substring(0, 1).toUpperCase();
                holder.avatarTV.setText(avatar);
                Random random = new Random();
                int r, g, b;
                r = random.nextInt(255);
                g = random.nextInt(255);
                b = random.nextInt(255);
                // if background is more likely to white
                if (r+g+b > 255*3/2) {
                    holder.avatarTV.setTextColor(Color.BLACK);
                } else {
                    holder.avatarTV.setTextColor(Color.WHITE);
                }
                holder.avatarTV.setBackgroundColor(Color.rgb(r, g, b));

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
                                        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
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
            public BbsTechSectionFragment.CardViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_bbs_post, group, false);

                return new BbsTechSectionFragment.CardViewHolder(view);
            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {
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
        private TextView avatarTV;
        private TextView authorTV;

        public CardViewHolder(View itemView) {
            super(itemView);
            mainTitleTV = itemView.findViewById(R.id.bpMainTitleText);
            subTitleTV = itemView.findViewById(R.id.bpSubTitleText);
            loveFAB = itemView.findViewById(R.id.bpLoveFAB);
            avatarTV = itemView.findViewById(R.id.postAvatarTextView);
            authorTV = itemView.findViewById(R.id.postAuthorTextView);

            loveFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isLoved == 0) {
                        loveFAB.setImageResource(R.drawable.ic_favorite_24dp);
                        isLoved = 1;
                    } else if (isLoved == 1) {
                        loveFAB.setImageResource(R.drawable.ic_favorite_border_24dp);
                        isLoved = -1;
                    } else if (isLoved == -1) {
                        loveFAB.setImageResource(R.drawable.ic_favorite_24dp);
                        isLoved = 1;
                    }
                }
            });
        }
    }

}
