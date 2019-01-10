package com.kurofish.airdronebbs;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BbsRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String mainTitle;
    private String subTitle;
    private List<String> docID = new ArrayList<>();
    private long clickCount = 0;
    private int itemCount = 0;
    private Boolean isGetDocSuccessful = false;

    private static final String TAG = "BBSRVAdapterTAG";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;

    private LayoutInflater mLayoutInflater;
    private Context mContext;

    private TextView mainTitleTV;
    private TextView subTitleTV;
    private FloatingActionButton loveFAB;

    public BbsRVAdapter(final Context context, String collectionID) {
        collectionReference = db.collection(collectionID);
        readData(new FireStoreCallback() {
            @Override
            public void onCallback(List<String> list) {
                docID = list;
            }
        });
        for (int i = 0; i < docID.size(); i++) {
            Log.d(TAG, docID.get(i));
        }
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    private interface FireStoreCallback {
        void onCallback(List<String> list);
    }

    private void readData(final FireStoreCallback fireStoreCallback) {
        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                docID.add(document.getId());
                                Log.d(TAG, docID.get(i));
                                i++;
                            }
                            fireStoreCallback.onCallback(docID);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "Creating View");
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_bbs_post, viewGroup, false);
        mainTitleTV = view.findViewById(R.id.bpMainTitleText);
        subTitleTV = view.findViewById(R.id.bpSubTitleText);
        loveFAB = view.findViewById(R.id.bpLoveFAB);
        RecyclerView.ViewHolder viewHolder = new RecyclerView.ViewHolder(view){};
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Log.d(TAG, "Binding View");
        DocumentReference docRef = collectionReference.document(docID.get(i));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        mainTitle = document.getString("Main Title");
                        subTitle = document.getString("Sub Title");
                        clickCount = document.getLong("click");
                        isGetDocSuccessful = true;
                        itemCount++;
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        isGetDocSuccessful = false;
                        Log.d(TAG, "No such document");
                    }
                } else {
                    isGetDocSuccessful = false;
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        if (isGetDocSuccessful) {
            mainTitleTV.setText(mainTitle);
            subTitleTV.setText(subTitle);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO goto post detail page
                updateClick(itemCount - 1);
            }
        });
    }

    private void updateClick(int i) {
        DocumentReference docRef = collectionReference.document(docID.get(i));
        docRef.update("click", clickCount + 1);
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

}
