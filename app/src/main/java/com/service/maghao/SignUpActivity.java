package com.service.maghao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    public static final String TAG = "SignUpActivity";

    EditText phoneNumberField, codeField;
    Button sendCodeButton, verifyCodeButton;
    TextView codeSentMessage;
    ProgressBar progressBar;


    //user data map
    Map<String, String> userData;

    //wallet saving
    boolean taskCompleted = false;


    //auth reference
    FirebaseAuth myAuth;

    //storage
    StorageReference storageReference;

    //fire store reference to store user data
    FirebaseFirestore userDatabase;

    private String verificationId;

    String fullUserName, fullUserPhone, fullUserEmail, fullUserGender, fullWalletKey;

    String imageDownloadUrl;

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        myAuth = FirebaseAuth.getInstance();

        userDatabase = FirebaseFirestore.getInstance();

        phoneNumberField = findViewById(R.id.phone_number_field);
        codeField = findViewById(R.id.code_field);

        progressBar = findViewById(R.id.progressBarSignUp);

        codeSentMessage = findViewById(R.id.code_sent_message);

        sendCodeButton = findViewById(R.id.send_code_button);
        verifyCodeButton = findViewById(R.id.verify_code_button);

        fullUserName = getIntent().getStringExtra("username");
        fullUserPhone = getIntent().getStringExtra("phonenumber");
        fullUserEmail = getIntent().getStringExtra("emailaddress");
        fullUserGender = getIntent().getStringExtra("gender");
        fullWalletKey = getIntent().getStringExtra("walletkey");
        imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));

        Log.d(TAG, "userData: " + fullUserName + " " + imageUri);

        phoneNumberField.setText(fullUserPhone);

        userData = new HashMap<>();

        userData.put("name", fullUserName);
        userData.put("email", fullUserEmail);
        userData.put("number", fullUserPhone);
        userData.put("gender", fullUserGender);
        userData.put("walletKey", fullWalletKey);

        sendCodeButton.setOnClickListener(
                view -> {
                    checkNumberValidation(phoneNumberField.getText().toString());

                }
        );

        verifyCodeButton.setOnClickListener(
                view ->
                checkCodeValidation(codeField.getText().toString())
        );



    }

    //check number length
    private void checkNumberValidation(String number){

        if (number == null){
            phoneNumberField.requestFocus();
            phoneNumberField.setError("Please provide valid number");
            return;
        }
        if(number.length() < 14){
            phoneNumberField.requestFocus();
            phoneNumberField.setError("Please provide valid number");
            return;
        }

        codeSentMessage.setText("Code is sent to: " + number);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(number, 30, TimeUnit.SECONDS, this , mCallBacks);

    }

    private void signInWithCredential(PhoneAuthCredential credential){

        myAuth.signInWithCredential(credential).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            uploadProfileToFirebase();
                        }
                        else {
                            Log.d("SignUpActivity", "onError: "+ task.getException().getMessage());
                        }

                    }
                }
        );
    }

    //to check entered code
    private void checkCodeValidation(String code){

        if (code == null){

            Toast.makeText(this, "Code is empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (code.length() < 6){
            Toast.makeText(this, "Incorrect code!", Toast.LENGTH_SHORT).show();
        }

        progressBar.setVisibility(View.VISIBLE);

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        signInWithCredential(credential);


    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);

                    verificationId = s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    String code = phoneAuthCredential.getSmsCode();

                    if (code != null){
                        codeField.setText(code);
                        checkCodeValidation(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {

                    Toast.makeText(SignUpActivity.this, "Verification Failed!", Toast.LENGTH_SHORT).show();
                }
            };


    //verify
    private void saveUserDataToCloudFireStore(Map<String, String> data){

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //data.put("profile", imageDownloadUrl);

        userDatabase.collection("Users")
                .document(userId)
                .collection("profileData")
                .add(data)
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                if (task.isSuccessful()) {

                                    Toast.makeText(SignUpActivity.this, "Saving profile!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        }
                );
    }


    //file extension like jpeg, png etc.
    private String getFileExtension(Uri uri) {

        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadProfileToFirebase(){

        storageReference = FirebaseStorage.getInstance().getReference("profilePictures").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        StorageReference reference = storageReference
                .child("profile."+System.currentTimeMillis()+getFileExtension(imageUri));

        reference.putFile(imageUri)
                .addOnSuccessListener(
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                reference.getDownloadUrl()
                                        .addOnSuccessListener(
                                                new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {

                                                        imageDownloadUrl = uri.toString();

                                                        userData.put("profile", uri.toString());

                                                        saveUserDataToCloudFireStore(userData);

                                                        Log.d(TAG, "imageDownloadUrl: " + imageDownloadUrl);

                                                    }
                                                }
                                        );

                            }
                        }
                );
    }
}
