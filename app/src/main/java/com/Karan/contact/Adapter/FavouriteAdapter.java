package com.Karan.contact.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.Karan.contact.AddEditContactActivity;
import com.Karan.contact.Model.ContactModel;
import com.Karan.contact.R;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {

    public ArrayList<ContactModel> contacts;
    public Context context;

    public FavouriteAdapter(ArrayList<ContactModel> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_card, parent, false);
        return new FavouriteAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.contactName.setText(contacts.get(position).getFirstName()+" "+contacts.get(position).getLastName());
        StorageReference storageReference =  FirebaseStorage.getInstance().getReference("Images/"+contacts.get(position).getImage());

        storageReference.getDownloadUrl().addOnSuccessListener(uri ->
                Glide.with(context)
                        .load(uri)
                        .into(holder.contactImage));
        holder.card.setOnClickListener(view -> {
            Intent intent = new Intent(context, AddEditContactActivity.class);
            intent.putExtra("contactID", contacts.get(position).getId());
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView contactImage;
        public TextView contactName;
        public CardView card;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.conctact_materiac_card);
            contactImage = itemView.findViewById(R.id.contact_image);
            contactName = itemView.findViewById(R.id.contact_name);
        }
    }
}
