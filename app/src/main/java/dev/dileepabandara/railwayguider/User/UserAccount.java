/*
   --------------------------------------
      Developed by
      Dileepa Bandara
      https://dileepabandara.github.io
      contact.dileepabandara@gmail.com
      ©dileepabandara.dev
      2020
   --------------------------------------
*/

package dev.dileepabandara.railwayguider.User;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import dev.dileepabandara.railwayguider.Common.Login;
import dev.dileepabandara.railwayguider.Prevalent.Prevalent;
import dev.dileepabandara.railwayguider.Prevalent.Prevalent2;
import dev.dileepabandara.railwayguider.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

// CanHub imports
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;

public class UserAccount extends AppCompatActivity {

    TextInputLayout txtName, txtEmail, txtMobile;
    TextView lblName, lblMobile;
    ImageView userImage;
    ProgressBar progressBar_account;

    // Launcher to pick an image from gallery
    private ActivityResultLauncher<String> pickImageLauncher;

    // Launcher to crop the picked image
    private ActivityResultLauncher<CropImageContractOptions> cropImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_account);

        // Initialize views
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtMobile = findViewById(R.id.txtMobile);
        lblName = findViewById(R.id.lblName);
        lblMobile = findViewById(R.id.lblMobile);
        userImage = findViewById(R.id.userImage);
        progressBar_account = findViewById(R.id.progressBar_account);

        // Initialize Paper (local storage)
        Paper.init(this);

        // Back button
        ImageView imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> finish());

        // Home button
        ImageView imgHome = findViewById(R.id.imgHome);
        imgHome.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), UserDashboard.class));
            finishAffinity();
        });

        // Set user details
        setUserDetails();

        // Display stored image if exists
        try {
            String paperUserImage = Paper.book().read(Prevalent2.UserImageKey);
            if (paperUserImage != null && !paperUserImage.equals("null")) {
                userImage.setImageURI(Uri.parse(paperUserImage));
            } else {
                userImage.setImageResource(R.drawable.icon_user_profile_pic);
            }
        } catch (Exception e) {
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
        }

// Register launcher to pick image
        pickImageLauncher = registerForActivityResult(
                new androidx.activity.result.contract.ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        // Create crop options
                        CropImageOptions cropOptions = new CropImageOptions();
                        cropOptions.guidelines = CropImageView.Guidelines.ON;
                        cropOptions.fixAspectRatio = true;
                        cropOptions.aspectRatioX = 1;
                        cropOptions.aspectRatioY = 1;

                        // Launch cropper
                        cropImageLauncher.launch(new CropImageContractOptions(uri, cropOptions));
                    }
                }
        );

// Register crop image launcher
        cropImageLauncher = registerForActivityResult(
                new CropImageContract(),
                result -> {
                    if (result.isSuccessful()) {
                        Uri croppedUri = result.getUriContent();
                        if (croppedUri != null) {
                            userImage.setImageURI(croppedUri);
                            Paper.book().write(Prevalent2.UserImageKey, croppedUri.toString());
                            Toast.makeText(UserAccount.this, "Profile photo updated", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Exception error = result.getError();
                        if (error != null) {
                            Toast.makeText(UserAccount.this, "Crop failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    // Fetch user details from Firebase
    private void setUserDetails() {
        final String user_mobile = Paper.book().read(Prevalent.UserMobileKey);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUser = reference.orderByChild("mobile").equalTo(user_mobile);

        progressBar_account.setVisibility(View.VISIBLE);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(user_mobile).exists()) {
                    String nameFromDB = dataSnapshot.child(user_mobile).child("name").getValue(String.class);
                    String mobileFromDB = dataSnapshot.child(user_mobile).child("mobile").getValue(String.class);
                    String emailFromDB = dataSnapshot.child(user_mobile).child("email").getValue(String.class);

                    txtName.getEditText().setText(nameFromDB);
                    txtEmail.getEditText().setText(emailFromDB);
                    txtMobile.getEditText().setText(mobileFromDB);
                    lblName.setText(nameFromDB);
                    lblMobile.setText(mobileFromDB);

                    progressBar_account.setVisibility(View.GONE);
                } else {
                    Toast.makeText(UserAccount.this, "User not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public void onClickUpdate(View view) {
        if (!validateName()) {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirm before update")
                .setMessage("Are you sure to update?")
                .setPositiveButton("UPDATE", (dialog, which) -> {
                    dialog.dismiss();
                    updateProfile();
                })
                .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void updateProfile() {
        final String user_mobile = Paper.book().read(Prevalent.UserMobileKey);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(user_mobile);
        progressBar_account.setVisibility(View.VISIBLE);

        reference.child("name").setValue(txtName.getEditText().getText().toString());
        reference.child("email").setValue(txtEmail.getEditText().getText().toString());
        reference.child("mobile").setValue(txtMobile.getEditText().getText().toString());

        progressBar_account.setVisibility(View.GONE);
        Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(getApplicationContext(), Login.class));
        finishAffinity();
    }

    private Boolean validateName() {
        String val = txtName.getEditText().getText().toString();
        if (val.isEmpty()) {
            txtName.setError("Name cannot be empty");
            return false;
        } else if (val.length() > 20) {
            txtName.setError("Use 0-20 characters for name");
            return false;
        } else {
            txtName.setError(null);
            txtName.setErrorEnabled(false);
            return true;
        }
    }

    // Launch image picker
    public void onClickSelectImage(View view) {
        pickImageLauncher.launch("image/*");
    }

    public void onClickDeleteAccount(View view) {
        final String name = Paper.book().read(Prevalent.UserNameKey);
        new AlertDialog.Builder(this)
                .setTitle("Hi " + name + "! Are you sure to delete your account?")
                .setMessage("We hope to give amazing service by our next updates. Thank you for using Railway Guider.")
                .setPositiveButton("CONFIRM", (dialog, which) -> {
                    startActivity(new Intent(getApplicationContext(), UserAccountDelete.class));
                    dialog.dismiss();
                })
                .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss())
                .show();
    }
}