package com.aci.student24.api.tanks.objects;

import java.io.Serializable;

public class Base extends Position implements Serializable {

  private int id, teamId;

  private byte dir;

  public Base() {}

  public int getId() {
    return id;
  }

  public int getTeamId() {
    return teamId;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setTeamId(int teamId) {
    this.teamId = teamId;
  }

  public byte getDir() {
    return dir;
  }

  public void setDir(byte dir) {
    this.dir = dir;
  }


}
