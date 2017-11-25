package com.aci.student24.api.tanks.state;

import java.io.Serializable;

public class Size implements Serializable {

    private int w,h;

    public Size() {}

    public Size(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public int getWidth() {
        return w;
    }

    public int getHeight() {
        return h;
    }

  public void setWidth(int w) {
    this.w = w;
  }

  public void setHeight(int h) {
    this.h = h;
  }
}
