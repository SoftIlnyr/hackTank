package com.aci.student24.tanks;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.aci.student24.api.tanks.Algorithm;
import com.aci.student24.api.tanks.state.MapState;
import com.aci.student24.api.tanks.state.TankMove;

public class TankPlayerRandom implements Algorithm {
  private int teamId;
  private int i = 0;

  @Override
  public void setMyId(final int id) {
    teamId = id;
  }

  @Override
  public List<TankMove> nextMoves(MapState mapState) {
    try {
      Thread.sleep(220);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      return null;
    }

    i++;
    final Random rn = new Random();

    return mapState.getTanks(teamId).stream().map(tank -> {
      int randomDir = rn.nextInt(4) + 1;
      int dir = (i % 2 == 0) ? tank.getDir() : randomDir;
      return new TankMove(tank.getId(), (byte) dir, true);
    }).collect(Collectors.toList());
  }
}
