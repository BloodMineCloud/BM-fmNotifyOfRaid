package ru.feymer.fmnotifyofraid.manager;

import javax.inject.Provider;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class CooldownManager<ID> {
    private Provider<Duration> duration;
    private final Map<ID, LocalTime> cooldowns = new HashMap<>();

    public CooldownManager(Provider<Duration> duration) {
        this.duration = duration;
    }

    public void addCooldown(ID id) {
        cooldowns.put(id, LocalTime.now().plus(duration.get()));
    }

    public boolean hasPassed(ID id) {
        return !cooldowns.containsKey(id) || cooldowns.get(id).isBefore(LocalTime.now());
    }

    public boolean addNewCooldownIfPassed(ID id) {
        if (hasPassed(id)) {
            addCooldown(id);
            return true;
        }
        return false;
    }

    public void resetCooldown(ID id) {
        cooldowns.remove(id);
    }


}
