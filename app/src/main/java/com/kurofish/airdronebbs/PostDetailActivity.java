package com.kurofish.airdronebbs;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class PostDetailActivity extends AppCompatActivity {
    private BbsPost post;
    private TextView avatarTV;
    private TextView authorTV;
    private TextView dateTV;
    private TextView mainTitleTV;
    private TextView subTitleTV;
    private TextView textTV;

    private RecyclerView replyRV;
    private FirestoreRecyclerAdapter adapter;

    private EditText replyEditText;
    private Button replyButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String parentCollectionID;
    private String documentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Post Detail");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        avatarTV = findViewById(R.id.rplyAvatarTextView);
        authorTV = findViewById(R.id.rplyAuthorTextView);
        dateTV = findViewById(R.id.rplyDateTextView);
        mainTitleTV = findViewById(R.id.pdMainTitleTextView);
        subTitleTV = findViewById(R.id.pdSubTitleTextVIew);
        textTV = findViewById(R.id.rplyTextTextView);

        replyRV = findViewById(R.id.pdRepliesRecyclerView);
        replyRV.setLayoutManager(new LinearLayoutManager(this));

        replyEditText = findViewById(R.id.replyEditText);
        replyButton = findViewById(R.id.replyButton);

        Bundle bundle = getIntent().getExtras();
        post = Objects.requireNonNull(bundle).getParcelable("post");
        parentCollectionID = bundle.getString("collectionID");
        documentID = bundle.getString("documentID");

        setAvatar();
        authorTV.setText(post.getAuthor());
        dateTV.setText(post.getTime().toString());
        mainTitleTV.setText(post.getMain_title());
        subTitleTV.setText(post.getSub_title());
        textTV.setText(post.getText());

        showReplies();

        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (replyEditText.getText().toString().equals("")) {
                    Toast.makeText(PostDetailActivity.this, getString(R.string.invalid_post), Toast.LENGTH_SHORT).show();
                } else {
                    sendReply();
                }
            }
        });
    }

    void setAvatar() {
        String author = post.getAuthor();
        String avatar = author.substring(0, 1).toUpperCase();
        avatarTV.setText(avatar);
        Random random = new Random();
        int r, g, b;
        r = random.nextInt(255);
        g = random.nextInt(255);
        b = random.nextInt(255);
        // if background is more likely to white
        if (r+g+b > 255*3/2) {
            avatarTV.setTextColor(Color.BLACK);
        } else {
            avatarTV.setTextColor(Color.WHITE);
        }
        avatarTV.setBackgroundColor(Color.rgb(r, g, b));
    }


    void showReplies() {
        Query query = db.collection(parentCollectionID).document(documentID).collection("replies").orderBy("time");
        FirestoreRecyclerOptions<BbsReply> response = new FirestoreRecyclerOptions.Builder<BbsReply>()
                .setQuery(query, BbsReply.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<BbsReply, PostDetailActivity.CardViewHolder>(response) {
            @Override
            public void onBindViewHolder(@NonNull PostDetailActivity.CardViewHolder holder, int position, @NonNull BbsReply model) {
                holder.authorTV.setText(model.getAuthor());

                String avatar = model.getAuthor().substring(0, 1).toUpperCase();
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

                holder.dateTV.setText(model.getTime().toString());
                holder.textTV.setText(model.getText());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }

            @NonNull
            @Override
            public PostDetailActivity.CardViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_bbs_post_detail_reply, group, false);

                return new PostDetailActivity.CardViewHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        replyRV.addItemDecoration(new SpaceItemDecoration(0, 1));
        replyRV.setAdapter(adapter);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        private TextView authorTV;
        private TextView avatarTV;
        private TextView dateTV;
        private TextView textTV;

        public CardViewHolder(View itemView) {
            super(itemView);
            authorTV = itemView.findViewById(R.id.rplyAuthorTextView);
            avatarTV = itemView.findViewById(R.id.rplyAvatarTextView);
            dateTV = itemView.findViewById(R.id.rplyDateTextView);
            textTV = itemView.findViewById(R.id.rplyTextTextView);
        }
    }

    void sendReply() {
        // build the object
        String author = Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName();
        final BbsReply newReply = new BbsReply(author);
        Date date = new Date(System.currentTimeMillis());
        newReply.setTime(date);
        String text = replyEditText.getText().toString();
        newReply.setText(text);

        // add the reply to the db
        Query query = db.collection(parentCollectionID).whereEqualTo("time", post.getTime());
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        DocumentReference dRef = queryDocumentSnapshots.getDocuments().get(0).getReference();
                        dRef.collection("replies").add(newReply);
                    }
                });
        replyEditText.setText("");
        replyEditText.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
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
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
