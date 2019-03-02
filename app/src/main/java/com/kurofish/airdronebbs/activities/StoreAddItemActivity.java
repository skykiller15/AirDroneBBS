package com.kurofish.airdronebbs.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kurofish.airdronebbs.R;
import com.kurofish.airdronebbs.data.StoreItem;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import static com.kurofish.airdronebbs.utils.MD5AndSHA.MD5_SHA;

public class StoreAddItemActivity extends AppCompatActivity {
    private ImageView addItemIV;
    private EditText addItemNameET;
    private EditText addItemPriceET;
    private Button itemUploadButton;
    private String name;
    private String pic_name;
    private long price;
    private Uri itemPicUri;
    private View foucsView;
    private final static int GET_PIC = 9;
    private final static String TAG = "BBSADDITEM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_add_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.addItemToolbar);
        toolbar.setTitle("Upload an item");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(true);

        addItemIV = findViewById(R.id.addItemImageView);
        addItemNameET = findViewById(R.id.addItemNameEditText);
        addItemPriceET = findViewById(R.id.addItemPriceEditText);
        itemUploadButton = findViewById(R.id.itemUploadButton);

        addItemIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(addItemNameET.getText().toString())) {
                    Toast.makeText(StoreAddItemActivity.this, "Please input the name firstly",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                pic_name = addItemNameET.getText().toString().toLowerCase() + ".png";
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, GET_PIC);
            }
        });
        itemUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput() && !TextUtils.isEmpty(itemPicUri.toString())) {
                    if(uploadItem()) {
                        finish();
                    }
                } else {
                    Toast.makeText(StoreAddItemActivity.this, "Please input all the contents",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkInput() {
        boolean result = true;
        name = addItemNameET.getText().toString();
        String sPrice = addItemPriceET.getText().toString();
        if (TextUtils.isEmpty(name)) {
            addItemNameET.setError(getString(R.string.error_field_required));
            foucsView = addItemNameET;
            result = false;
        }
        if (TextUtils.isEmpty(sPrice)) {
            addItemPriceET.setError(getString(R.string.error_field_required));
            foucsView = addItemPriceET;
            result = false;
        }
        if (result) {
            price = Long.parseLong(sPrice);
            return true;
        } else
        {
            foucsView.requestFocus();
            return false;
        }
    }

    private boolean uploadItem() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseStorage mStorage = FirebaseStorage.getInstance();
        final StoreItem newItem = new StoreItem();
        String md5 = MD5_SHA(name, "MD5");

        newItem.setPublisher(mAuth.getCurrentUser().getDisplayName());
        newItem.setName(name);
        newItem.setPic_name(name.toLowerCase());
        newItem.setPrice(price);
        newItem.setMd5(md5);

        final CollectionReference itemRef = db.collection(getString(R.string.item_collection_id));
        Query query = itemRef.orderBy("id", Query.Direction.DESCENDING).limit(1);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                StoreItem maxItem = queryDocumentSnapshots.toObjects(StoreItem.class).get(0);
                long maxID = maxItem.getId() + 1;
                newItem.setId(maxID);
                itemRef.add(newItem).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                });
                StorageReference mReference = mStorage.getReference("trade_airdrone/" + pic_name);
                mReference.putFile(itemPicUri);
            }
        });

        /*Client client = new Client("GSJXMZJMKF", "53028273960e3344f59cc34100e091be");
        Index index = client.getIndex("TradeItem");
        try {
            index.addObjectAsync(new JSONObject()
                    .put("id", newItem.getId())
                    .put("md5", newItem.getMd5())
                    .put("name", newItem.getName())
                    .put("pic_name", newItem.getPic_name())
                    .put("price", newItem.getPrice())
                    .put("publisher", newItem.getPublisher()), null);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            itemPicUri = resultUri;
            addItemIV.setImageURI(itemPicUri);
            Log.d(TAG, itemPicUri.toString());
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        } else if (resultCode == RESULT_OK && requestCode == GET_PIC) {
            Uri sourceUri = data.getData();
            String img_url = sourceUri.getPath();
            Uri destnationUri = Uri.fromFile(new File(getCacheDir(), pic_name));
            UCrop uCrop = UCrop.of(sourceUri, destnationUri)
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(320, 320);
            UCrop.Options options = new UCrop.Options();
            options.setCompressionFormat(Bitmap.CompressFormat.PNG);
            uCrop.withOptions(options);
            uCrop.start(StoreAddItemActivity.this);
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
