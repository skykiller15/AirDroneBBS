package com.kurofish.airdronebbs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class BbsFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private BbsFPAdapter bbsFPAdapter = null;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_bbs, container, false);
        /*tabLayout = view.findViewById(R.id.bTabLayout);
        viewPager = view.findViewById(R.id.bViewPager);
        bbsFPAdapter = new BbsFPAdapter(getChildFragmentManager());
        Log.d("BBSTAG", "test");

        viewPager.setAdapter(bbsFPAdapter);
        tabLayout.setupWithViewPager(viewPager);*/
        db = FirebaseFirestore.getInstance();
        progressBar = view.findViewById(R.id.bProgressBar);
        recyclerView = view.findViewById(R.id.tmpRecyclerView);

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        showPosts();
        progressBar.setVisibility(View.GONE);
        //recyclerView.setAdapter();
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
        Query query = db.collection(getString(R.string.tech_collection_id));

        FirestoreRecyclerOptions<BbsPost> response = new FirestoreRecyclerOptions.Builder<BbsPost>()
                .setQuery(query, BbsPost.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<BbsPost, CardViewHolder>(response) {
            @Override
            public void onBindViewHolder(@NonNull CardViewHolder holder, int position, @NonNull BbsPost model) {
                holder.mainTitleTV.setText(model.getMain_title());
                holder.subTitleTV.setText(model.getSub_title());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }

            @NonNull
            @Override
            public CardViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_bbs_post, group, false);

                return new CardViewHolder(view);
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

}
