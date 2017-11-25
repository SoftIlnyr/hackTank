package com.aci.student24.api.tanks;

import static com.aci.student24.api.tanks.SystemCommand.ERROR;
import static com.aci.student24.api.tanks.SystemCommand.FINISHED;
import static com.aci.student24.api.tanks.SystemCommand.OUT_OF_SYNC;

import com.aci.student24.api.tanks.state.MapState;
import com.aci.student24.api.tanks.state.OutputMapState;
import com.aci.student24.api.tanks.state.TankMove;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Util {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  /**
   *
   * @param s input command
   * @return type of command
   */
  public static Optional<SystemCommand> deserializeSystemCommand(final String s) {
    if (s == null || s.isEmpty()) {
      throw new IllegalArgumentException("Broken response");
    }

    if (s.startsWith(ERROR.toString())) {
      return Optional.of(ERROR);
    } else if (s.startsWith(FINISHED.toString())) {
      return Optional.of(FINISHED);
    } else if (s.startsWith(OUT_OF_SYNC.toString())) {
      return Optional.of (OUT_OF_SYNC);
    }

    return Optional.empty();
  }

  public static MapState deserializeInitialMapState(final String s) {
    if (s == null || "".equals(s)) {
      System.err.println("Received empty map state");
      return null;
    }
    try {
      return objectMapper.readValue(s, MapState.class);
    } catch (IOException e) {
      System.err.println("Cannot parse initial MapState: " + e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

  public static OutputMapState deserializeMapState(final String s) {
    if (s == null || s.isEmpty()) {
      throw new IllegalArgumentException("Received empty map state");
    }

    try {
      return objectMapper.readValue(s, OutputMapState.class);
    } catch (final Exception e) {
      System.err.println("Cannot parse OutputMapState: " + e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

  public static String serializeMoves(final List<TankMove> moves) {
    try {
      return objectMapper.writeValueAsString(moves);
    } catch (final Exception e) {
      System.err.println("Cannot serialize tank moves: " + e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

  public static String serializeUserMap(Map<Integer, String> users) {
    try {
      return objectMapper.writeValueAsString(users);
    } catch (final Exception e) {
      System.err.println("Cannot serialize user map: " + e.getMessage());
      e.printStackTrace();
    }
    return null;
  }
}
