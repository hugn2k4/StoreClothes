package com.example.storeclothes.ui.Strategy;

import android.app.Dialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import androidx.datastore.core.DataStore;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.User;
import com.example.storeclothes.factory.EntityFactory;

public class UserActionStrategy implements EntityActionStrategy {
    private final Context context;
    private final EntityFactory userFactory;

    public UserActionStrategy(Context context) {
        this.context = context;
        this.userFactory = new EntityFactory.UserFactory();
    }

    @Override
    public void add(Entity entity) {
        DataStore.getInstance().getUsers().add(entity);
        Toast.makeText(context, "User added", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void edit(Entity entity) {
        User user = (User) entity;
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_add_user);

        EditText etFirstName = dialog.findViewById(R.id.etFirstName);
        EditText etLastName = dialog.findViewById(R.id.etLastName);
        EditText etEmail = dialog.findViewById(R.id.etEmail);
        EditText etPassword = dialog.findViewById(R.id.etPassword);
        etFirstName.setText(user.getFirstName());
        etLastName.setText(user.getLastName());
        etEmail.setText(user.getEmail());
        etPassword.setText(user.getPassword());

        dialog.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString();
            String lastName = etLastName.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            if (!firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email);
                user.setPassword(password);
                Toast.makeText(context, "User updated", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void delete(Entity entity) {
        DataStore.getInstance().getUsers().remove(entity);
        Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toggleStatus(Entity entity) {
        User user = (User) entity;
        user.setIsVerified(!user.getIsVerified());
        Toast.makeText(context, "User verification toggled", Toast.LENGTH_SHORT).show();
    }
}