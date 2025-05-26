package com.example.storeclothes.ui.Customer.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.storeclothes.data.model.Order;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.data.repository.OrderRepository;

import java.util.List;
import java.util.Map;

public class OrderViewModel extends ViewModel {

    private final MutableLiveData<List<Order>> _orders = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Product>> _productMap = new MutableLiveData<>();

    public LiveData<List<Order>> orders = _orders;
    public LiveData<Map<String, Product>> productMap = _productMap;

    private final OrderRepository repository = OrderRepository.getInstance();

    public void setUserId(String userId) {
        repository.getOrdersByUserId(userId, (orderList, productIds) -> {
            _orders.setValue(orderList);

            if (productIds != null && !productIds.isEmpty()) {
                repository.getProductsByIds(productIds, _productMap::setValue);
            }
        });
    }
}
