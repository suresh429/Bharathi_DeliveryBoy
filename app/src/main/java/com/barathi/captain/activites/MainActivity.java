package com.barathi.captain.activites;

import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.barathi.captain.R;
import com.barathi.captain.databinding.ActivityMainBinding;
import com.barathi.captain.fragments.CompleteFragment;
import com.barathi.captain.fragments.PendingFragment;
import com.barathi.captain.fragments.ProfileFragment;

import static com.google.android.gms.common.api.CommonStatusCodes.RESOLUTION_REQUIRED;
import static com.google.android.gms.common.api.CommonStatusCodes.SUCCESS;
import static com.google.android.gms.location.LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initGps();
        initView();
        initListnear();

    }

    private void initGps() {
        if(googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addApi(LocationServices.API).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30000);
            locationRequest.setFastestInterval(5000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);


            builder.setAlwaysShow(true); // this is the key ingredient


            PendingResult result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(result1 -> {
                final Status status = result1.getStatus();


                switch(status.getStatusCode()) {


                    case SUCCESS:
                        Log.d("TAG", "onResult: success");
                        break;

                    case RESOLUTION_REQUIRED:
                        Log.d("TAG", "onResult: requried");

                        try {

                            status.startResolutionForResult(MainActivity.this, 1000);

                        } catch(IntentSender.SendIntentException e) {
                            Log.d("TAG", "onResult: erre" + e.toString());
                        }

                        break;

                    case SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d("TAG", "onResult: unavalible");
                        break;

                    default:

                }
            });


            googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).build();

        }
    }

    private void initListnear() {
        binding.tabComplete.setOnClickListener(v -> loadFragment(new CompleteFragment()));
        binding.tabPending.setOnClickListener(v -> loadFragment(new PendingFragment()));
        binding.tabProfile.setOnClickListener(v -> loadFragment(new ProfileFragment()));
    }

    private void initView() {
        PendingFragment pendingFragment = new PendingFragment();
        loadFragment(pendingFragment);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();

    }
}