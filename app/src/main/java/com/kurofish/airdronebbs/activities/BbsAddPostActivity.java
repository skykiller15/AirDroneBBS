package com.kurofish.airdronebbs.activities;

import android.content.Intent;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.kurofish.airdronebbs.data.BbsPost;
import com.kurofish.airdronebbs.R;
import com.kurofish.airdronebbs.utils.BadWordUtil2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class BbsAddPostActivity extends AppCompatActivity {
    private Button cancelButton;
    private Button postButton;
    private String collectionID;
    private EditText mainTitleEditText;
    private EditText subTitleEditText;
    private EditText textEditText;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference postRef;

    final static private String TAG = "ADDPOST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbs_add_post);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle.getString("section_name").equals(getString(R.string.bbs_tech_section))) {
            collectionID = getString(R.string.tech_collection_id);
        } else if (bundle.getString("section_name").equals(getString(R.string.bbs_chat_section))) {
            collectionID = getString(R.string.chat_collection_id);
        }

        cancelButton = findViewById(R.id.cancelButton);
        postButton = findViewById(R.id.postButton);
        mainTitleEditText = findViewById(R.id.mainTitleEditText);
        subTitleEditText = findViewById(R.id.subTitleEditText);
        textEditText = findViewById(R.id.textEditText);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addPost()) {
                    finish();
                }
            }
        });
    }

    private boolean addPost() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String author = currentUser.getDisplayName();
        String mainTitle = mainTitleEditText.getText().toString();
        String subTitle = subTitleEditText.getText().toString();
        String text = textEditText.getText().toString();

        final BbsPost newPost = new BbsPost(mainTitle, subTitle, author);
        newPost.setText(text);

        if (!checkPost(newPost)) {
            Toast.makeText(this, getString(R.string.invalid_post), Toast.LENGTH_SHORT).show();
            return false;
        }

        AssetManager assetManager = getAssets();
        InputStream in;
        try {
            in = assetManager.open("dictionary.txt");
            BadWordUtil2 badWordUtil2 = new BadWordUtil2(in);
            if (badWordUtil2.isContaintBadWord(mainTitle, 1)
                    || badWordUtil2.isContaintBadWord(subTitle, 1)
                    || badWordUtil2.isContaintBadWord(text, 1)) {
                Toast.makeText(this, getString(R.string.sensitive_post), Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Date date = new Date(System.currentTimeMillis());
        newPost.setTime(date);

        newPost.setClick(0);
        postRef = db.collection(collectionID);
        Query query = postRef.orderBy("id", Query.Direction.DESCENDING).limit(1);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                BbsPost maxBbsPost = queryDocumentSnapshots.toObjects(BbsPost.class).get(0);
                long maxID = maxBbsPost.getId() + 1;
                newPost.setId(maxID);
                postRef.add(newPost)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "Add post succeed");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Add post failed");
                            }
                        });
            }
        });

        return true;
    }

    private boolean checkPost(BbsPost bbsPost) {
        if (bbsPost.getMain_title().equals("")) {
            return false;
        } else if (bbsPost.getSub_title().equals("")) {
            return false;
        } else if (bbsPost.getText().equals("")) {
            return false;
        }
        return true;
    }
}
