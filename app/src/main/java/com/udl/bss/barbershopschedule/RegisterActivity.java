package com.udl.bss.barbershopschedule;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.udl.bss.barbershopschedule.domain.Barber;
import com.udl.bss.barbershopschedule.domain.Client;
import com.udl.bss.barbershopschedule.listeners.GoogleMaps;
import com.udl.bss.barbershopschedule.serverCommunication.APIController;
import com.udl.bss.barbershopschedule.utils.BitmapUtils;

public class RegisterActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final int PLACE_PICKER_REQUEST = 2;

    private static final int PERMISSION_EXTERNAL_STORAGE = 1;
    private static final int PICK_IMAGE = 1;

    private GoogleApiClient mGoogleApiClient;

    private boolean isBarber;
    private ImageView imageView;
    private EditText et_name, et_pass, et_mail, et_phone, et_desc, et_age;
    private Bitmap bitmap;
    private Button btn_img;
    private Button btn_placesID;
    private String placesID, address;
    private String imagePath;
    private Spinner spinner_gender;
    private CardView cv_desc, cv_age, cv_place;

    private GoogleMaps googleMaps;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        buildGoogleApiClient();

        et_name = findViewById(R.id.editText_register_name);
        et_mail = findViewById(R.id.editText_register_mail);
        et_pass = findViewById(R.id.editText_register_password);
        et_phone = findViewById(R.id.editText_register_phone);
        et_desc = findViewById(R.id.editText_register_desc);
        et_age = findViewById(R.id.editText_register_age);
        btn_img = findViewById(R.id.button_form_image);
        Button btn_ok = findViewById(R.id.button_register_ok);
        btn_placesID = findViewById(R.id.button_selectPlacesID);
        imageView = findViewById(R.id.image_form);
        cv_desc = findViewById(R.id.cardview_desc);
        cv_age = findViewById(R.id.cardview_age);
        cv_place = findViewById(R.id.cardview_place);

        Spinner spinner = findViewById(R.id.mode_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.spinner_items, android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        spinner_gender = findViewById(R.id.gender_spinner);
        ArrayAdapter<CharSequence> adapter_gender = ArrayAdapter.createFromResource(
                this, R.array.gender_spinner_items, android.R.layout.simple_spinner_item
        );
        adapter_gender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_gender.setAdapter(adapter_gender);


        btn_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                PERMISSION_EXTERNAL_STORAGE);
                    }
                } else {
                    Intent pickIntent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickIntent.setType("image/*");
                    startActivityForResult(pickIntent, PICK_IMAGE);
                }
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToServer();
            }
        });

        btn_placesID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPlacePicker();
            }
        });

        googleMaps = new GoogleMaps(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        if (item.equals(getString(R.string.user))) {
            isBarber = false;
            btn_placesID.setVisibility(View.INVISIBLE);
            cv_age.setVisibility(View.VISIBLE);
            cv_desc.setVisibility(View.GONE);
            cv_place.setVisibility(View.GONE);
        } else if (item.equals(getString(R.string.barber))) {
            isBarber = true;
            btn_placesID.setVisibility(View.VISIBLE);
            cv_age.setVisibility(View.GONE);
            cv_desc.setVisibility(View.VISIBLE);
            cv_place.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private boolean registerOk () {

        if (isBarber){

            return et_name != null && et_mail != null && et_pass != null
                    && placesID != null && !et_name.getText().toString().trim().equals("")
                    && !et_pass.getText().toString().equals("") && !et_mail.getText().toString().equals("")
                    && et_phone != null && !et_phone.getText().toString().equals("")
                    && et_desc != null && !et_desc.getText().toString().equals("");
        }

        return et_name != null && et_mail != null && et_pass != null && !et_name.getText().toString().trim().equals("")
                && !et_pass.getText().toString().equals("") && !et_mail.getText().toString().equals("")
                && et_phone != null && !et_phone.getText().toString().equals("")
                && et_age != null && !et_age.getText().toString().equals("");

    }

    private void saveToServer() {

        if (registerOk()) {

            final String username = et_name.getText().toString().trim();
            APIController.getInstance().isUserAvailable(username)
                    .addOnCompleteListener(new OnCompleteListener<Boolean>() {
                @Override
                public void onComplete(@NonNull Task<Boolean> task) {
                    if (task.getResult()) {

                        if (isBarber) {

                            String[] strArray = address.replaceAll("\\'", " ").split(",");

                            Barber barber = new Barber(
                                    username,
                                    et_mail.getText().toString(),
                                    placesID,
                                    et_pass.getText().toString(),
                                    et_phone.getText().toString(),
                                    String.valueOf(spinner_gender.getSelectedItemPosition()),
                                    et_desc.getText().toString(),
                                    strArray[0]+","+strArray[1],
                                    strArray[2].replaceFirst("\\s",""),
                                    imagePath
                            );

                            APIController.getInstance().createBarber(barber);

                            Toast.makeText(getApplicationContext(), getString(R.string.barber_create), Toast.LENGTH_SHORT).show();


                        } else {

                            Client client = new Client(
                                    username,
                                    et_mail.getText().toString(),
                                    et_pass.getText().toString(),
                                    et_phone.getText().toString(),
                                    spinner_gender.getSelectedItemPosition(),
                                    Integer.valueOf(et_age.getText().toString()),
                                    imagePath
                            );

                            APIController.getInstance().createClient(client);

                            Toast.makeText(getApplicationContext(), getString(R.string.client_create), Toast.LENGTH_SHORT).show();

                        }

                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.name_not_available), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.field_error), Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode) {
            case PICK_IMAGE:
                if(resultCode == Activity.RESULT_OK){
                    Uri selectedImage = intent.getData();
                    if (selectedImage != null) {
                        bitmap = BitmapUtils.getImageFromUri(intent.getData(), this);
                        imageView.setImageBitmap(bitmap);
                    }

                }
                break;

            case PLACE_PICKER_REQUEST:
                if(resultCode == RESULT_OK && intent != null){
                    placesID = PlacePicker.getPlace(this, intent).getId();
                    address = (String) PlacePicker.getPlace(this, intent).getAddress();

                    googleMaps.setMap(PlacePicker.getPlace(this, intent).getName().toString(),
                            PlacePicker.getPlace(this, intent).getLatLng());
                    TextView tv_no_place = findViewById(R.id.textView_no_place);
                    tv_no_place.setVisibility(View.GONE);
                }
                break;
        }
    }


    private void displayPlacePicker() {
        if( mGoogleApiClient == null || !mGoogleApiClient.isConnected() )
            return;

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult( builder.build( this ), PLACE_PICKER_REQUEST );
        } catch ( GooglePlayServicesRepairableException e ) {
            Log.d( "PlacesAPI Demo", "GooglePlayServicesRepairableException thrown" );
        } catch ( GooglePlayServicesNotAvailableException e ) {
            Log.d( "PlacesAPI Demo", "GooglePlayServicesNotAvailableException thrown" );
        }
    }

    private void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient
                .Builder( this )
                .enableAutoManage( this, 0, this )
                .addApi( Places.GEO_DATA_API )
                .addApi( Places.PLACE_DETECTION_API )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if( mGoogleApiClient != null )
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("ON CONNECTION FAILED","Google Places API connection failed with error code:");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

}
