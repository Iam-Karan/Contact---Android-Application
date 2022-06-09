package com.Karan.contact;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.Karan.contact.Adapter.ContactRecyclerview;
import com.Karan.contact.Adapter.FavouriteAdapter;
import com.Karan.contact.Model.ContactModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ImageButton addContact;
    private SearchView searchView;
    private RecyclerView recyclerView, favRecyclerView;
    private ContactRecyclerview adapter;
    private FavouriteAdapter favAdapter;
    private ArrayList<ContactModel> contacts;
    private ArrayList<ContactModel> copyContacts;
    private ArrayList<ContactModel> favContacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.home_screen_searchView);
        addContact = findViewById(R.id.add_contact);
        recyclerView = findViewById(R.id.home_recyclerview);
        favRecyclerView = findViewById(R.id.fav_recyclerview);
        contacts = new ArrayList<>();
        copyContacts = new ArrayList<>();
        favContacts = new ArrayList<>();
        loadContacts();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.search(s, copyContacts);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.search(s, copyContacts);
                return true;
            }
        });

        searchView.setOnCloseListener(() -> {
            adapter.search("", copyContacts);
            return false;
        });

        addContact.setOnClickListener(view -> {
            Intent intent = new Intent(getBaseContext(), AddEditContactActivity.class);
            intent.putExtra("contactID", "");
            startActivity(intent);
        });

    }

    private void loadContacts(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("Contacts").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            String id = d.getId().toString();
                            String firstName = Objects.requireNonNull(d.getData().get("firstName")).toString();
                            String lastName = Objects.requireNonNull(d.getData().get("lastName")).toString();
                            String email = Objects.requireNonNull(d.getData().get("Email")).toString();
                            String image = Objects.requireNonNull(d.getData().get("image")).toString();
                            String phonenumber = Objects.requireNonNull(d.getData().get("PhoneNumber")).toString();
                            boolean isFavourite = (boolean) d.getData().get("isFavourite");
                            ContactModel data = new ContactModel(id, firstName, lastName,email, phonenumber, image, isFavourite);
                            if(isFavourite){
                                favContacts.add(data);
                            }
                            contacts.add(data);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No data found in Database", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    copyContacts.addAll(contacts);
                    adapter = new ContactRecyclerview(contacts, getApplicationContext());
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(adapter);

                    favAdapter = new FavouriteAdapter(favContacts, getApplicationContext());
                    RecyclerView.LayoutManager favLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                    favRecyclerView.setLayoutManager(favLayoutManager);
                    favRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    favRecyclerView.setAdapter(favAdapter);
                });
    }
}