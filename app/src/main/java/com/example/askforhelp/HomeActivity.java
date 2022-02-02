package com.example.askforhelp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.askforhelp.R.color.design_default_color_on_primary;

public class HomeActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;

    private Toolbar toolbar;
    private BottomNavigationView bottomNav;
    private boolean shouldRefreshOnResume = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fragmentManager = getSupportFragmentManager();

        bottomNav = findViewById(R.id.home_activity_bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            onNavigationItemSelected(item);
            return true;
        });

        toolbar = findViewById(R.id.home_activity_toolbar);
        setSupportActionBar(toolbar);
    }

    private void onNavigationItemSelected(MenuItem item) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (item.getItemId()) {
            case R.id.home_nav_home:
                HelpTopicsFragment helpTopicsFragment = HelpTopicsFragment.newInstance();
                transaction
                        .replace(R.id.home_activity_fragment_container_view, helpTopicsFragment, "TOPICS_VIEW_FRAGMENT")
                        .commit();
                break;
            case R.id.home_nav_search:
                SearchHelpTopicsFragment searchHelpTopicsFragment = SearchHelpTopicsFragment.newInstance();
                transaction
                        .replace(R.id.home_activity_fragment_container_view, searchHelpTopicsFragment, "SEARCH_FRAGMENT")
                        .commit();
                break;
            case R.id.home_nav_new:
                NewHelpTopicFragment newHelpTopicFragment = NewHelpTopicFragment.newInstance();
                transaction
                        .replace(R.id.home_activity_fragment_container_view, newHelpTopicFragment, "NEW_POST_FRAGMENT")
                        .commit();
                break;
            case R.id.home_nav_services:
                ServicesFragment servicesFragment = ServicesFragment.newInstance();
                transaction
                        .replace(R.id.home_activity_fragment_container_view, servicesFragment, "SERVICES_FRAGMENT")
                        .commit();
                break;
            case R.id.home_nav_vendors:
                VendorsFragment vendorsFragment = VendorsFragment.newInstance();
                transaction
                        .replace(R.id.home_activity_fragment_container_view, vendorsFragment, "VENDORS_FRAGMENT")
                        .commit();
                break;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (!shouldRefreshOnResume) {
            return;
        }

        HelpTopicsFragment helpTopicsFragment = HelpTopicsFragment.newInstance();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction
                .replace(R.id.home_activity_fragment_container_view, helpTopicsFragment, "LOGIN_FRAGMENT")
                .commit();
        bottomNav.setSelectedItemId(R.id.home_nav_home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_activity_actions, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_actions_log_out:
                onLogOutClicked();
                break;
            case R.id.home_actions_my_profile_button:
                onViewMyProfileClicked();
        }
        return true;
    }

    private void onLogOutClicked() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        HomeActivity.this.finish();
    }

    private void onViewMyProfileClicked() {
        Intent intent = new Intent(this, ViewProfileActivity.class);
        intent.putExtra("USER_UID", FirebaseAuth.getInstance().getCurrentUser().getUid());
        startActivity(intent);
    }

    public void setShouldRefreshOnResume(boolean flag) {
        shouldRefreshOnResume = flag;
    }
}