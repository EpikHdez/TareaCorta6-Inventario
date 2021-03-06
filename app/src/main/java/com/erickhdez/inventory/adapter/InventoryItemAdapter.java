package com.erickhdez.inventory.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.erickhdez.inventory.R;
import com.erickhdez.inventory.model.Item;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InventoryItemAdapter extends ArrayAdapter<Item> {
    private LayoutInflater mInflater;

    public InventoryItemAdapter(@NonNull Context context, @NonNull List<Item> objects) {
        super(context, R.layout.inventory_item, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        Item item = getItem(position);

        TextView itemName, itemDescription;
        final ImageView itemImage;
        String name;

        StorageReference storage = FirebaseStorage.getInstance().getReference(item.getPicture());

        if (view == null) {
            view = mInflater.inflate(R.layout.inventory_item, null);

            itemName = view.findViewById(R.id.itemName);
            itemDescription = view.findViewById(R.id.itemDescription);
            itemImage = view.findViewById(R.id.itemPicture);

            name = String.format("%s ($%s)", item.getName(), item.getPrice());

            itemName.setText(name);
            itemDescription.setText(item.getDescription());

            storage.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(itemImage);
                        }
                    });
        }

        return view;
    }
}
