package com.aci.student24.api.tanks;

import com.aci.student24.api.tanks.state.MapState;
import com.aci.student24.api.tanks.state.TankMove;

import java.util.List;

public interface Algorithm {
  void setMyId(int id);
  /**
   * Returns next moves for all our tanks
   *
   * @param mapState current state of field
   * @return list of moves
   */
  List<TankMove> nextMoves(MapState mapState);
}
