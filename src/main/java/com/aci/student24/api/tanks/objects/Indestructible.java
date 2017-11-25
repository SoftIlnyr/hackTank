package com.aci.student24.api.tanks.objects;

import java.io.Serializable;

public class Indestructible extends Position implements Serializable {

  private int id;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}
