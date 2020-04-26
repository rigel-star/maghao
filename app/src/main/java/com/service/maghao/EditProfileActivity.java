package com.service.maghao;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.service.maghao.important_classes.GenerateRandomUserKey;
import com.service.maghao.important_classes.UserAccountId;
import com.squareup.picasso.Picasso;

public class EditProfileActivity extends AppCompatActivity {

    EditText nameField, numberField, emailField;
    RadioButton maleButton, femaleButton;
    Button saveButton;

    ImageView uploadProfile;

    private static final int PICK_IMAGE = 1;

    Uri imageUri = null;

    //random key generator
    private GenerateRandomUserKey randomUserKey = new GenerateRandomUserKey();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initWidgets();

        saveButton.setOnClickListener(
                view -> {

                    Log.d("EditProfile", "onImageLoaded: "+ String.valueOf(imageUri));
                    validateUser();

                }
        );

        uploadProfile.setOnClickListener(
                view -> {
                    pickImageFromGallery();
                }
        );


    }

    //on app start if user is already logged in, skip the sign up part
    //so user will directly go to main activity
    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null){

            Intent intent = new Intent(this, MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){

            try {

                imageUri = data.getData();

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);


                Picasso.get().load(imageUri).into(uploadProfile);

            } catch (Exception ex) {

                ex.printStackTrace();
            }

        }

    }

    private void validateUser(){

        if (imageUri == null){
            Toast.makeText(this, "Image is necessary!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nameField.getText().toString() == null){
            nameField.requestFocus();
            nameField.setError("Please provide name");
            return;
        }
        if (numberField.getText().toString().length() < 10 | numberField.getText().toString() == null){
            numberField.requestFocus();
            numberField.setError("Please provide valid number");
            return;
        }
        if (emailField.getText().toString() == null){
            emailField.requestFocus();
            emailField.setError("Please provide email address");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailField.getText().toString()).matches()){
            emailField.requestFocus();
            emailField.setError("Please provide valid email address");
            return;
        }

        if (findGender() == null){
            Toast.makeText(this, "Please choose gender", Toast.LENGTH_SHORT).show();
            return;
        }

        String userWalletKey = randomUserKey.generateKey();

        Intent intent = new Intent(this, SignUpActivity.class);


        /*check if user uploaded image or not*/
        if (imageUri == null){
            intent.putExtra("imageUri", "noImage");
        }
        else
            intent.putExtra("imageUri", imageUri.toString());


        intent.putExtra("username", nameField.getText().toString());
        intent.putExtra("phonenumber", "+977" + numberField.getText().toString());
        intent.putExtra("emailaddress", emailField.getText().toString());
        intent.putExtra("gender", findGender());
        intent.putExtra("walletkey", userWalletKey);

        startActivity(intent);
    }


    private String findGender(){

        if (maleButton.isChecked()){
            return "male";
        }
        if (femaleButton.isChecked()){
            return "female";
        }

        return null;
    }

    //initing widgets
    private void initWidgets(){

        nameField = findViewById(R.id.full_name_field);
        numberField = findViewById(R.id.mobile_number_field);
        emailField = findViewById(R.id.email_field);

        maleButton = findViewById(R.id.male_radio_button);
        femaleButton = findViewById(R.id.female_radio_button);

        saveButton = findViewById(R.id.save_profile_button);

        uploadProfile = findViewById(R.id.upload_profile_buutton);
    }

    //picking image from galley
    private void pickImageFromGallery() {

        Intent intentGallery = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intentGallery.setType("image/*");
        intentGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intentGallery, "Choose picture from"), PICK_IMAGE);
    }
}
