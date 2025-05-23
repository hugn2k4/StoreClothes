package com.example.storeclothes.data.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuditLogManager {
    private static AuditLogManager instance;
    private final List<String> auditLogs;
    private final List<AuditLogObserver> observers;

    private AuditLogManager() {
        auditLogs = new ArrayList<>();
        observers = new ArrayList<>();
    }

    public static synchronized AuditLogManager getInstance() {
        if (instance == null) {
            instance = new AuditLogManager();
        }
        return instance;
    }

    public void addLog(String log) {
        auditLogs.add(0, log); // Thêm log mới lên đầu danh sách
        notifyObservers();
    }

    public List<String> getAuditLogs() {
        return Collections.unmodifiableList(auditLogs);
    }

    public void addObserver(AuditLogObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(AuditLogObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (AuditLogObserver observer : observers) {
            observer.onAuditLogChanged();
        }
    }

    public interface AuditLogObserver {
        void onAuditLogChanged();
    }
}