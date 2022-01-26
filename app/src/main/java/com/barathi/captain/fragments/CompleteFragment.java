package com.barathi.captain.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.barathi.captain.R;
import com.barathi.captain.SessionManager;
import com.barathi.captain.adapters.CompleteOrderAdapter;
import com.barathi.captain.databinding.FragmentCompleteBinding;
import com.barathi.captain.models.CompleteOrderRoot;
import com.barathi.captain.retrofit.Const;
import com.barathi.captain.retrofit.RetrofitBuilder;
import com.barathi.captain.retrofit.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CompleteFragment extends Fragment {

    FragmentCompleteBinding binding;
    SessionManager sessionManager;
    private String token;
    CompleteOrderAdapter completeOrderAdapter = new CompleteOrderAdapter();
    private boolean isLoding = false;
    private int start = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_complete, container, false);


        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sessionManager = new SessionManager(getActivity());
        if(sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            token = sessionManager.getUser().getData().getToken();
            binding.shimmer.startShimmer();
            binding.rvOrders.setAdapter(completeOrderAdapter);

            getData();
            initListnear();
        }
    }

    private void initListnear() {
        binding.rvOrders.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!binding.rvOrders.canScrollVertically(1)) {
                    LinearLayoutManager manager = (LinearLayoutManager) binding.rvOrders.getLayoutManager();

                    int visibleItemcount = manager.getChildCount();
                    int totalitem = manager.getItemCount();
                    int firstvisibleitempos = manager.findFirstCompletelyVisibleItemPosition();

                    if(!isLoding && (visibleItemcount + firstvisibleitempos >= totalitem) && firstvisibleitempos >= 0) {

                        start = start + Const.LIMIT;
                        binding.pd2.setVisibility(View.VISIBLE);
                        getData();
                    }
                }
            }
        });


    }

    private void getData() {
        isLoding = true;
        binding.lyt404.setVisibility(View.GONE);
        RetrofitService service = RetrofitBuilder.create();
        Call<CompleteOrderRoot> call = service.getCompleteOrders(Const.DEV_KEY, token, start, Const.LIMIT);
        call.enqueue(new Callback<CompleteOrderRoot>() {
            @Override
            public void onResponse(Call<CompleteOrderRoot> call, Response<CompleteOrderRoot> response) {
                if(response.code() == 200) {
                    isLoding = false;
                    if(response.body().getStatus() == 200 && !response.body().getData().isEmpty()) {
                        List<CompleteOrderRoot.Datum> data = response.body().getData();
                        completeOrderAdapter.addData(data);
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
            public void onFailure(Call<CompleteOrderRoot> call, Throwable t) {
                binding.lyt404.setVisibility(View.VISIBLE);
            }
        });
    }
}