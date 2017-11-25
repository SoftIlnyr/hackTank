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

    //стоять
    private TankMove stay(Tank tank) {
        return new TankMove(tank.getId(), Direction.NO, false);
    }

    //стрелять
    private TankMove shoot(Tank tank) {
        return new TankMove(tank.getId(), Direction.NO, true);
    }

    private void checkTrasser() {

    }

    private int[][] matrix;
//0-green
//1-blue
//2-red
//3-white
//4-black
//51-tank our
//52-tank not our
//6-brick
//7-block
//81-base our
//82-base not our

    private void initMatrix(MapState map) {
        matrix = new int[map.getSize().getHeight()][map.getSize().getWidth()];
        for (int x = 0; x < map.getSize().getHeight(); x++) {
            for (int y = 0; y < map.getSize().getWidth(); y++) {
                matrix[x][y] = Color.WHITE;
            }
        }
    }

    private void setBricks(MapState map) {
        for (Brick brick : map.getBricks()) {
            matrix[brick.getX()][brick.getY()] = 6;
        }
    }

    private void setBlock(List<Position> list, int type) {
        for (Position position : list) {
            matrix[position.getX()][position.getY()] = type;
        }
    }

    private void setFireLine(MapState map) {
        map.getShells();
    }
}

class Color {
    public static final byte WHITE = 0; //безопасно
    public static final byte BLUE = 1; //линия огн
    public static final byte RED = 2; //смертельная опасность
    public static final byte GREY = 3; //блок (ращрушаемый)
    public static final byte BLACK = 3; //блок (неращрушаемый)


}