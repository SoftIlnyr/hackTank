package com.aci.student24.api.tanks.objects;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class Shell extends Position implements Serializable {

  private static AtomicInteger counter = new AtomicInteger(1);

  private int id, teamId;

  private byte dir;

  private long shooterId;

  private Position destructionPosition;

  private int destroyedId;

  public Shell() {}

  public Shell(int x, int y, byte dir, long shooterId) {
    super(x, y);
    this.id = counter.getAndIncrement();
    this.dir = dir;
    this.shooterId = shooterId;
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

  public long getShooterId() {
    return shooterId;
  }

  public void setShooterId(long shooterId) {
    this.shooterId = shooterId;
  }


  public Position getDestructionPosition() {
    return destructionPosition;
  }

  public void setDestructionPosition(Position destructionPosition) {
    this.destructionPosition = destructionPosition;
  }

  public int getDestroyedId() {
    return destroyedId;
  }

  public void setDestroyedId(int destroyedId) {
    this.destroyedId = destroyedId;
  }

  public int getTeamId() {
    return teamId;
  }

  public void setTeamId(int teamId) {
    this.teamId = teamId;
  }
}
