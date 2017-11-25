package com.aci.student24.tanks;

import java.util.List;
import java.util.stream.Collectors;

import com.aci.student24.api.tanks.Algorithm;
import com.aci.student24.api.tanks.state.MapState;
import com.aci.student24.api.tanks.state.TankMove;

public class TankPlayer implements Algorithm {
  private int teamId;

  @Override
  public void setMyId(final int id) {
    teamId = id;
  }

  @Override
  public List<TankMove> nextMoves(MapState mapState) {
    return mapState.getTanks(teamId).stream()
        .map(tank -> new TankMove(tank.getId(), tank.getDir(), true)).collect(Collectors.toList());
  }
}
