package com.barathi.captain.fragments;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.barathi.captain.R;
import com.barathi.captain.SessionManager;
import com.barathi.captain.adapters.PendingOrderAdapter;
import com.barathi.captain.databinding.FragmentPendingBinding;
import com.barathi.captain.models.OrdersRoot;
import com.barathi.captain.retrofit.Const;
import com.barathi.captain.retrofit.RetrofitBuilder;
import com.barathi.captain.retrofit.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PendingFragment extends Fragment {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;
    private static final String TAG = "profilefra";
    FragmentPendingBinding binding;
    SessionManager sessionManager;
    private String token;
    PendingOrderAdapter pendingOrderAdapter = new PendingOrderAdapter();
    private boolean locationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location lastKnownLocation;
    private int start = 0;
    private boolean isLoding = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pending, container, false);
        sessionManager = new SessionManager(getActivity());


        binding.rvOrders.setAdapter(pendingOrderAdapter);

        if(sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            token = sessionManager.getUser().getData().getToken();
            binding.tvname.setText("Hello ," + sessionManager.getUser().getData().getFullname());
            binding.shimmer.setVisibility(View.VISIBLE);
            binding.shimmer.startShimmer();


            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
            getLocationPermission();
            initListnear();


        } else {
            Toast.makeText(getActivity(), "no login yet", Toast.LENGTH_SHORT).show();
        }


        return binding.getRoot();
    }

    private void initListnear() {
        binding.rvOrders.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!binding.rvOrders.canScrollVertically(1)) {
                    LinearLayoutManager manager = (LinearLayoutManager) binding.rvOrders.getLayoutManager();
                    Log.d(TAG, "onScrollStateChanged: ");
                    int visibleItemcount = manager.getChildCount();
                    int totalitem = manager.getItemCount();
                    int firstvisibleitempos = manager.findFirstCompletelyVisibleItemPosition();
                    Log.d(TAG, "onScrollStateChanged:187   " + visibleItemcount);
                    Log.d(TAG, "onScrollStateChanged:188 " + totalitem);
                    if(!isLoding && (visibleItemcount + firstvisibleitempos >= totalitem) && firstvisibleitempos >= 0) {
                        Log.d(TAG, "onScrollStateChanged: " + start);
                        start = start + Const.LIMIT;
                        binding.pd2.setVisibility(View.VISIBLE);
                        getData();
                    }
                }
            }
        });


    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: ");
        if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;

            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if(locationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), task -> {
                    if(task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.getResult();
                        if(lastKnownLocation != null) {
                            getData();

                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());

                    }
                });
            }
        } catch(SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }


    private void getData() {
        isLoding = true;
        binding.lyt404.setVisibility(View.GONE);
        String lat = String.valueOf(lastKnownLocation.getLatitude());
        String lon = String.valueOf(lastKnownLocation.getLongitude());
        RetrofitService service = RetrofitBuilder.create();
        Call<OrdersRoot> call = service.getPendingOrders(Const.DEV_KEY, token, lat, lon, start, Const.LIMIT);
        call.enqueue(new Callback<OrdersRoot>() {
            @Override
            public void onResponse(Call<OrdersRoot> call, Response<OrdersRoot> response) {
                if(response.code() == 200) {
                    isLoding = false;
                    if(response.body().getStatus() == 200 && !response.body().getData().isEmpty()) {
                        List<OrdersRoot.Datum> data = response.body().getData();
                        pendingOrderAdapter.addData(data);
//
                    } else {
                        if(start == 0) {
                            binding.lyt404.setVisibility(View.VISIBLE);
                            binding.shimmer.setVisibility(View.GONE);
                        }
                    }
                }
                binding.pd.setVisibility(View.GONE);
                binding.pd2.setVisibility(View.GONE);
                binding.shimmer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<OrdersRoot> call, Throwable t) {
                binding.lyt404.setVisibility(View.VISIBLE);
            }
        });
    }
}