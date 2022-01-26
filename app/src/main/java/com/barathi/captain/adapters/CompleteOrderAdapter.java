package com.barathi.captain.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.barathi.captain.R;
import com.barathi.captain.activites.OrderDetailActivity;
import com.barathi.captain.databinding.ItemOrderlistBinding;
import com.barathi.captain.models.Address;
import com.barathi.captain.models.CompleteOrderRoot;
import com.barathi.captain.retrofit.Const;

import java.util.ArrayList;
import java.util.List;

public class CompleteOrderAdapter extends RecyclerView.Adapter<CompleteOrderAdapter.OrderViewHolder> {
    private List<CompleteOrderRoot.Datum> data = new ArrayList<>();
    private Context context;
    private Address.Datum addressObj;


    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_orderlist, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Log.d("TAG", "onBindViewHolder: " + position + data.get(position).getOrderId());
        holder.setModel(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addData(List<CompleteOrderRoot.Datum> data) {
        for(int i = 0; i < data.size(); i++) {
            this.data.add(data.get(i));
            notifyItemInserted(this.data.size() - 1);
        }

    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        ItemOrderlistBinding binding;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemOrderlistBinding.bind(itemView);
        }

        public void setModel(CompleteOrderRoot.Datum datum) {
            binding.lytDistance.setVisibility(View.INVISIBLE);
            binding.orderid.setText("#" + datum.getOrderId());
            String addressStr = datum.getAddress();
            if(addressStr != null && !addressStr.equals("")) {
                try {
                    addressObj = new Gson().fromJson(addressStr, Address.Datum.class);
                    if(addressObj != null) {

                        binding.tvaddress1.setText(addressObj.getArea().concat(", ").concat(addressObj.getCity()));
                        binding.tvaddress2.setText(addressObj.getHomeNo().concat(", ").concat(addressObj.getSociety() + " " + addressObj.getStreet()
                                + " \n" + addressObj.getArea() + " " + addressObj.getLandmark() + " " + addressObj.getPincode()));


                        binding.tvCustomername.setText(addressObj.getFirstName().concat(" " + addressObj.getLastName()));
                    }
                } catch(Exception o) {
                    Log.d("TAG", "setModel: " + o.toString());
                }
            }


            Glide.with(binding.getRoot().getContext())
                    .load(Const.BASE_IMG_URL + datum.getProfileImage())
                    .placeholder(R.drawable.delivery_placeholder)
                    .error(R.drawable.delivery_placeholder)
                    .circleCrop()
                    .into(binding.imgCustomer);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("uid", datum.getUserId());
                intent.putExtra("oid", datum.getOrderId());
                intent.putExtra("from", "complete");
                context.startActivity(intent);
            });
            binding.imgarrow.setOnClickListener(v -> {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("uid", datum.getUserId());
                intent.putExtra("oid", datum.getOrderId());
                intent.putExtra("from", "complete");
                context.startActivity(intent);
            });

        }
    }
}
