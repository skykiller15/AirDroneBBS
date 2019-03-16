package com.kurofish.airdronebbs.activities;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.kurofish.airdronebbs.R;
import com.kurofish.airdronebbs.data.DoingItem;
import com.kurofish.airdronebbs.utils.BadWordUtil2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class AddDoingActivity extends AppCompatActivity {

    private EditText nameEditText;
    private TextView dateTextView;
    private EditText numEditText;
    private EditText detailEditText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Date mDate;
    private static final String TAG = "AddDoingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doing);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle(R.string.add_a_doing);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(true);

        nameEditText = findViewById(R.id.doingNameEditText);
        dateTextView = findViewById(R.id.doingDateEditText);
        numEditText = findViewById(R.id.doingNumEditText);
        detailEditText = findViewById(R.id.detailEditText);
        Button cancelButton = findViewById(R.id.doingCancelButton);
        Button postButton = findViewById(R.id.doingPostButton);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mDate = new Date(System.currentTimeMillis());

        setDateEditText();
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postDoing();
            }
        });

    }

    private void setDateEditText() {
        Calendar calendar = Calendar.getInstance();
        dateTextView.setText(calendar.getTime().toString());
    }

    private void setDate() {
        TimePickerView pvTime = new TimePickerBuilder(AddDoingActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                dateTextView.setText(date.toString());
                mDate = date;
            }
        })
                .setType(new boolean[]{true, true, true, true, true, true})
                .build();
        pvTime.show();
    }

    private void postDoing() {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String announcer = user.getDisplayName();
        String name = nameEditText.getText().toString();
        String full_num_s = numEditText.getText().toString();
        long cur_num = 1;
        String detail = detailEditText.getText().toString();
        boolean checkInput = true;

        if (name.isEmpty()) {
            checkInput = false;
        }
        if (full_num_s.isEmpty()) {
            checkInput = false;
        }
        if (detail.isEmpty()) {
            checkInput = false;
        }

        if (!checkInput) {
            Toast.makeText(this, getString(R.string.invalid_post), Toast.LENGTH_SHORT).show();
        } else {
            final DoingItem post = new DoingItem();
            post.setAnnouncer(announcer);
            post.setName(name);
            post.setCur_participant(cur_num);
            post.setFull_participant(Long.valueOf(full_num_s));
            post.setDate(mDate);
            post.setDetail(detail);

            AssetManager assetManager = getAssets();
            InputStream in;
            try {
                in = assetManager.open("dictionary.txt");
                BadWordUtil2 badWordUtil2 = new BadWordUtil2(in);
                if (badWordUtil2.isContaintBadWord(name, 1)
                        || badWordUtil2.isContaintBadWord(detail, 1)) {
                    Toast.makeText(this, getString(R.string.sensitive_post), Toast.LENGTH_SHORT).show();
                } else {
                    Query query = db.collection(getString(R.string.doing_collection_id)).orderBy("id", Query.Direction.DESCENDING).limit(1);
                    query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            DoingItem maxDoingItem = queryDocumentSnapshots.toObjects(DoingItem.class).get(0);
                            long maxID = maxDoingItem.getId() + 1;
                            post.setId(maxID);
                            db.collection(getString(R.string.doing_collection_id)).add(post)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "Add doing succeed");
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "Add doing failed");
                                        }
                                    });
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
