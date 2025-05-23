package com.example.storeclothes.ui.Manager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.storeclothes.R;
import com.example.storeclothes.data.repository.AuditLogManager;
import java.util.List;

public class AuditLogFragment extends Fragment implements AuditLogManager.AuditLogObserver {
    private ListView listView;
    private ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audit_log, container, false);
        listView = view.findViewById(R.id.listViewAuditLog);
        List<String> logs = AuditLogManager.getInstance().getAuditLogs();
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, logs);
        listView.setAdapter(adapter);
        AuditLogManager.getInstance().addObserver(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AuditLogManager.getInstance().removeObserver(this);
    }

    @Override
    public void onAuditLogChanged() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                adapter.clear();
                adapter.addAll(AuditLogManager.getInstance().getAuditLogs());
                adapter.notifyDataSetChanged();
            });
        }
    }
}