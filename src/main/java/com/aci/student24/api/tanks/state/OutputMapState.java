package com.aci.student24.api.tanks.state;


import com.aci.student24.api.tanks.objects.Base;
import com.aci.student24.api.tanks.objects.Brick;
import com.aci.student24.api.tanks.objects.Shell;
import com.aci.student24.api.tanks.objects.Tank;

import java.io.Serializable;
import java.util.List;

public class OutputMapState implements Serializable {

  private boolean done;
  private MapState map;
  private List<Shell> shellsOut, shellsDestroyed;
  private List<Tank> tanksDestroyed;
  private List<Brick> bricksDestroyed;
  private List<Base> basesDestroyed;

  public OutputMapState() {}

  public OutputMapState(MapState map, List<Shell> shellsOut, List<Shell> shellsDestroyed,
                        List<Tank> tanksDestroyed) {
    this.map = map;
    this.shellsOut = shellsOut;
    this.shellsDestroyed = shellsDestroyed;
    this.tanksDestroyed = tanksDestroyed;
  }

  public MapState getMap() {
    return map;
  }

  public List<Shell> getShellsOut() {
    return shellsOut;
  }

  public List<Shell> getShellsDestroyed() {
    return shellsDestroyed;
  }

  public List<Tank> getTanksDestroyed() {
    return tanksDestroyed;
  }

  public boolean isDone() {
    return done;
  }

  public void setDone(boolean done) {
    this.done = done;
  }

  public void setMap(MapState map) {
    this.map = map;
  }

  public void setShellsOut(List<Shell> shellsOut) {
    this.shellsOut = shellsOut;
  }

  public void setShellsDestroyed(List<Shell> shellsDestroyed) {
    this.shellsDestroyed = shellsDestroyed;
  }

  public void setTanksDestroyed(List<Tank> tanksDestroyed) {
    this.tanksDestroyed = tanksDestroyed;
  }

  public List<Brick> getBricksDestroyed() {
    return bricksDestroyed;
  }

  public void setBricksDestroyed(List<Brick> bricksDestroyed) {
    this.bricksDestroyed = bricksDestroyed;
  }

  public List<Base> getBasesDestroyed() {
    return basesDestroyed;
  }

  public void setBasesDestroyed(List<Base> basesDestroyed) {
    this.basesDestroyed = basesDestroyed;
  }
}
