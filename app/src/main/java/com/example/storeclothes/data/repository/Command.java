package com.example.storeclothes.data.repository;

public interface Command {
    void execute();
    String getDescription();
}