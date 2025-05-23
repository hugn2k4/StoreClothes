package com.example.storeclothes.ui.Fragment.Cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private List<Address> addressList;
    private OnAddressClickListener listener;
    public interface OnAddressClickListener {
        void onAddressClick(Address address);
    }
    public AddressAdapter(List<Address> addressList, OnAddressClickListener listener) {
        this.addressList = addressList;
        this.listener = listener;
    }
    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.bind(address);
    }
    @Override
    public int getItemCount() {
        return addressList.size();
    }
    public class AddressViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAddress;
        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAddress = itemView.findViewById(R.id.tvAddress);
        }
        public void bind(final Address address) {
            tvAddress.setText(address.getAddress());
            itemView.setOnClickListener(v -> listener.onAddressClick(address));
        }
    }
    public void addAddress(Address address) {
        addressList.add(address);
        notifyItemInserted(addressList.size() - 1);
    }
    public void updateList(List<Address> addressList) {
        this.addressList = addressList;
        notifyDataSetChanged();
    }
}