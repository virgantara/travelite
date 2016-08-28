package com.keltech.travel;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tgwarrior.travelmockup.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends ActionBarActivity {

    @BindView(R.id.signInBtn) Button signInBtn;
    @BindView(R.id.newHere) TextView newHere;
    @BindView(R.id.forgotPassword) TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), SignUpActivity.class);
                startActivity(i);
            }
        });
    }
}
