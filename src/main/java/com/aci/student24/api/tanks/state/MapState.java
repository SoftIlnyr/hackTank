package com.aci.student24.api.tanks.state;

import com.aci.student24.api.tanks.objects.Base;
import com.aci.student24.api.tanks.objects.Brick;
import com.aci.student24.api.tanks.objects.Indestructible;
import com.aci.student24.api.tanks.objects.Shell;
import com.aci.student24.api.tanks.objects.Tank;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class MapState implements Serializable {

  private Size size;

  private List<Tank> tanks;

  private List<Shell> shells;

  private List<Base> bases;

  private List<Brick> bricks;

  private List<Indestructible> indestructibles;

  public MapState() {}

  public List<Shell> getShells() {
    return shells;
  }

  public Size getSize() {
    return size;
  }

  public void setSize(Size size) {
    this.size = size;
  }

  public void setTanks(List<Tank> tanks) {
    this.tanks = tanks;
  }


  public List<Tank> getTanks() {
    return tanks;
  }

  public void setShells(List<Shell> shells) {
    this.shells = shells;
  }

  public Tank getTank(long id) {
    List<Tank> res = tanks.stream().filter(t -> t.getId() == id).collect(Collectors.toList());
    if (res.size() > 0) {
      return res.get(0);
    }
    return null;
  }

  public List<Tank> getTanks(long teamId) {
    return tanks.stream().filter(t -> t.getTeamId() == teamId).collect(Collectors.toList());
  }

  public List<Base> getBases() {
    return bases;
  }

  public void setBases(List<Base> bases) {
    this.bases = bases;
  }

  public List<Brick> getBricks() {
    return bricks;
  }

  public void setBricks(List<Brick> bricks) {
    this.bricks = bricks;
  }

  public List<Indestructible> getIndestructibles() {
    return indestructibles;
  }

  public void setIndestructibles(List<Indestructible> indestructibles) {
    this.indestructibles = indestructibles;
  }
}
