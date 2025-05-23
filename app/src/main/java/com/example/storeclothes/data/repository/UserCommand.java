package com.example.storeclothes.data.repository;

import com.example.storeclothes.data.model.User;

public class UserCommand implements Command {
    public enum ActionType { DELETE, UPDATE, DISABLE }
    private final User user;
    private final ActionType actionType;

    public UserCommand(User user, ActionType actionType) {
        this.user = user;
        this.actionType = actionType;
    }

    @Override
    public void execute() {
        switch (actionType) {
            case DELETE:
                // Xóa user khỏi database (giả lập)
                AuditLogManager.getInstance().addLog("Xóa người dùng: " + user.getUserId());
                break;
            case UPDATE:
                // Cập nhật user (giả lập)
                AuditLogManager.getInstance().addLog("Cập nhật người dùng: " + user.getUserId());
                break;
            case DISABLE:
                // Vô hiệu hóa user (giả lập)
                AuditLogManager.getInstance().addLog("Vô hiệu hóa người dùng: " + user.getUserId());
                break;
        }
    }

    @Override
    public String getDescription() {
        switch (actionType) {
            case DELETE: return "Xóa người dùng: " + user.getUserId();
            case UPDATE: return "Cập nhật người dùng: " + user.getUserId();
            case DISABLE: return "Vô hiệu hóa người dùng: " + user.getUserId();
            default: return "";
        }
    }
}