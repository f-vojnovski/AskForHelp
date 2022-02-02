package com.example.askforhelp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        checkForLoggedInUser();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
    }

    public void changeToLoginFragment() {
        LoginFragment loginFragment = LoginFragment.newInstance();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        transaction
                .replace(R.id.main_activity_fragment_container_view, loginFragment, "LOGIN_FRAGMENT")
                .commit();
    }

    private void checkForLoggedInUser() {
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            this.finish();
        }
    }
}