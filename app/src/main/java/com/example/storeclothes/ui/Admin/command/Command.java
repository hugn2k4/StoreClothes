package com.example.storeclothes.ui.Admin.command;

/**
 * Interface cho Command Pattern
 */
public interface Command {
    void execute();
    void undo();
    String getDescription();
}