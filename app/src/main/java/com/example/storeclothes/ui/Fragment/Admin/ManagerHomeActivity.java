package com.example.storeclothes.ui.Fragment.Admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.storeclothes.R;
import com.example.storeclothes.ui.Fragment.Admin.DashboardFragment;
import com.example.storeclothes.ui.Fragment.Admin.UsersFragment;
import com.example.storeclothes.ui.Fragment.Admin.ProductsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class ManagerHomeActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_home);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        setupViewPager();
    }

    private void setupViewPager() {
        List<androidx.fragment.app.Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new DashboardFragment());
        fragmentList.add(new UsersFragment());
        fragmentList.add(new ProductsFragment());

        ViewPagerAdapter adapter = new ViewPagerAdapter(this, fragmentList);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Dashboard");
                            break;
                        case 1:
                            tab.setText("Users");
                            break;
                        case 2:
                            tab.setText("Products");
                            break;
                    }
                }
        ).attach();
    }
}
