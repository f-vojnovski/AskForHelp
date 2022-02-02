package com.example.askforhelp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.concurrent.Executor;


public class RegisterFragment extends Fragment {
    private EditText emailTextbox;
    private EditText displayNameTextbox;
    private EditText passwordTextbox;
    private Button registerButton;
    private TextView redirectToLoginTextView;

    private FirebaseAuth mAuth;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        emailTextbox = view.findViewById(R.id.register_form_email);
        displayNameTextbox = view.findViewById(R.id.register_form_display_name);
        passwordTextbox = view.findViewById(R.id.register_form_password);
        registerButton = view.findViewById(R.id.register_form_register_button);
        redirectToLoginTextView = view.findViewById(R.id.register_form_redirect_to_login);

        registerButton.setOnClickListener(v -> onRegisterButtonClicked());
        redirectToLoginTextView.setOnClickListener(v -> onRedirectToLoginButtonClicked());

        mAuth = FirebaseAuth.getInstance();
    }

    private void onRegisterButtonClicked() {
        String email = emailTextbox.getText().toString();
        String password = passwordTextbox.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        onRegisterSuccessful();
                    }
                });
    }

    private void onRegisterSuccessful() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            return;
        }

        String displayName = displayNameTextbox.getText().toString();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        redirectToHomeActivity();
                    }
                });
    }

    private void onRedirectToLoginButtonClicked() {
        ((MainActivity)getActivity()).changeToLoginFragment();
    }

    private void redirectToHomeActivity() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
        this.getActivity().finish();
    }
}