package me.scarletleaf1000.awakened.breath;

/**
 * Player-attached Breath resource capability.
 */
public interface IBreath {
    int getBreath();

    void setBreath(int amount);

    void addBreath(int amount);

    void removeBreath(int amount);
}
