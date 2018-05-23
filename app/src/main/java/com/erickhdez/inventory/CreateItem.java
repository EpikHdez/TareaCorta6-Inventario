package com.erickhdez.inventory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.erickhdez.inventory.model.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class CreateItem extends AppCompatActivity {
    private EditText txtItemName, txtItemDescription, txtItemPrice;
    private ImageView imgItemPicture;
    private View itemForm, createItemProgress;

    private StorageReference storage;
    private DatabaseReference database;
    private FirebaseUser user;

    private String itemId;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);

        imgItemPicture = findViewById(R.id.imgItemPicture);
        txtItemName = findViewById(R.id.txtItemName);
        txtItemDescription = findViewById(R.id.txtItemDecription);
        txtItemPrice = findViewById(R.id.txtItemPrice);

        itemForm = findViewById(R.id.itemForm);
        createItemProgress = findViewById(R.id.create_item_progress);

        itemId = UUID.randomUUID().toString();
        user = FirebaseAuth.getInstance().getCurrentUser();

        String sReference = String.format("/%s/images/%s", user.getUid(), itemId);
        storage = FirebaseStorage.getInstance().getReference(sReference);

        String dReference = String.format("/users/%s/inventory/", user.getUid());
        database = FirebaseDatabase.getInstance().getReference(dReference);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            Glide.with(this)
                    .load(filePath)
                    .into(imgItemPicture);
        }
    }

    public void onImgItemPictureClicked(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void onBtnSaveItemClicked(View view) {
        String name, description, price;
        double parsedPrice = 0.0;
        boolean cancel = false;

        txtItemName.setError(null);
        txtItemDescription.setError(null);
        txtItemPrice.setError(null);

        name = txtItemName.getText().toString();
        description = txtItemDescription.getText().toString();
        price = txtItemPrice.getText().toString();

        if (TextUtils.isEmpty(name)) {
            txtItemName.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        if (TextUtils.isEmpty(description)) {
            txtItemDescription.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        if (TextUtils.isEmpty(price)) {
            txtItemPrice.setError(getString(R.string.error_field_required));
            cancel = true;
        } else {
            parsedPrice = Double.parseDouble(price);
        }

        if (cancel) {
            return;
        }

        item = new Item(name, description, storage.getPath(), parsedPrice);
        uploadImage();
    }

    private void uploadImage() {
        if (filePath == null) {
            return;
        }

        showProgress(true);

        storage.putFile(filePath)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    save();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showProgress(false);
                }
            });
    }

    private void save() {
        database.child(itemId).setValue(item)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    closeActivity();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showProgress(false);
                }
            });
    }

    private void closeActivity() {
        finish();
        showProgress(false);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            itemForm.setVisibility(show ? View.GONE : View.VISIBLE);
            itemForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    itemForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            createItemProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            createItemProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    createItemProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            createItemProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            itemForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
