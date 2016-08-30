package com.dimensionalwave.gladiator.actors.AI;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.dimensionalwave.gladiator.actors.Actor;
import com.dimensionalwave.gladiator.actors.Player;
import com.dimensionalwave.gladiator.handlers.ContentManager;
import com.dimensionalwave.gladiator.helpers.CharacterAction;

public abstract class AI extends Actor {

    protected boolean isRemovable = false;
    protected boolean isHit = false;
    protected float hitAmount = 0.0f;
    protected Player inContact = null;
    protected Player target = null;
    protected boolean isKillTaken = false;

    public AI(ContentManager contentManager, World newWorld, Vector2 startPosition, String newName) {
        super(contentManager, newWorld, startPosition, newName);
    }

    public void doDamage(float damageAmount) {
        if(!isDead()) {
            float trueDamage = damageAmount;
            if(((CharacterAction)getProperty("ACTION")).equals(CharacterAction.BLOCK)) {
                trueDamage = trueDamage / 2f;
            }

            if(getAiHealth() - trueDamage <= 0f) {
                setProperty("HEALTH", 0f);
            } else {
                setProperty("HEALTH", getAiHealth() - trueDamage);
            }

            isHit = true;
        }
    }

    public boolean isKillTaken() {
        if(!isKillTaken) {
            isKillTaken = true;
            return false;
        }

        return isKillTaken;
    }

    public void addContact(Player player) {
        inContact = player;
    }

    public void removeContact() {
        inContact = null;
    }

    public Player getTarget() {
        return target;
    }

    public void setTarget(Player player) {
        target = player;
    }

    public float getAiHealth() {
        return (Float) getProperty("HEALTH");
    }

    public boolean isDead() {
        return (getAiHealth() <= 0.0f);
    }

    public boolean isRemovable() {
        return isRemovable;
    }

}
