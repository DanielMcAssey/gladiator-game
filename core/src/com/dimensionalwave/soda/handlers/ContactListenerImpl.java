package com.dimensionalwave.soda.handlers;

import com.badlogic.gdx.physics.box2d.*;

public class ContactListenerImpl implements ContactListener {

    private Boolean isPlayerOnGround = false;

    @Override
    public void beginContact(Contact contact) {

        Fixture fA = contact.getFixtureA();
        Fixture fB = contact.getFixtureB();

        if(isEqual(fA, fB, "foot")) {
            isPlayerOnGround = true;
        }

    }

    @Override
    public void endContact(Contact contact) {

        Fixture fA = contact.getFixtureA();
        Fixture fB = contact.getFixtureB();

        if(isEqual(fA, fB, "foot")) {
            isPlayerOnGround = false;
        }

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public Boolean isPlayerOnGround() {
        return isPlayerOnGround;
    }

     private boolean isEqual(Fixture fixtureA, Fixture fixtureB, Object compareTo) {
         return (fixtureA != null && fixtureA.getUserData() != null && fixtureA.getUserData().equals(compareTo)) ||
                 (fixtureB != null && fixtureB.getUserData() != null && fixtureB.getUserData().equals(compareTo));
     }
}
