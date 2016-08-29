package com.dimensionalwave.gladiator.input;

import java.util.HashMap;

public class InputManager {

    public static HashMap<InputAction, Boolean> currentKeys;
    public static HashMap<InputAction, Boolean> previousKeys;

    static {
        currentKeys = new HashMap<InputAction, Boolean>();
        previousKeys = new HashMap<InputAction, Boolean>();

        for(InputAction action: InputAction.values()) {
            currentKeys.put(action, false);
            previousKeys.put(action, false);
        }
    }

    public static void update() {
        for(InputAction key: currentKeys.keySet()) {
            previousKeys.put(key, currentKeys.get(key));
        }
    }

    public static void setCurrentKeys(InputAction action, Boolean value) {
        currentKeys.put(action, value);
    }

    public static boolean isDown(InputAction action) {
        return currentKeys.get(action);
    }

    public static boolean isPressed(InputAction action) {
        return currentKeys.get(action) && !previousKeys.get(action);
    }

    public static boolean isReleased(InputAction action) {
        return !currentKeys.get(action) && previousKeys.get(action);
    }
}
