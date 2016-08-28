package com.dimensionalwave.soda.handlers;

import com.badlogic.gdx.physics.box2d.*;

public class ContactListenerImpl implements ContactListener {

    private int footContactsCount = 0;

    @Override
    public void beginContact(Contact contact) {

        Fixture fA = contact.getFixtureA();
        Fixture fB = contact.getFixtureB();

        if(isEqual(fA, fB, "player_foot")) {
            footContactsCount++;
        }

    }

    @Override
    public void endContact(Contact contact) {

        Fixture fA = contact.getFixtureA();
        Fixture fB = contact.getFixtureB();

        if(isEqual(fA, fB, "player_foot")) {
            footContactsCount--;
        }

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public Boolean isPlayerOnGround() {
        return (footContactsCount > 0);
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
