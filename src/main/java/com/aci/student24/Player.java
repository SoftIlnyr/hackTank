package com.aci.student24;

import com.aci.student24.api.tanks.Algorithm;
import com.aci.student24.api.tanks.SystemCommand;
import com.aci.student24.api.tanks.Util;
import com.aci.student24.api.tanks.state.MapState;
import com.aci.student24.api.tanks.state.OutputMapState;
import com.aci.student24.api.tanks.state.TankMove;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Sample class, to show how your Algorithm will be executed.
 */
public class Player {
  private final int id;
  private final Algorithm algorithm;

  public Player(final int id) {
    this.id = id;
    algorithm = new Algorithm() { //There are a couple of examples in the project
      public void setMyId(int id) {
        //TODO
      }

      public List<TankMove> nextMoves(MapState mapState) {
        //TODO
        return null;
      }
    };
  }

  public void doPlay() {
    try {
      MapState map = Util.deserializeInitialMapState(readMsg());
      while (map != null) {
        map = move(map);
      }
      System.out.println("Algorithm has finished (move is empty)");
    } catch (final Exception e) {
      System.err.println("Algorithm has failed: " + e.getMessage());
      e.printStackTrace();
      System.err.flush();
    }
  }

  private MapState move(final MapState map) throws IOException {
    final List<TankMove> moves = algorithm.nextMoves(map);
    sendMsg(Util.serializeMoves(moves));

    final String message = readMsg();
    Optional<SystemCommand> systemCommand = Util.deserializeSystemCommand(message);
    if (systemCommand.isPresent()) {
      handleSystemCommand(systemCommand.get());
      return null;
    }
    return handleUpdatedMap(message);
  }

  /**
   *
   * @param command
   * @return true - game should be finished, false - proceed with game
   */
  private boolean handleSystemCommand(SystemCommand command) {
    switch (command) {
      case FINISHED:
        System.out.println("Game is finished. Exiting!");
        break;
      case ERROR:
        System.err.println("Referee error. Exiting!");
        break;
      default:
        System.err.println("Unknown command");
        break;
    }
    return true;
  }

  private MapState handleUpdatedMap(final String message) throws IOException {
    final OutputMapState mapState = Util.deserializeMapState(message);
    return mapState.getMap();
  }

  private void sendMsg(final String s) {
    //do nothing, as there is no Server locally
  }

  private String readMsg() {
    //TODO: i.e., read line from log (JSON)
    return null;
  }

  public static void main(String[] args) {
    new Player(1).doPlay();
  }
}
