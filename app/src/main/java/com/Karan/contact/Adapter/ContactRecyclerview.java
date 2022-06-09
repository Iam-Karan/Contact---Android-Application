package com.Karan.contact.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Karan.contact.AddEditContactActivity;
import com.Karan.contact.Model.ContactModel;
import com.Karan.contact.R;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ContactRecyclerview extends RecyclerView.Adapter<ContactRecyclerview.ViewHolder> {
    public ArrayList<ContactModel> contacts;
    public Context context;
    public ContactRecyclerview(ArrayList<ContactModel> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_card, parent, false);
        return new ViewHolder(itemView);
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
        public ImageButton contactCall;
        public MaterialCardView card;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.conctact_materiac_card);
            contactImage = itemView.findViewById(R.id.contact_image);
            contactName = itemView.findViewById(R.id.contact_name);
            contactCall = itemView.findViewById(R.id.contact_call);
        }
    }

    public void search(String text, ArrayList<ContactModel> itemsCopy) {
        contacts.clear();
        if(text.isEmpty() || text == null){
            contacts.addAll(itemsCopy);
        } else{
            text = text.toLowerCase();
            for(ContactModel item: itemsCopy){
                if(item.getFirstName().toLowerCase().contains(text) || item.getLastName().toLowerCase().contains(text)){
                    contacts.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}
