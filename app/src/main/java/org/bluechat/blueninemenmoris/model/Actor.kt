package org.bluechat.blueninemenmoris.model

/**
 * Created by Samsaini on 05/25/2016.
 */
class Actor(posx: Int, posy: Int, var number: Int) {
    //things an actor can have
    //position
    var posx: Int = 0
        private set
    var posy: Int = 0
        private set
    var prePosx: Int = 0
        private set
    var prePosy: Int = 0
        private set
    private val rmx: Int = 0
    private val rmy: Int = 0
    var isRemoved: Boolean = false
        set(removed) {
            field = removed
            this.placedIndex = 111
            this.isPlaced = false
        }
    var isAvailableToRemove: Boolean = false
    var isPlaced: Boolean = false
    var placedIndex = -1
        set(placedIndex) {
            this.isPlaced = true
            field = placedIndex
        }
    //Player that have this Actor
    /*
     *Get player
     */
    var player: Int = 0


    init {
        this.posx = posx
        this.posy = posy
        this.prePosx = posx
        this.prePosy = posy
        isPlaced = false
    }

    fun setPosxy(posx: Int, posy: Int) {
        this.posx = posx
        this.posy = posy
    }

    fun setToPreviousPosition() {
        posx = prePosx
        posy = prePosy
    }

    fun setPrePosxy(prex: Int, prey: Int) {
        this.prePosx = prex
        this.prePosy = prey
    }
}
