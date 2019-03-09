package com.kurofish.airdronebbs.fragments;

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
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.kurofish.airdronebbs.R;
import com.kurofish.airdronebbs.data.DoingItem;
import com.kurofish.airdronebbs.utils.SpaceItemDecoration;
import com.lapism.searchview.widget.SearchView;

import java.util.Random;

public class DoingFragment extends Fragment {

    private RecyclerView doingRecyclerView;
    private FloatingActionButton addDoingFAB;
    private SearchView doingSearchView;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_doing, container, false);
        doingRecyclerView = view.findViewById(R.id.doingRecyclerView);
        addDoingFAB = view.findViewById(R.id.addDoingFloatingActionButton);
        doingSearchView = view.findViewById(R.id.doingSearchView);
        doingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        db = FirebaseFirestore.getInstance();

        showDoings();

        addDoingFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    private void showDoings() {
        Query query = db.collection(getString(R.string.act_collection_id)).orderBy("id", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<DoingItem> response = new FirestoreRecyclerOptions.Builder<DoingItem>()
                .setQuery(query, DoingItem.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<DoingItem, DoingFragment.CardViewHolder>(response) {
            @Override
            protected void onBindViewHolder(@NonNull DoingFragment.CardViewHolder holder, int position, @NonNull DoingItem model) {
                holder.announcerTV.setText(model.getAnnouncer());
                holder.nameTV.setText(model.getName());
                holder.dateTV.setText(model.getDate().toString());

                String joinNum = Long.toString(model.getCur_participant()) + "/" + Long.toString(model.getFull_participant());
                holder.joinNumTV.setText(joinNum);
                if (model.getCur_participant() == model.getFull_participant()) {
                    holder.joinNumTV.setTextColor(Color.RED);
                } else {
                    holder.joinNumTV.setTextColor(Color.GREEN);
                }

                String announcer = model.getAnnouncer();
                String avatar = announcer.substring(0, 1).toUpperCase();
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
            }

            @NonNull
            @Override
            public DoingFragment.CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_activities, viewGroup, false);
                return new DoingFragment.CardViewHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        doingRecyclerView.addItemDecoration(new SpaceItemDecoration(0, 16));
        doingRecyclerView.setAdapter(adapter);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        private TextView avatarTV;
        private TextView announcerTV;
        private TextView nameTV;
        private TextView dateTV;
        private TextView joinNumTV;

        public CardViewHolder(View itemView) {
            super(itemView);
            avatarTV = itemView.findViewById(R.id.actAvatarTextView);
            announcerTV = itemView.findViewById(R.id.actAnnouncerTextView);
            nameTV = itemView.findViewById(R.id.actNameTextView);
            dateTV = itemView.findViewById(R.id.actDateTextView);
            joinNumTV = itemView.findViewById(R.id.actJoinNumTextView);
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
}
