package com.keltech.travel;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.keltech.travel.modules.Constants;
import com.tgwarrior.travelmockup.R;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AgreementActivity extends ActionBarActivity {

    @BindView(R.id.okBtn)
    Button okBtn;
    @BindView(R.id.cancelBtn)
    Button cancelBtn;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.rate_content)
    TextView rateContent;

    @BindString(R.string.car)
    String carTitle;
    @BindString(R.string.rate_content_car)
    String contentCar;
    @BindString(R.string.bike)
    String bikeTitle;
    @BindString(R.string.rate_content_bike)
    String contentBike;

    int from = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();

        int message = bundle.getInt(Constants.FROM);
        switch (message) {
            case Constants.FROM_BIKE:
                title.setText(bikeTitle);
                rateContent.setText(contentBike);
                break;
            case Constants.FROM_TAXI:
                title.setText(carTitle);
                rateContent.setText(contentCar);
                break;
        }

        from = message;

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("Check", "ok");
                Intent i = new Intent(getBaseContext(), PickUpActivity.class);
                i.putExtra(Constants.FROM, from);
                startActivity(i);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Check", "cancel");
            }
        });
    }
}
