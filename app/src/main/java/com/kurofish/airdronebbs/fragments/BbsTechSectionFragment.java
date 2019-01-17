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

public class BbsTechSectionFragment extends Fragment {
    private RecyclerView recyclerView;

    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;

    private long isLoved = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_bbs_section, container, false);

        db = FirebaseFirestore.getInstance();
        progressBar = view.findViewById(R.id.bProgressBar);
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
