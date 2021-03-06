package org.bluechat.blueninemenmoris.model;

/**
 * Created by Samsaini on 05/25/2016.
 */
public class Actor {
    //things an actor can have
    //position
    private int posx;
    private int posy;
    private int prex;
    private int prey;
    private int rmx;
    private int rmy;
    private boolean removed;
    private boolean availableToRemove;
    private boolean placed;
    private int placedIndex = -1;
    //Player that have this Actor
    private int player;
    private int number;


    public Actor(int posx, int posy, int number) {
        this.posx = posx;
        this.posy = posy;
        this.prex = posx;
        this.prey = posy;
        this.number = number;
        placed = false;
    }

    /*
     *Get player
     */
    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public int getPosy() {
        return posy;
    }

    public void setPosxy(int posx, int posy) {
        this.posx = posx;
        this.posy = posy;
    }
    public void setToPreviousPosition(){
        posx = prex;
        posy = prey;
    }
    public int getPosx() {
        return posx;
    }

    public int getPrePosy() {
        return prey;
    }
    public int getPrePosx() {
        return prex;
    }

    public void setPrePosxy(int prex, int prey) {
        this.prex = prex;
        this.prey = prey;
    }


    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
        this.setPlacedIndex(111);
        this.placed = false;
    }

    public boolean isAvailableToRemove() {
        return availableToRemove;
    }

    public void setAvailableToRemove(boolean availableToRemove) {
        this.availableToRemove = availableToRemove;
    }

    public boolean isPlaced() {
        return placed;
    }

    public void setPlaced(boolean placed) {
        this.placed = placed;
    }

    public int getPlacedIndex() {
        return placedIndex;
    }

    public void setPlacedIndex(int placedIndex) {
        this.placed = true;
        this.placedIndex = placedIndex;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
