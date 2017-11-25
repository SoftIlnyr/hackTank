package com.aci.student24.api.tanks.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Position {

    private int x,y;

  public Position() {}

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    @JsonIgnore
    public Position getPosition() {
        return new Position(x,y);
    }

    @Override
    public boolean equals(Object pos) {
        if (pos instanceof Position) {
            Position position = (Position)pos;
            return x == position.getX() && y == position.getY();
        }
        return false;
    }
}
