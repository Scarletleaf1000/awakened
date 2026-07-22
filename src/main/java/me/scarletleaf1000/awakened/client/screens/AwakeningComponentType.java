package me.scarletleaf1000.awakened.client.screens;

public enum AwakeningComponentType {
    TRIGGER("Trigger", "Choose when this command activates."),
    ACTION("Action", "Choose what this command does."),
    TARGET("Target", "Choose who this command affects.");

    private final String displayName;
    private final String description;

    AwakeningComponentType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String displayName() {
        return displayName;
    }

    public String description() {
        return description;
    }
}
