package com.example.storeclothes.ui.Admin.command;

import com.example.storeclothes.data.model.User;
import com.example.storeclothes.ui.Admin.factory.ManagerFactory;
import com.example.storeclothes.ui.Admin.manager.BaseManager;

public class DeleteUserCommand implements Command {
    private final String userId;
    private User deletedUser;
    private final BaseManager<User> userManager;

    public DeleteUserCommand(String userId) {
        this.userId = userId;
        this.userManager = ManagerFactory.createManager(ManagerFactory.ManagerType.USER);
    }

    @Override
    public void execute() {
        // Lưu user trước khi xóa để có thể hoàn tác
        userManager.getById(userId).observeForever(user -> {
            if (user != null) {
                deletedUser = user;
                userManager.delete(userId);
            }
        });
    }

    @Override
    public void undo() {
        if (deletedUser != null) {
            userManager.add(deletedUser);
        }
    }

    @Override
    public String getDescription() {
        return "Delete user with ID: " + userId;
    }
}