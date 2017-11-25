package com.aci.student24.tanks;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.aci.student24.api.tanks.Algorithm;
import com.aci.student24.api.tanks.objects.Brick;
import com.aci.student24.api.tanks.objects.Position;
import com.aci.student24.api.tanks.objects.Tank;
import com.aci.student24.api.tanks.state.Direction;
import com.aci.student24.api.tanks.state.MapState;
import com.aci.student24.api.tanks.state.TankMove;

public class TankPlayer1 implements Algorithm {
  private int teamId;

  private List<Tank> offence;
  private List<Tank> defence;

  @Override
  public void setMyId(final int id) {
    teamId = id;
  }

  @Override
  public List<TankMove> nextMoves(MapState mapState) {
    if (offence == null) {
      offence = mapState.getTanks(teamId).stream().limit(3).collect(Collectors.toList());
    }
    if (defence == null) {
      defence = mapState.getTanks(teamId).stream().skip(3).collect(Collectors.toList());
    }
    List<TankMove> tankMoves = new ArrayList<>();
    mapState.getTanks(teamId).forEach(tank -> {
      TankMove tankMove = new TankMove(tank.getId(), Direction.DOWN, false);
      tankMoves.add(tankMove);
    });

    return tankMoves;
  }

  private TankMove stay(Tank tank) {
    return new TankMove(tank.getId(), Direction.NO, false);
  }

  private TankMove shoot(Tank tank) {
    return new TankMove(tank.getId(), Direction.NO, true);
  }

  private TankMove turn()

  private void checkTrasser() {

  }
}

