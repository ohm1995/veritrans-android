package id.co.veritrans.sdk.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import id.co.veritrans.sdk.R;

/**
 * Created by shivam on 10/16/15.
 */
public class PaymentMethodsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payments_method);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}