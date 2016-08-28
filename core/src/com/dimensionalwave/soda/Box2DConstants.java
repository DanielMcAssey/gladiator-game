package com.dimensionalwave.soda;

public class Box2DConstants {

    public static final float PPM = 100.0f;

    public static final short CATEGORY_GROUND = 0x0001;
    public static final short CATEGORY_POWERUP = 0x0002;
    public static final short CATEGORY_PLAYER = 0x0003;
    public static final short CATEGORY_AI = 0x0004;
    public static final short CATEGORY_AI_BOSS = 0x0005;
    public static final short CATEGORY_WEAPON = 0x0006;

    public static final short MASK_PLAYER = CATEGORY_GROUND | CATEGORY_POWERUP | CATEGORY_AI | CATEGORY_AI_BOSS;
    public static final short MASK_AI = CATEGORY_GROUND | CATEGORY_PLAYER | CATEGORY_AI;
    public static final short MASK_AI_BOSS = CATEGORY_GROUND | CATEGORY_PLAYER | CATEGORY_AI;
    public static final short MASK_PLAYER_WEAPON = CATEGORY_AI | CATEGORY_AI_BOSS;
    public static final short MASK_AI_WEAPON = CATEGORY_PLAYER;
    public static final short MASK_SCENERY = -1;

}
