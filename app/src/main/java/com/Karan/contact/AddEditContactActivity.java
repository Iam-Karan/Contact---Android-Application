package com.Karan.contact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddEditContactActivity extends AppCompatActivity {

    private final int RESULT_LOAD_IMG = 120;
    private ImageView image;
    private LinearLayout callFeatures;
    private TextInputEditText firstName;
    private TextInputEditText lastName;
    private TextInputEditText email;
    private TextInputEditText phoneNumber;
    private ImageButton delete;
    private ImageButton share;
    private ImageButton favourite;
    private Button done;
    private ImageButton backButton;
    private String firstNameString = "";
    private String lastNameString = "";
    private String emailString = "";
    private String phoneNumberString = "";
    private String imageUrl = "";
    private boolean isFavourite;
    private Uri imageUri;
    private File file;
    private FirebaseFirestore firestore;
    private String contactId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_contact);
        Intent intent = getIntent();
        contactId = intent.getExtras().getString("contactID");

        firestore = FirebaseFirestore.getInstance();
        getUI();
        if(!contactId.isEmpty()){
            setData();
            callFeatures.setVisibility(View.VISIBLE);
        }

        done.setOnClickListener(view -> {
            getData();
            if(validate()){
                if(!contactId.isEmpty()){
                    updateContact();
                }else {
                    addContact();
                }
            }
        });
        backButton.setOnClickListener(view -> {
            Intent intent1 = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent1);
        }
        );
        image.setOnClickListener(view -> getImage());
        favourite.setOnClickListener(view -> {
            isFavourite = !isFavourite;
            setFavourite();
        });
        delete.setOnClickListener(view -> deleteContact());
    }

    private void getUI(){
        callFeatures = findViewById(R.id.call_features);
        image = findViewById(R.id.person_image);
        firstName = findViewById(R.id.person_first_name);
        lastName = findViewById(R.id.person_last_name);
        email = findViewById(R.id.person_email);
        phoneNumber = findViewById(R.id.person_phone_number);
        done = findViewById(R.id.contact_done);
        backButton = findViewById(R.id.back_button);
        share = findViewById(R.id.contact_share);
        favourite = findViewById(R.id.contact_favorite);
        delete = findViewById(R.id.contact_delete);
    }

    private void setData(){
        DocumentReference docRef = firestore.collection("Contacts").document(contactId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                DocumentSnapshot d = documentSnapshot;

                firstNameString = Objects.requireNonNull(d.getData().get("firstName")).toString();
                lastNameString = Objects.requireNonNull(d.getData().get("lastName")).toString();
                emailString = Objects.requireNonNull(d.getData().get("Email")).toString();
                imageUrl = Objects.requireNonNull(d.getData().get("image")).toString();
                phoneNumberString = Objects.requireNonNull(d.getData().get("PhoneNumber")).toString();
                isFavourite = (boolean) d.getData().get("isFavourite");
            }

        }).addOnCompleteListener(task -> {

            StorageReference storageReference =  FirebaseStorage.getInstance().getReference("Images/"+imageUrl);

            storageReference.getDownloadUrl().addOnSuccessListener(uri ->
                    Glide.with(getApplicationContext())
                            .load(uri)
                            .into(image));
            firstName.setText(firstNameString);
            lastName.setText(lastNameString);
            email.setText(emailString);
            phoneNumber.setText(phoneNumberString);
            setFavIcon();
        }).addOnFailureListener(e -> Log.d("error", e.toString()));
    }

    private void getData(){
        if(firstNameString.isEmpty()  || !firstName.getText().toString().equals(firstNameString)){
            firstNameString = firstName.getText().toString();
        }
        if(lastNameString.isEmpty() || !lastName.getText().toString().equals(lastNameString)){
            lastNameString = lastName.getText().toString();
        }
        if(emailString.isEmpty() || email.getText().toString().equals(emailString)){
            emailString = email.getText().toString();
        }
        if(phoneNumberString.isEmpty() || phoneNumber.getText().toString().equals(phoneNumberString)){
            phoneNumberString = phoneNumber.getText().toString();
        }
    }

    private void getImage(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                imageUri = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(imageUri,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);

                file = new File(picturePath);
                imageUrl = file.getName();


                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                image.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(AddEditContactActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(AddEditContactActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    public boolean isValidEmailAddress(String emailID) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = java.util.regex.Pattern.compile(ePattern);
        Matcher m = p.matcher(emailID);
        if(!Objects.requireNonNull(email.getText()).toString().isEmpty()){
            return m.matches();
        }
        return false;
    }
    public static boolean isValidMobileNo(String str)
    {
        long num = Long.parseLong("0" + str.replaceAll("[^0-9]", ""));
        return 2000000000L <= num && num < 19999999999L;
    }
    private boolean validate(){
        if (!firstNameString.isEmpty() && !lastNameString.isEmpty() && !imageUrl.isEmpty() && isValidEmailAddress(emailString) && isValidMobileNo(phoneNumberString)){
            return true;
        }else if(firstNameString.isEmpty()){
            setError(firstName, "First Name must not be empty");
        }else if(lastNameString.isEmpty()){
            setError(lastName, "Last Name must not be empty");
        }else if(!isValidEmailAddress(emailString)){
            setError(email, "invalid email id");
        } else if(!isValidMobileNo(phoneNumberString)){
            setError(phoneNumber, "invalide phone number");
        }else if(imageUrl.isEmpty()){
            Toast.makeText(getApplicationContext(), "Image Should not be null", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void setError(TextInputEditText field, String message){
        field.setError(message);
    }

    private void addContact(){
        Map<String, Object> productData = new HashMap<>();
        productData.put("firstName", firstNameString);
        productData.put("lastName", lastNameString);
        productData.put("Email", emailString);
        productData.put("PhoneNumber", phoneNumberString);
        productData.put("image", imageUrl);
        productData.put("isFavourite", false);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("Images");

        storageRef.child(imageUrl).putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    firestore.collection("Contacts").document().set(productData).addOnSuccessListener(unused -> {
                        Toast.makeText(AddEditContactActivity.this, "Contact Added Successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }).addOnFailureListener(e -> Toast.makeText(AddEditContactActivity.this, e.toString(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(AddEditContactActivity.this, e.toString(), Toast.LENGTH_SHORT).show());
    }

    private void updateContact(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("Images");


        firestore.collection("Contacts").document(contactId).update(
                "firstName" , firstNameString,
                "lastName" , lastNameString,
                "Email", emailString,
                "PhoneNumber", phoneNumberString,
                "image", imageUrl,
                "isFavourite" , true
        ).addOnSuccessListener(unused -> {

            if(imageUri != null){
                storageRef.child(imageUrl).putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            Toast.makeText(AddEditContactActivity.this, "Contact updated Successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> Toast.makeText(AddEditContactActivity.this, e.toString(), Toast.LENGTH_SHORT).show());
            }
        }).addOnFailureListener(e -> Toast.makeText(AddEditContactActivity.this, e.toString(), Toast.LENGTH_SHORT).show());
    }

    private void setFavourite(){
        setFavIcon();
        firestore.collection("Contacts").document(contactId).update(
                "isFavourite" , isFavourite
        ).addOnSuccessListener(unused -> Toast.makeText(AddEditContactActivity.this, "Contact added as Favourite", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(AddEditContactActivity.this, e.toString(), Toast.LENGTH_SHORT).show());
    }
    private void deleteContact(){
        firestore.collection("Contacts").document(contactId).delete().addOnSuccessListener(unused -> {
            Toast.makeText(AddEditContactActivity.this, "Contact deleted Successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });
    }

    private void setFavIcon(){
        if(isFavourite){
            favourite.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_round_favorite_24));
        }else {
            favourite.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_round_favorite_border_24));
        }
    }
}