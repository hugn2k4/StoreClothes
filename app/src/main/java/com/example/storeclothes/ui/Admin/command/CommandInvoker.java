package com.example.storeclothes.ui.Admin.command;

import java.util.Stack;

/**
 * Invoker cho Command Pattern
 */
public class CommandInvoker {
    private static CommandInvoker instance;
    private final Stack<Command> commandHistory;

    private CommandInvoker() {
        commandHistory = new Stack<>();
    }

    public static synchronized CommandInvoker getInstance() {
        if (instance == null) {
            instance = new CommandInvoker();
        }
        return instance;
    }

    public void executeCommand(Command command) {
        command.execute();
        commandHistory.push(command);
    }

    public void undoLastCommand() {
        if (!commandHistory.isEmpty()) {
            Command lastCommand = commandHistory.pop();
            lastCommand.undo();
        }
    }

    public boolean canUndo() {
        return !commandHistory.isEmpty();
    }

    public String getLastCommandDescription() {
        if (!commandHistory.isEmpty()) {
            return commandHistory.peek().getDescription();
        }
        return "No commands executed";
    }
}