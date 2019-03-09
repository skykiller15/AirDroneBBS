package com.kurofish.airdronebbs.activities;

import android.graphics.Color;
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
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.kurofish.airdronebbs.R;
import com.kurofish.airdronebbs.data.DoingItem;
import com.kurofish.airdronebbs.fragments.DoingFragment;
import com.kurofish.airdronebbs.utils.SpaceItemDecoration;

import java.util.Random;

public class MyActsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_acts);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.my_activities);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(true);

        recyclerView = findViewById(R.id.myActsRecyclerView);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        recyclerView.setLayoutManager(new LinearLayoutManager(MyActsActivity.this));
        showMyDoings();
    }

    private void showMyDoings() {
        String userName = mAuth.getCurrentUser().getDisplayName();
        Query query = db.collection(getString(R.string.act_collection_id)).whereEqualTo("announcer", userName).orderBy("id", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<DoingItem> response = new FirestoreRecyclerOptions.Builder<DoingItem>()
                .setQuery(query, DoingItem.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<DoingItem, MyActsActivity.CardViewHolder>(response) {
            @Override
            protected void onBindViewHolder(@NonNull MyActsActivity.CardViewHolder holder, int position, @NonNull DoingItem model) {
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
            public MyActsActivity.CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_activities, viewGroup, false);
                return new MyActsActivity.CardViewHolder(view);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
