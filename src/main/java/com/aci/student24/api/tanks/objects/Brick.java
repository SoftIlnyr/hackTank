package com.aci.student24.api.tanks.objects;

import java.io.Serializable;

public class Brick extends Position implements Serializable {

  private int id;

  public Brick() {}

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}
