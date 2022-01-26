package com.barathi.captain.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.barathi.captain.R;
import com.barathi.captain.databinding.ItemProductsBinding;
import com.barathi.captain.models.OrderDetailRoot;
import com.barathi.captain.retrofit.Const;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ItemViewHolder> {
    private List<OrderDetailRoot.ItemDetailsItem> itemDetails;

    public OrderItemAdapter(List<OrderDetailRoot.ItemDetailsItem> itemDetails) {

        this.itemDetails = itemDetails;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_products, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.setmodel(itemDetails.get(position));
    }

    @Override
    public int getItemCount() {
        return itemDetails.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ItemProductsBinding binding;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemProductsBinding.bind(itemView);
        }

        public void setmodel(OrderDetailRoot.ItemDetailsItem itemDetail) {
            binding.productName.setText(itemDetail.getName());
            binding.produxtWeight.setText(itemDetail.getUnit());
            binding.tvproductprice.setText(Const.CURRENCY + itemDetail.getPrice());
            binding.produxtQuentity.setText("Quantity " + itemDetail.getQuantity());
            Glide.with(binding.getRoot().getContext())
                    .load(Const.BASE_IMG_URL + itemDetail.getProductImage().get(0))
                    .placeholder(R.drawable.delivery_placeholder)
                    .error(R.drawable.delivery_placeholder)
                    .into(binding.imgProduct);

        }
    }
}
