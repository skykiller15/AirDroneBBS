package com.kurofish.airdronebbs.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kurofish.airdronebbs.MainActivity;
import com.kurofish.airdronebbs.R;
import com.kurofish.airdronebbs.activities.PostDetailActivity;
import com.kurofish.airdronebbs.activities.StoreAddItemActivity;
import com.kurofish.airdronebbs.data.StoreItem;
import com.kurofish.airdronebbs.utils.SpaceItemDecoration;

import java.util.Objects;

public class StoreFragment extends Fragment {
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private RecyclerView itemRecyclerView;
    private FirestoreRecyclerAdapter adapter;
    private StorageReference storageReference;
    private FloatingActionButton addItemFAB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_store, container, false);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        itemRecyclerView = view.findViewById(R.id.tradeItemRecyclerView);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        itemRecyclerView.setLayoutManager(gridLayoutManager);
        showItems();

        addItemFAB = view.findViewById(R.id.addItemFloatingActionButton);
        addItemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), StoreAddItemActivity.class));
            }
        });
        return view;
    }

    void showItems() {
        Query query = db.collection(getString(R.string.item_collection_id)).orderBy("id");

        FirestoreRecyclerOptions<StoreItem> response = new FirestoreRecyclerOptions.Builder<StoreItem>()
                .setQuery(query, StoreItem.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<StoreItem, StoreFragment.CardViewHolder>(response) {
            @Override
            public void onBindViewHolder(@NonNull StoreFragment.CardViewHolder holder, int position, @NonNull final StoreItem model) {
                holder.itemNameTV.setText(model.getName());
                String price = "¥ " + Long.toString(model.getPrice());
                holder.itemPriceTV.setText(price);
                final String imagePath = "trade_airdrone/" + model.getPic_name() + ".png";
                StorageReference imageRef = storageReference.child(imagePath);
                Glide.with(Objects.requireNonNull(getContext()))
                        .load(imageRef)
                        .into(holder.itemIV);
                final String publisher = model.getPublisher();

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        if (publisher.equals(mAuth.getCurrentUser().getDisplayName())) {
                            final AlertDialog.Builder normalDialog =
                                    new AlertDialog.Builder(getContext());
                            normalDialog.setMessage(getString(R.string.delete_message));
                            normalDialog.setPositiveButton(getString(R.string.yes_delete),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Query query1 = db.collection(getString(R.string.item_collection_id)).whereEqualTo("id", model.getId());
                                            query1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    queryDocumentSnapshots.getDocuments().get(0).getReference().delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    StorageReference deleteDef = storageReference.child(imagePath);
                                                                    deleteDef.delete();
                                                                }
                                                            });
                                                }
                                            });
                                        }
                                    });
                            normalDialog.setNegativeButton(getString(R.string.no_delete),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            // 显示
                            normalDialog.show();
                        }
                        return false;
                    }
                });
            }

            @NonNull
            @Override
            public StoreFragment.CardViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_store, group, false);

                return new StoreFragment.CardViewHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        itemRecyclerView.addItemDecoration(new SpaceItemDecoration(8, 16));
        itemRecyclerView.setAdapter(adapter);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemIV;
        private TextView itemNameTV;
        private TextView itemPriceTV;

        public CardViewHolder(View itemView) {
            super(itemView);
            itemIV = itemView.findViewById(R.id.itemImageView);
            itemNameTV = itemView.findViewById(R.id.itemNameTextView);
            itemPriceTV = itemView.findViewById(R.id.itemPriceTextView);
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
