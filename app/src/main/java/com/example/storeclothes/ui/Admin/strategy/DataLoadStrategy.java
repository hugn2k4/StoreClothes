package com.example.storeclothes.ui.Admin.strategy;

import androidx.lifecycle.LiveData;
import java.util.List;

/**
 * Interface chiến lược tải dữ liệu
 * Áp dụng Strategy Pattern
 */
public interface DataLoadStrategy<T> {
    LiveData<List<T>> loadData();
    String getStrategyName();
}