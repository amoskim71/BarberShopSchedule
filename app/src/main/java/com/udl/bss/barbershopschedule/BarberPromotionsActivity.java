package com.udl.bss.barbershopschedule;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BarberPromotionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_promotions);

        Button barber_new_promotion_btn = (Button) findViewById(R.id.barber_new_promotion_btn);

        barber_new_promotion_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BarberNewPromotionActivity.class);
                startActivity(intent);
            }
        });

        Button btn_back = (Button) findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BarberHomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
