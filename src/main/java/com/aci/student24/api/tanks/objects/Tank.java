package com.aci.student24.api.tanks.objects;

public class Tank extends Position {
  private int id, teamId;

  private boolean coolDown;

  private byte dir, oldDir;

  public Tank() {
    super();
  }

  public Tank(int x, int y) {
    super(x, y);
  }

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

  public long getTeamId() {
    return teamId;
  }

  public void setTeamId(int teamId) {
    this.teamId = teamId;
  }

  public byte getOldDir() {
    return oldDir;
  }

  public void setOldDir(byte oldDir) {
    this.oldDir = oldDir;
  }

  public boolean isCoolDown() {
    return coolDown;
  }

  public void setCoolDown(boolean coolDown) {
    this.coolDown = coolDown;
  }
}
