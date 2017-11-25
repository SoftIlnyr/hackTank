package com.aci.student24.api.tanks.state;

import java.io.Serializable;

public class TankMove implements Serializable {

    private int id;

    private byte dir;

    private boolean shoot;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte getDir() {
        return dir;
    }

    public void setDir(byte dir) {
        this.dir = dir;
    }

    public boolean isShoot() {
        return shoot;
    }

    public void setShoot(boolean shoot) {
        this.shoot = shoot;
    }

    public TankMove() {}

    public TankMove(int id, byte dir, boolean shoot) {
        this.id = id;
        this.dir = dir;
        this.shoot = shoot;
    }
}
