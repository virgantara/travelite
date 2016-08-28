package com.keltech.travel;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tgwarrior.travelmockup.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends ActionBarActivity {

    @BindView(R.id.editName) EditText editName;
    @BindView(R.id.editEmail) EditText editEmail;
    @BindView(R.id.editMobile) EditText editMobile;
    @BindView(R.id.signUpBtn) Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }
}
