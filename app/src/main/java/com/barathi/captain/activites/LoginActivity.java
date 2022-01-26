package com.barathi.captain.activites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.barathi.captain.databinding.ActivityLoginBinding;
import com.google.firebase.iid.FirebaseInstanceId;
import com.barathi.captain.R;
import com.barathi.captain.SessionManager;
import com.barathi.captain.models.DeliveryBoyRoot;
import com.barathi.captain.retrofit.Const;
import com.barathi.captain.retrofit.RetrofitBuilder;
import com.barathi.captain.retrofit.RetrofitService;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    SessionManager sessionManager;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        sessionManager = new SessionManager(this);
        if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            Log.d("TAG", "onCreate: " + sessionManager.getUser().getData().getDeviceToken());
        } else {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w("notification", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        if (task.getResult() != null) {
                            token = task.getResult().getToken();
                            sessionManager.saveStringValue(Const.NOTIFICATION_TOKEN, token);
                            Log.d("notification", token);
                        }
                        // Log and toast
                    });

            initListnear();

        }


    }

    private void initListnear() {
        binding.tvSignup.setOnClickListener(v -> {
            if (binding.etUserName.getText().toString().equals("")) {
                Toast.makeText(this, "Enter username first", Toast.LENGTH_SHORT).show();
                binding.etUserName.setError("Enter username first");
                return;
            }
            if (binding.etPassword.getText().toString().equals("")) {
                Toast.makeText(this, "Enter Password First", Toast.LENGTH_SHORT).show();
                binding.etUserName.setError("Enter Password First");
                return;
            }
            RetrofitService service = RetrofitBuilder.create();
            binding.pd.setVisibility(View.VISIBLE);
            Call<DeliveryBoyRoot> call = service.login(Const.DEV_KEY, binding.etUserName.getText().toString(),
                    binding.etPassword.getText().toString(), "1", token);
            call.enqueue(new Callback<DeliveryBoyRoot>() {
                @Override
                public void onResponse(Call<DeliveryBoyRoot> call, Response<DeliveryBoyRoot> response) {
                    if (response.code() == 200) {

                        Log.d("TAG", "onResponse: "+new Gson().toJson(response.body()));
                        if (response.body().getStatus() == 200 && response.body().getData() != null) {
                            DeliveryBoyRoot data = response.body();
                            sessionManager.saveUser(data);
                            sessionManager.saveBooleanValue(Const.IS_LOGIN, true);
                            Toast.makeText(LoginActivity.this, data.getData().getFullname(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));

                        } else if (response.body().getStatus() == 401) {
                            Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    binding.pd.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<DeliveryBoyRoot> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.pd.setVisibility(View.GONE);
                }
            });

        });

    }
}