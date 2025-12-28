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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import dev.dileepabandara.railwayguider.Common.Login;
import dev.dileepabandara.railwayguider.Common.ReadingWall;
import dev.dileepabandara.railwayguider.Prevalent.Prevalent;

import dev.dileepabandara.railwayguider.R;
import com.google.android.material.navigation.NavigationView;

import io.paperdb.Paper;

public class UserDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // DrawerMenu Variables
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;

    // User profile variables
    TextView lbl_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_dashboard);

        // Hooks
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);

        // Toolbar setup
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");

        // Navigation drawer setup
        navigationView.bringToFront();
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawerOpen, R.string.drawerClose);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // User profile hooks
        lbl_name = findViewById(R.id.txt_name);

        // Set user details
        setUserDetails();
    }

    // Set user details to UserDashboard
    private void setUserDetails() {
        Paper.init(this);
        final String user_mobile = Paper.book().read(Prevalent.UserMobileKey);
        final String user_name = Paper.book().read(Prevalent.UserNameKey);
        String Welcome = "Hi " + user_name + "!";

        try {
            lbl_name.setText(Welcome);
        } catch (Exception e) {
            Toast.makeText(this, "Error passing data: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    // Close drawer if open on back press
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Navigation Drawer Menu handling
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        if (id == R.id.menu_account) {
            startActivity(new Intent(UserDashboard.this, UserAccount.class));

        } else if (id == R.id.menu_logout) {
            Paper.book().destroy();
            startActivity(new Intent(UserDashboard.this, Login.class));
            finish();

        } else if (id == R.id.menu_notes) {
            startActivity(new Intent(UserDashboard.this, ReadingWall.class));

        } else if (id == R.id.menu_online_support) {
            startActivity(new Intent(UserDashboard.this, OnlineSupport.class));

        } else if (id == R.id.menu_settings) {
            startActivity(new Intent(UserDashboard.this, UserSettings.class));

        } else if (id == R.id.menu_share) {
            startActivity(new Intent(UserDashboard.this, UserShare.class));

        } else if (id == R.id.menu_about_us) {
            startActivity(new Intent(UserDashboard.this, AboutUs.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Dashboard Schedule Activity
    public void onClickSchedule(View view) {
        startActivity(new Intent(this, TrainSchedule.class));
    }

    // Dashboard Books Activity
    public void onClickBooks(View view) {
        startActivity(new Intent(this, BookedTrains.class));
    }

    // Dashboard Location Activity
    public void onClickLocation(View view) {
        startActivity(new Intent(this, Location.class));
    }

    // Dashboard ReadingWall Activity
    public void onClickReadingWall(View view) {
        startActivity(new Intent(this, ReadingWall.class));
    }

    // Dashboard Gifts Activity
    public void onClickGifts(View view) {
        startActivity(new Intent(this, RailwayGuiderGifts.class));
    }

    // Dashboard Account Activity
    public void onClickAccount(View view) {
        startActivity(new Intent(UserDashboard.this, UserAccount.class));
    }

    // Dashboard Payments Activity
    public void onClickPayments(View view) {
        startActivity(new Intent(this, PaymentHistory.class));
    }

    // Dashboard Scan Activity
    public void onClickScan(View view) {
        startActivity(new Intent(this, QRScan.class));
    }
}