package com.aci.student24.tanks;

import java.util.*;

import com.aci.student24.api.tanks.Algorithm;
import com.aci.student24.api.tanks.objects.Base;
import com.aci.student24.api.tanks.objects.Position;
import com.aci.student24.api.tanks.objects.Tank;
import com.aci.student24.api.tanks.state.Direction;
import com.aci.student24.api.tanks.state.MapState;
import com.aci.student24.api.tanks.state.TankMove;
import javafx.geometry.Pos;

public class TankPlayer1 implements Algorithm {
    private int teamId;

    private int[][] matrix;

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
                while (validPoint(pointx + iterx, pointy + itery) && danger > 0) {
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
            while (validPoint(pointx + iterx, pointy + itery)) {
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

    private boolean validPoint(int pointx, int pointy) {
        return validBorder(pointx, pointy) && matrix[pointx][pointy] != State.RED
                && matrix[pointx][pointy] != State.BLOCK && matrix[pointx][pointy] != State.BASE_ALLY;
    }

    private boolean validBorder(int pointx, int pointy) {
        return pointx >= 0 && pointx < matrix.length && pointy >= 0 && pointy < matrix[0].length
    }

    private TankMove getNextMoveForTank(Tank tank, Position finishPosition) {
        Stack<Position> positions = findPath(tank, finishPosition.getX(), finishPosition.getY());
        Position position = positions.peek();
        boolean shoot= false;
        byte bufDir = 0;
        if (tank.getX() - position.getX() != 0) {
            if (tank.getX() - position.getX() > 0) {
                bufDir = Direction.LEFT;
            } else if (tank.getX() - position.getX() < 0) {
                bufDir = Direction.RIGHT;
            }
        } else if (tank.getY() - position.getY() != 0) {
            if (tank.getY() - position.getY() > 0) {
                bufDir = Direction.UP;
            } else {
                bufDir = Direction.DOWN;
            }
        }

        //Посмотрим, как он двигается
        TankMove tankMove = new TankMove(tank.getId(), bufDir, false);
        if (matrix[position.getX()][position.getY()] == State.BRICK && tank.getDir() == bufDir) {
            tankMove.setShoot(true);
        }
        return tankMove;
    }

    private Stack<Position> findPath(Tank tank, int pointx, int pointy) {
        int[][] copyMatrix = new int[matrix.length][matrix[0].length];
        for (int i = 0; i < copyMatrix.length; i++) {
            for (int j = 0; j < copyMatrix[0].length; j++) {
                copyMatrix[i][j] = -1; //точка не посещена
            }
        }
        copyMatrix[tank.getX()][tank.getY()] = 0;
        Queue<Tank> queue = new LinkedList<>();
        queue.add(tank);
        while (!queue.isEmpty()) {
            Tank posTank = queue.poll();

            //ПРИЗНАК КОНЦА
            if (posTank.getX() == pointx && posTank.getY() == pointy) {
                return findReversePath(copyMatrix, pointx, pointy);
            }


            //left
            if (validPoint(posTank.getX() - 1, posTank.getY()) && copyMatrix[posTank.getX() - 1][posTank.getY()] != -1) {
                copyMatrix[posTank.getX() - 1][posTank.getY()] = copyMatrix[posTank.getX()][posTank.getY()] + 1;
                if (posTank.getDir() != Direction.LEFT) {
                    copyMatrix[posTank.getX() - 1][posTank.getY()] += 1;
                }
                Tank leftTank = new Tank(posTank.getX() - 1, posTank.getY());
                leftTank.setDir(Direction.LEFT);
                queue.add(leftTank);
            }
            //right
            if (validPoint(posTank.getX() + 1, posTank.getY()) && copyMatrix[posTank.getX() + 1][posTank.getY()] != -1) {
                copyMatrix[posTank.getX() + 1][posTank.getY()] = copyMatrix[posTank.getX()][posTank.getY()] + 1;
                if (posTank.getDir() != Direction.LEFT) {
                    copyMatrix[posTank.getX() - 1][posTank.getY()] += 1;
                }
                Tank leftTank = new Tank(posTank.getX() + 1, posTank.getY());
                leftTank.setDir(Direction.LEFT);
                queue.add(leftTank);
            }
            //up
            if (validPoint(posTank.getX(), posTank.getY() - 1) && copyMatrix[posTank.getX()][posTank.getY() - 1] != -1) {
                copyMatrix[posTank.getX()][posTank.getY() - 1] = copyMatrix[posTank.getX()][posTank.getY()] + 1;
                if (posTank.getDir() != Direction.LEFT) {
                    copyMatrix[posTank.getX()][posTank.getY() - 1] += 1;
                }
                Tank leftTank = new Tank(posTank.getX(), posTank.getY() - 1);
                leftTank.setDir(Direction.LEFT);
                queue.add(leftTank);
            }
            //down
            if (validPoint(posTank.getX(), posTank.getY() + 1) && copyMatrix[posTank.getX()][posTank.getY() + 1] != -1) {
                copyMatrix[posTank.getX()][posTank.getY() + 1] = copyMatrix[posTank.getX()][posTank.getY()] + 1;
                if (posTank.getDir() != Direction.LEFT) {
                    copyMatrix[posTank.getX()][posTank.getY() + 1] += 1;
                }
                Tank leftTank = new Tank(posTank.getX(), posTank.getY() + 1);
                leftTank.setDir(Direction.LEFT);
                queue.add(leftTank);
            }

            //признак конца
        }

        return null;

    }

    private Stack<Position> findReversePath(int[][] copyMatrix, int pointx, int pointy) {
        Stack<Position> stack = new Stack();
        stack.push(new Position(pointx, pointy));
        int posx = pointx;
        int posy = pointy;
        while (copyMatrix[posx][posy] > 0) {
            if (validBorder(posx - 1, posy)
                    && copyMatrix[posx][posy] - copyMatrix[posx - 1][posy] == 1 && copyMatrix[posx][posy] - copyMatrix[posx - 1][posy] == 2) {
                stack.push(new Position(posx - 1, posy));
            } else if (validBorder(posx + 1, posy)
                    && copyMatrix[posx][posy] - copyMatrix[posx + 1][posy] == 1 && copyMatrix[posx][posy] - copyMatrix[posx + 1][posy] == 2) {
                stack.push(new Position(posx + 1, posy));
            } else if (validBorder(posx, posy - 1)
                    && copyMatrix[posx][posy] - copyMatrix[posx][posy - 1] == 1 && copyMatrix[posx][posy] - copyMatrix[posx][posy - 1] == 2) {
                stack.push(new Position(posx, posy - 1));
            } else if (validBorder(posx, posy + 1)
                    && copyMatrix[posx][posy] - copyMatrix[posx][posy + 1] == 1 && copyMatrix[posx][posy] - copyMatrix[posx][posy + 1] == 2) {
                stack.push(new Position(posx, posy + 1));
            }
            posx = stack.peek().getX();
            posy = stack.peek().getY();
        }
        return stack;
    }


}


class State {
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