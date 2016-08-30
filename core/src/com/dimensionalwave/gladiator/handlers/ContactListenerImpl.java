package com.dimensionalwave.gladiator.handlers;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.dimensionalwave.gladiator.actors.AI.AI;
import com.dimensionalwave.gladiator.actors.Player;
import com.dimensionalwave.gladiator.actors.Powerups.Powerup;
import com.dimensionalwave.gladiator.levels.Level;

public class ContactListenerImpl implements ContactListener {

    private int footContactsCount = 0;
    private Array<Powerup> powerupsToCollect = new Array<Powerup>();

    @Override
    public void beginContact(Contact contact) {

        Fixture fA = contact.getFixtureA();
        Fixture fB = contact.getFixtureB();

        if(isEqual(fA, fB, "player_foot")) {
            footContactsCount++;
        }

        if(isEqual(fA, fB, "player_weapon", "ai_body")) {
            if(fA.getBody().getUserData() instanceof Player && fB.getBody().getUserData() instanceof AI) {
                addAiToPlayer((Player) fA.getBody().getUserData(), (AI) fB.getBody().getUserData());
            } else if (fA.getBody().getUserData() instanceof AI && fB.getBody().getUserData() instanceof Player) {
                addAiToPlayer((Player) fB.getBody().getUserData(), (AI) fA.getBody().getUserData());
            }
        }

        if(isEqual(fA, fB, "ai_weapon", "player_body")) {
            if(fA.getBody().getUserData() instanceof Player && fB.getBody().getUserData() instanceof AI) {
                addPlayerToAi((Player) fA.getBody().getUserData(), (AI) fB.getBody().getUserData());
            } else if (fA.getBody().getUserData() instanceof AI && fB.getBody().getUserData() instanceof Player) {
                addPlayerToAi((Player) fB.getBody().getUserData(), (AI) fA.getBody().getUserData());
            }
        }

        if(isEqual(fA, fB, "powerup_body")) {
            if(fA.getBody().getUserData() instanceof Powerup) {
                powerupsToCollect.add((Powerup)fA.getBody().getUserData());
            } else if (fB.getBody().getUserData() instanceof Powerup) {
                powerupsToCollect.add((Powerup)fB.getBody().getUserData());
            }
        }

        if(isEqual(fA, fB, "wall_death")) {
            if(fA.getBody().getUserData() instanceof Player) {
                ((Player)fA.getBody().getUserData()).setProperty("HEALTH", 0.0f);
            } else if (fB.getBody().getUserData() instanceof Player) {
                ((Player)fB.getBody().getUserData()).setProperty("HEALTH", 0.0f);
            }
        }

        if(isEqual(fA, fB, "warp_level_next", "player_body")) {
            if(fA.getBody().getUserData() instanceof Level) {
                ((Level)fA.getBody().getUserData()).setIsNextLevel(true);
            } else if (fB.getBody().getUserData() instanceof Level) {
                ((Level)fB.getBody().getUserData()).setIsNextLevel(true);
            }
        }
    }

    @Override
    public void endContact(Contact contact) {

        Fixture fA = contact.getFixtureA();
        Fixture fB = contact.getFixtureB();

        if(isEqual(fA, fB, "player_foot")) {
            footContactsCount--;
        }

        if(isEqual(fA, fB, "player_weapon", "ai_body")) {
            if(fA.getBody().getUserData() instanceof Player && fB.getBody().getUserData() instanceof AI) {
                removeAiFromPlayer((Player) fA.getBody().getUserData(), (AI) fB.getBody().getUserData());
            } else if (fA.getBody().getUserData() instanceof AI && fB.getBody().getUserData() instanceof Player) {
                removeAiFromPlayer((Player) fB.getBody().getUserData(), (AI) fA.getBody().getUserData());
            }
        }

        if(isEqual(fA, fB, "ai_weapon", "player_body")) {
            if(fA.getBody().getUserData() instanceof Player && fB.getBody().getUserData() instanceof AI) {
                removePlayerFromAi((AI) fB.getBody().getUserData());
            } else if (fA.getBody().getUserData() instanceof AI && fB.getBody().getUserData() instanceof Player) {
                removePlayerFromAi((AI) fA.getBody().getUserData());
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public Array<Powerup> getPowerupsToCollect() {
        return powerupsToCollect;
    }

    public Boolean isPlayerOnGround() {
        return (footContactsCount > 0);
    }

    private void addAiToPlayer(Player player, AI ai) {
        player.addContact(ai);
    }

    private void addPlayerToAi(Player player, AI ai) {
        ai.addContact(player);
    }

    private void removeAiFromPlayer(Player player, AI ai) {
        player.removeContact(ai);
    }

    private void removePlayerFromAi(AI ai) {
        ai.removeContact();
    }

    private boolean isEqual(Fixture fixtureA, Fixture fixtureB, Object compareTo) {
        return (fixtureA != null && fixtureA.getUserData() != null && fixtureA.getUserData().equals(compareTo)) ||
                (fixtureB != null && fixtureB.getUserData() != null && fixtureB.getUserData().equals(compareTo));
    }

    private boolean isEqual(Fixture fixtureA, Fixture fixtureB, Object compareToA, Object compareToB) {
        return ((fixtureA != null && fixtureA.getUserData() != null && fixtureA.getUserData().equals(compareToA)) &&
                (fixtureB != null && fixtureB.getUserData() != null && fixtureB.getUserData().equals(compareToB))) ||
                ((fixtureA != null && fixtureA.getUserData() != null && fixtureA.getUserData().equals(compareToB)) &&
                        (fixtureB != null && fixtureB.getUserData() != null && fixtureB.getUserData().equals(compareToA)));
    }
}
