package com.aci.student24.tanks;

import java.util.ArrayList;
import java.util.List;

import com.aci.student24.api.tanks.Algorithm;
import com.aci.student24.api.tanks.objects.Base;
import com.aci.student24.api.tanks.objects.Position;
import com.aci.student24.api.tanks.objects.Tank;
import com.aci.student24.api.tanks.state.Direction;
import com.aci.student24.api.tanks.state.MapState;
import com.aci.student24.api.tanks.state.TankMove;

public class TankPlayer1 implements Algorithm {
    private int teamId;

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

    @Override
    public void setMyId(final int id) {
        teamId = id;
    }

    @Override
    public List<TankMove> nextMoves(MapState mapState) {
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

    private void initMatrix(MapState map) {
        matrix = new int[map.getSize().getHeight()][map.getSize().getWidth()];
        for (int x = 0; x < map.getSize().getHeight(); x++) {
            for (int y = 0; y < map.getSize().getWidth(); y++) {
                matrix[x][y] = State.GREEN;
            }
        }
//brick
        for (Position position : map.getBricks()) {
            matrix[position.getX()][position.getY()] = State.BRICK;
        }

//block
        for (Position position : map.getIndestructibles()) {
            matrix[position.getX()][position.getY()] = State.BLOCK;
        }

//Base
        for (Base base : map.getBases()) {
            if (base.getId() == teamId) {
                matrix[base.getX()][base.getY()] = State.BASE_ALLY;
            } else {
                matrix[base.getX()][base.getY()] = State.BASE_ENEMY;
            }
        }

        setTanks(map);
        setFireLine(map);
    }

    private void setTanks(MapState mapState) {
        mapState.getTanks().forEach(tank -> {
            if (tank.getId() == teamId) {
                matrix[tank.getX()][tank.getY()] = State.TANK_ALLY;
            } else {
                matrix[tank.getX()][tank.getY()] = State.TANK_ENEMY;
                int iterx = 0; //смещение по x
                int itery = 0; //смещение по y
                switch (tank.getDir()) {
                    case Direction.UP:
                        itery = -1;
                        break;
                    case Direction.DOWN:
                        itery = 1;
                        break;
                    case Direction.LEFT:
                        iterx = -1;
                    case Direction.RIGHT:
                        iterx = 1;
                        break;
                }
                int pointx = tank.getX() + iterx;
                int pointy = tank.getY() + itery;
                int danger = 3;
                while (validShell(pointx + iterx, pointy + itery) && danger > 0) {
                    matrix[pointx][pointy] = State.RED;
                    pointx += iterx;
                    pointy += itery;
                    danger--;
                }
            }
        });
    }


    private void setFireLine(MapState mapState) {
        mapState.getShells().forEach(shell -> {
            int iterx = 0; //смещение по x
            int itery = 0; //смещение по y
            switch (shell.getDir()) {
                case Direction.UP:
                    itery = -1;
                    break;
                case Direction.DOWN:
                    itery = 1;
                    break;
                case Direction.LEFT:
                    iterx = -1;
                case Direction.RIGHT:
                    iterx = 1;
                    break;
            }
            int pointx = shell.getX() + iterx;
            int pointy = shell.getY() + itery;
            int danger = 6;
            while (validShell(pointx + iterx, pointy + itery)) {
                matrix[pointx][pointy] = State.BLUE;
                if (danger > 0) {
                    matrix[pointx][pointy] = State.RED;
                }
                pointx += iterx;
                pointy += itery;
                danger--;
            }
        });
    }

    private boolean validShell(int pointx, int pointy) {
        return pointx >= 0 && pointx < matrix.length && pointy >= 0 && pointy < matrix[0].length;
    }


}


class State {
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
    public static final byte GREEN = 0; //безопасно
    public static final byte BLUE = 1; //линия огн
    public static final byte RED = 2; //смертельная опасность
    public static final byte WHITE = 3; //блок (ращрушаемый)
    public static final byte BLACK = 4;
    public static final byte TANK_ALLY = 51;
    public static final byte TANK_ENEMY = 52;
    public static final byte BRICK = 6;
    public static final byte BLOCK = 7;
    public static final byte BASE_ALLY = 81;
    public static final byte BASE_ENEMY = 82;


}