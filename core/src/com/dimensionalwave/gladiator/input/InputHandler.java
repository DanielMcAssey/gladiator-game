package com.dimensionalwave.gladiator.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class InputHandler extends InputAdapter {

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.UP:
                InputManager.setCurrentKeys(InputAction.MENU_UP, true);
                break;
            case Input.Keys.DOWN:
                InputManager.setCurrentKeys(InputAction.MENU_DOWN, true);
                break;
            case Input.Keys.ENTER:
                InputManager.setCurrentKeys(InputAction.MENU_ENTER, true);
                break;
            case Input.Keys.A:
                InputManager.setCurrentKeys(InputAction.MOVE_LEFT, true);
                break;
            case Input.Keys.D:
                InputManager.setCurrentKeys(InputAction.MOVE_RIGHT, true);
                break;
            case Input.Keys.R:
                InputManager.setCurrentKeys(InputAction.ACTION_BLOCK, true);
                break;
            case Input.Keys.F:
                InputManager.setCurrentKeys(InputAction.ACTION_ATTACK, true);
                break;
            case Input.Keys.SPACE:
                InputManager.setCurrentKeys(InputAction.ACTION_JUMP, true);
                break;
        }

        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.UP:
                InputManager.setCurrentKeys(InputAction.MENU_UP, false);
                break;
            case Input.Keys.DOWN:
                InputManager.setCurrentKeys(InputAction.MENU_DOWN, false);
                break;
            case Input.Keys.ENTER:
                InputManager.setCurrentKeys(InputAction.MENU_ENTER, false);
                break;
            case Input.Keys.A:
                InputManager.setCurrentKeys(InputAction.MOVE_LEFT, false);
                break;
            case Input.Keys.D:
                InputManager.setCurrentKeys(InputAction.MOVE_RIGHT, false);
                break;
            case Input.Keys.R:
                InputManager.setCurrentKeys(InputAction.ACTION_BLOCK, false);
                break;
            case Input.Keys.F:
                InputManager.setCurrentKeys(InputAction.ACTION_ATTACK, false);
                break;
            case Input.Keys.SPACE:
                InputManager.setCurrentKeys(InputAction.ACTION_JUMP, false);
                break;
        }

        return super.keyUp(keycode);
    }
}
