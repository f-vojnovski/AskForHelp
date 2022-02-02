package com.example.askforhelp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {
    private EditText emailTextbox;
    private EditText passwordTextbox;

    private Button loginButton;

    private FirebaseAuth mAuth;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        emailTextbox = view.findViewById(R.id.login_form_email);
        passwordTextbox = view.findViewById(R.id.login_form_password);

        loginButton = view.findViewById(R.id.login_form_login_button);
        loginButton.setOnClickListener(v -> onLoginButtonClicked());

        mAuth = FirebaseAuth.getInstance();
    }

    private void onLoginButtonClicked() {
        String email = emailTextbox.getText().toString();
        String password = passwordTextbox.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        onSuccessfulLogin();
                    } else {
                        // TODO: Handle unsuccessful login
                    }
                });
    }

    private void onSuccessfulLogin() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
        this.getActivity().finish();
    }
}