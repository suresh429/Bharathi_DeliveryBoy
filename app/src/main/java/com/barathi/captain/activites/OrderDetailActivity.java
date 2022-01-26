package com.barathi.captain.activites;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.barathi.captain.DirectionsJSONParser;
import com.barathi.captain.R;
import com.barathi.captain.SessionManager;
import com.barathi.captain.adapters.OrderItemAdapter;
import com.barathi.captain.databinding.ActivityOrderDetailBinding;
import com.barathi.captain.databinding.BottomSheetorderdetailBinding;
import com.barathi.captain.databinding.PopUpCompleteorderBinding;
import com.barathi.captain.databinding.PopUpHoldorderBinding;
import com.barathi.captain.models.Address;
import com.barathi.captain.models.OrderDetailRoot;
import com.barathi.captain.models.RestResponse;
import com.barathi.captain.retrofit.Const;
import com.barathi.captain.retrofit.RetrofitBuilder;
import com.barathi.captain.retrofit.RetrofitService;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "orderdetailmap ";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1000;
    ActivityOrderDetailBinding binding;
    SessionManager sessionManager;
    int i = 0;
    private String token;
    private OrderDetailRoot.Data data;
    private RetrofitService service;
    BottomSheetDialog bottomSheetDialog;
    BottomSheetorderdetailBinding sheetbinding;
    GoogleMap map;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private Address.Datum addressObj;
    private String turnongps = "Turn On GPS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail);
        sessionManager = new SessionManager(this);
        String uid = getIntent().getStringExtra("uid");
        String oid = getIntent().getStringExtra("oid");
        String from = getIntent().getStringExtra("from");
        if(from != null && !from.equals("") && from.equals("complete")) {
            binding.cardview.setVisibility(View.GONE);
        }
        service = RetrofitBuilder.create();
        if(sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            token = sessionManager.getUser().getData().getToken();
            getData(uid, oid);
        }


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void getLocationPermission() {

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        // If request is cancelled, the result arrays are empty.
        if(requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        }
        updateLocationUI();
    }


    public void createCustomMarker(Context context, String resource, String name, LatLng destlatlang) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
        Log.d(TAG, "createCustomMarker: " + resource);
        Glide.with(context)
                .asBitmap()
                .load(Const.BASE_IMG_URL + resource)
                .placeholder(R.drawable.delivery_placeholder)
                .error(R.drawable.delivery_placeholder)
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        TextView textView = (TextView) marker.findViewById(R.id.name);
                        textView.setText(name);
                        markerImage.setImageBitmap(resource);
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
                        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
                        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
                        marker.buildDrawingCache();
                        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        marker.draw(canvas);
                        OrderDetailActivity.this.map.addMarker(new MarkerOptions()
                                .position(destlatlang)
                                .title(data.getFirstName())
                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                        return false;
                    }
                })
                .circleCrop()

                .into(markerImage);


    }

    private void setData() {


        setBottomSheet();

        String addressStr = data.getAddress();
        if(addressStr != null && !addressStr.equals("")) {
            try {
                addressObj = new Gson().fromJson(addressStr, Address.Datum.class);
                if(addressObj != null) {

                    binding.tvaddress1.setText(addressObj.getArea().concat(", ").concat(addressObj.getCity()));
                    binding.tvaddress2.setText(addressObj.getHomeNo().concat(", ").concat(addressObj.getSociety() + " " + addressObj.getStreet()
                            + " \n" + addressObj.getArea() + " " + addressObj.getLandmark() + " " + addressObj.getPincode()));
                    setSheetData();
                    binding.tvMobile.setText(addressObj.getMobileNumber());
                    binding.tvCustomerName.setText(addressObj.getFirstName().concat(" " + addressObj.getLastName()));
                    binding.tvName.setText(addressObj.getFirstName().concat(" " + addressObj.getLastName()));

                    binding.tvOrderid.setText("#" + data.getOrderId());

                    binding.tvdeliverytype.setText(data.getPaymentType());
                    binding.tvprice.setText(Const.CURRENCY + String.valueOf(data.getTotalAmount()));
                }
            } catch(Exception o) {
                Log.d("TAG", "setModel: " + o.toString());
            }
        }


        Glide.with(binding.getRoot().getContext())
                .load(Const.BASE_IMG_URL + data.getProfileImage())
                .placeholder(R.drawable.delivery_placeholder)
                .error(R.drawable.delivery_placeholder)
                .circleCrop()
                .into(binding.imgUser);


        initListnear();
    }

    private void setBottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(this);
        sheetbinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottom_sheetorderdetail, null, false);
        BottomSheetBehavior<LinearLayout> sheetBehavior = BottomSheetBehavior.from(binding.continerlyt);
        sheetBehavior.setFitToContents(false);
        sheetBehavior.setHideable(false);//prevents the boottom sheet from completely hiding off the screen
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        binding.arrowicon.setRotation(90);
        binding.arrowicon.setOnClickListener(v -> {
            if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                binding.arrowicon.setRotation(-90);
            } else {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                binding.arrowicon.setRotation(90);
            }
        });

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.arrowicon.setRotation(-90);
                } else if(newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.arrowicon.setRotation(90);
                } else if(newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                    binding.arrowicon.setRotation(90);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//ll
            }
        });
    }

    private void initListnear() {
        binding.tvStart.setOnClickListener(v -> startDelivery());
        binding.tvComplete.setOnClickListener(v -> openColmpletePopup());
        binding.tvHold.setOnClickListener(v -> openHoldPopup());

    }

    private void setSheetData() {
        binding.tvdeliverytype.setText(data.getPaymentType());
        binding.tvName.setText(addressObj.getFirstName().concat(" " + addressObj.getLastName()));
        binding.tvorderid.setText("#" + data.getOrderId());
        binding.tvMobile.setText(addressObj.getMobileNumber());
        OrderItemAdapter orderItemAdapter = new OrderItemAdapter(data.getItemDetails());
        binding.rvorderitems.setAdapter(orderItemAdapter);
        Log.d(TAG, "setSheetData: " + data.getStatus());
        if(data.getStatus().equals("Confirmed ")) {
            binding.lytCompleteHold.setVisibility(View.GONE);
            binding.lytStart.setVisibility(View.VISIBLE);
        } else if(data.getStatus().equals("Delivery Started")) {
            binding.lytCompleteHold.setVisibility(View.VISIBLE);
            binding.lytStart.setVisibility(View.GONE);
        }
    }

    private void radiobtnLogic(PopUpHoldorderBinding holdorderBinding) {


        holdorderBinding.rd1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            removeallchaked(holdorderBinding);
            holdorderBinding.rd1.setChecked(isChecked);
            i = 1;
        });
        holdorderBinding.rd2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            removeallchaked(holdorderBinding);
            holdorderBinding.rd2.setChecked(isChecked);
            i = 2;
        });
        holdorderBinding.rd3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            removeallchaked(holdorderBinding);
            holdorderBinding.rd3.setChecked(isChecked);
            i = 3;
        });
        holdorderBinding.rd4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            removeallchaked(holdorderBinding);
            holdorderBinding.rd4.setChecked(isChecked);
            i = 4;
        });


    }

    private void removeallchaked(PopUpHoldorderBinding holdorderBinding) {
        holdorderBinding.rd1.setChecked(false);
        holdorderBinding.rd2.setChecked(false);
        holdorderBinding.rd3.setChecked(false);
        holdorderBinding.rd4.setChecked(false);
    }

    private void openHoldPopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        PopUpHoldorderBinding holdorderBinding = DataBindingUtil.inflate(inflater, R.layout.pop_up_holdorder, null, false);
        Dialog dialog = new Dialog(this, R.style.customStyle);
        dialog.setContentView(holdorderBinding.getRoot());

        radiobtnLogic(holdorderBinding);


        holdorderBinding.tvPositive.setOnClickListener(v -> holdOrder(dialog));
        holdorderBinding.tvCencel.setOnClickListener(v -> dialog.dismiss());
        holdorderBinding.tvhold.setVisibility(View.VISIBLE);

        dialog.show();
    }

    private void openColmpletePopup() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        PopUpCompleteorderBinding completePopupbinding = DataBindingUtil.inflate(inflater, R.layout.pop_up_completeorder, null, false);
        Dialog dialog = new Dialog(this, R.style.customStyle);
        dialog.setContentView(completePopupbinding.getRoot());

        completePopupbinding.tvdeliverytype.setText(data.getPaymentType());
        completePopupbinding.tvPrice.setText(Const.CURRENCY + String.valueOf(data.getTotalAmount()));
        if(data.getPaymentType().equals("Cash on Delivery")) {
            completePopupbinding.tvDes.setText("Order is Cash On Delivery\n" +
                    "Has customer paid \n you full amount?");
        } else if(data.getPaymentType().equals("Card Payment")) {
            completePopupbinding.tvDes.setText("Order Payment has been transferred online\n" +
                    "You deliver the entire order to customer?");

        }
        completePopupbinding.tvPositive.setOnClickListener(v -> completeOrder(dialog));
        completePopupbinding.tvCencel.setOnClickListener(v -> dialog.dismiss());


        dialog.show();
    }

    private void holdOrder(Dialog dialog) {
        binding.pd.setVisibility(View.VISIBLE);
        Call<RestResponse> call = service.onHoldDelivery(Const.DEV_KEY, token, data.getOrderId(), String.valueOf(i));
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if(response.code() == 200 && response.body().getStatus() == 200) {
                    Log.d(TAG, "onResponse: hold");
                    Toast.makeText(OrderDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();
                }
                binding.pd.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
//ll
            }
        });

    }

    private void startDelivery() {
        binding.pd.setVisibility(View.VISIBLE);
        Call<RestResponse> call = service.startDelivery(Const.DEV_KEY, token, data.getOrderId());
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if(response.code() == 200 && response.body().getStatus() == 200) {
                    Toast.makeText(OrderDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    binding.lytCompleteHold.setVisibility(View.VISIBLE);
                    binding.lytStart.setVisibility(View.GONE);
                }
                binding.pd.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
//ll
            }
        });
    }

    private void getData(String uid, String oid) {
        binding.pd.setVisibility(View.VISIBLE);
        Call<OrderDetailRoot> call = service.getOrdersDetail(Const.DEV_KEY, token, uid, oid);
        call.enqueue(new Callback<OrderDetailRoot>() {
            @Override
            public void onResponse(Call<OrderDetailRoot> call, Response<OrderDetailRoot> response) {
                if(response.code() == 200 && response.body().getStatus() == 200 && response.body().getData() != null) {
                    data = response.body().getData();
                    setData();
                    initMap();
                }
                binding.pd.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<OrderDetailRoot> call, Throwable t) {
//ll
            }
        });
    }

    ArrayList<LatLng> markerPoints;
    SupportMapFragment fm;

    private void initMap() {
        markerPoints = new ArrayList<>();

        // Getting reference to SupportMapFragment of the activity_main
        fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting reference to Button


        // Getting Map for the SupportMapFragment
        fm.getMapAsync(this);
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        Log.d(TAG, "getDirectionsUrl: start==" + strOrigin);
        // Destination of route
        String strDest = "destination=" + dest.latitude + "," + dest.longitude;
        Log.d(TAG, "getDirectionsUrl: end==" + strDest);
        // Sensor enabled
        String sensor = "sensor=false";

        // Waypoints
        StringBuilder waypoints = new StringBuilder();
        for(int i = 2; i < markerPoints.size(); i++) {
            LatLng point = markerPoints.get(i);
            if(i == 2)
                waypoints = new StringBuilder("waypoints=");
            waypoints.append(point.latitude).append(",").append(point.longitude).append("|");
        }

        // Building the parameters to the web service
        String parameters = strOrigin + "&" + strDest + "&" + sensor + "&" + waypoints + "&key=" + getString(R.string.map_api_key);

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.d(TAG, "getDirectionsUrl: url =" + url);
        return url;
    }

    /**
     * A method to download json data from url
     */

    private void updateLocationUI() {
        if(map == null) {
            return;
        }
        try {
            if(locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch(SecurityException e) {
            Log.e(TAG, e.getMessage());
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
                locationResult.addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.getResult();
                        if(lastKnownLocation != null) {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()), 14));

                            setUpMapRoute();
                        } else {
                            Toast.makeText(OrderDetailActivity.this, turnongps, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, TAG, task.getException());
                        Toast.makeText(OrderDetailActivity.this, turnongps, Toast.LENGTH_SHORT).show();

                        map.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch(SecurityException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("TAG", "onMapReady: " + googleMap.toString());
        map = googleMap;

        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();


    }

    boolean imageCreated = false;
    Bitmap bmp = null;
    Marker currentLocationMarker;

    private void completeOrder(Dialog dialog) {
        binding.pd.setVisibility(View.VISIBLE);
        Call<RestResponse> call = service.completeDelivery(Const.DEV_KEY, token, data.getOrderId());
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if(response.code() == 200 && response.body().getStatus() == 200) {
                    Log.d(TAG, "onResponse: colplere");
                    Toast.makeText(OrderDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();
                }
                binding.pd.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
//ll
            }
        });

    }

    private void setUpMapRoute() {
        if(map != null) {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }


            Log.d(TAG, "onCreate: map is not null ");
            map.setMyLocationEnabled(true);

            // Setting onclick event listener for the map

            // The map will be cleared on long click

            if(lastKnownLocation == null) {
                Toast.makeText(this, "unknown Location", Toast.LENGTH_SHORT).show();
                Toast.makeText(OrderDetailActivity.this, turnongps, Toast.LENGTH_SHORT).show();

                return;
            }

            LatLng mylatlang = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            if(addressObj == null) {
                Toast.makeText(this, "Customer Address Not Found", Toast.LENGTH_SHORT).show();
                return;
            }
            if(addressObj.getLongitude() != null && addressObj.getLatitude() != null) {
                if(!addressObj.getLatitude().equals("") && !addressObj.getLongitude().equals("")) {
                    LatLng destlatlang = new LatLng(Double.parseDouble(addressObj.getLatitude()), Double.parseDouble(addressObj.getLongitude()));
                    markerPoints.add(mylatlang);
                    markerPoints.add(destlatlang);


                    createCustomMarker(this, data.getProfileImage(), addressObj.getFirstName(), destlatlang);
                    Log.d(TAG, "onMapReady: desti " + addressObj.getLatitude());
                    Log.d(TAG, "onMapReady: size " + markerPoints.size());
                    if(markerPoints.size() >= 2) {


                        LatLng origin = markerPoints.get(0);
                        LatLng dest = markerPoints.get(1);

                        // Getting URL to the Google Directions API
                        String url = getDirectionsUrl(origin, dest);

                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);
                    }
                } else {
                    Toast.makeText(this, "Client Location Not Available", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Client Location Not Available", Toast.LENGTH_SHORT).show();
            }
            // Click event handler for Button btn_draw

        } else {
            Log.d(TAG, "onCreate: map not ready");
            fm.getMapAsync(this);
        }
    }


    // Fetches data from url passed
    @Deprecated
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data1 from web service
            Log.d(TAG, "doInBackground: ");
            String data1 = "";

            try {
                // Fetching the data1 from web service
                data1 = downloadUrl(url[0]);
            } catch(Exception e) {
                Log.d("TAG Background Task", e.toString());
            }
            return data1;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Deprecated
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "onPostExecute: ");
            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }

        private String downloadUrl(String strUrl) throws IOException {
            Log.d(TAG, "downloadUrl: " + strUrl);
            String data1 = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data1 from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuilder sb = new StringBuilder();

                String line = "";
                while((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data1 = sb.toString();

                br.close();

            } catch(Exception e) {
                Log.d("TAG  222Exception", e.toString());
            } finally {
                if(iStream != null) {
                    iStream.close();
                    urlConnection.disconnect();
                }
            }
            return data1;
        }

    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch(Exception e) {
                Log.d(TAG, "doInBackground: err286  " + e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Deprecated
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            Log.d(TAG, "onPostExecute: ");
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            if(result != null) {
                if(!result.isEmpty()) {
                    for(int i = 0; i < result.size(); i++) {
                        points = new ArrayList<>();
                        lineOptions = new PolylineOptions();

                        // Fetching i-th route
                        List<HashMap<String, String>> path = result.get(i);
                        Log.d(TAG, "onPostExecute: path " + path.toString());
                        // Fetching all the points in i-th route
                        for(int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);
                            Log.d(TAG, "onPostExecute: point " + point.toString());
                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);

                            points.add(position);
                        }
                        Log.d(TAG, "onPostExecute: points size " + points.size());
                        // Adding all the points in the route to LineOptions
                        lineOptions.addAll(points);
                        lineOptions.width(8);
                        lineOptions.color(Color.BLACK);
                        Log.d(TAG, "onPostExecute: line options " + lineOptions.toString());
                    }

                    // Drawing polyline in the Google Map for the i-th route
                    map.addPolyline(lineOptions);
                    Log.d(TAG, "onPostExecute: 2");
                } else {
                    Log.d(TAG, "onPostExecute: result is null " + result);
                }
            } else {
                Log.d(TAG, "onPostExecute: result is null " + result);
            }
        }
    }


}