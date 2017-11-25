package com.aci.student24.tanks;

import com.aci.student24.api.tanks.Algorithm;
import com.aci.student24.api.tanks.objects.Base;
import com.aci.student24.api.tanks.objects.Position;
import com.aci.student24.api.tanks.objects.Tank;
import com.aci.student24.api.tanks.state.Direction;
import com.aci.student24.api.tanks.state.MapState;
import com.aci.student24.api.tanks.state.TankMove;

import java.util.*;
import java.util.stream.Collectors;

import static com.aci.student24.api.tanks.Util.deserializeInitialMapState;

public class TankPlayer03Test implements Algorithm {
    private int teamId;

    private int[][] matrix;

    @Override
    public void setMyId(final int id) {
        teamId = id;
    }

    @Override
    public List<TankMove> nextMoves(MapState mapState) {
        initMatrix(mapState);
        try {
            List<TankMove> tankMoves = new ArrayList<>();
//            Position enemyBase = mapState.getBases().stream().filter(b -> b.getTeamId() != teamId).collect(Collectors.toList()).get(0);
            Position allyBase = mapState.getBases().stream().filter(b -> b.getTeamId() == teamId).collect(Collectors.toList()).get(0);
            mapState.getTanks(teamId).forEach(tank -> {
                TankMove tankMove = getNextMoveForTank(tank, allyBase);
                tankMoves.add(tankMove);
            });

            return tankMoves;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        matrix = new int[map.getSize().getWidth()][map.getSize().getHeight()];
        for (int x = 0; x < map.getSize().getWidth(); x++) {
            for (int y = 0; y < map.getSize().getHeight(); y++) {
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
        if (map.getShells() != null) {
            setFireLine(map);
        }
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
        boolean flag = validBorder(pointx, pointy);
        flag = flag && matrix[pointx][pointy] != State.RED;
        flag = flag && matrix[pointx][pointy] != State.BLOCK;
        flag = flag && matrix[pointx][pointy] != State.BASE_ALLY;
        return flag;
//        return validBorder(pointx, pointy) && matrix[pointx][pointy] != State.RED
//                && matrix[pointx][pointy] != State.BLOCK && matrix[pointx][pointy] != State.BASE_ALLY;
    }

    private boolean validBorder(int pointx, int pointy) {
        return pointx >= 0 && pointx < matrix.length && pointy >= 0 && pointy < matrix[0].length;
    }

    private TankMove getNextMoveForTank(Tank tank, Position finishPosition) {
        Stack<Position> positions = findPath(tank, finishPosition.getX(), finishPosition.getY());
        positions.pop();
        Position position = positions.peek();
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
                if (matrix[i][j] == State.BLOCK) {
                    copyMatrix[i][j] = -2;
                }
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
            if (validPoint(posTank.getX() - 1, posTank.getY()) && copyMatrix[posTank.getX() - 1][posTank.getY()] == -1) {
                copyMatrix[posTank.getX() - 1][posTank.getY()] = copyMatrix[posTank.getX()][posTank.getY()] + 1;
                if (posTank.getDir() != Direction.LEFT) {
                    copyMatrix[posTank.getX() - 1][posTank.getY()] += 1;
                }
                Tank tank1 = new Tank(posTank.getX() - 1, posTank.getY());
                tank1.setDir(Direction.LEFT);
                queue.add(tank1);
            }
            //right
            if (validPoint(posTank.getX() + 1, posTank.getY()) && copyMatrix[posTank.getX() + 1][posTank.getY()] == -1) {
                copyMatrix[posTank.getX() + 1][posTank.getY()] = copyMatrix[posTank.getX()][posTank.getY()] + 1;
                if (posTank.getDir() != Direction.RIGHT) {
                    copyMatrix[posTank.getX() - 1][posTank.getY()] += 1;
                }
                Tank tank1 = new Tank(posTank.getX() + 1, posTank.getY());
                tank1.setDir(Direction.RIGHT);
                queue.add(tank1);
            }
            //up
            if (validPoint(posTank.getX(), posTank.getY() - 1) && copyMatrix[posTank.getX()][posTank.getY() - 1] == -1) {
                copyMatrix[posTank.getX()][posTank.getY() - 1] = copyMatrix[posTank.getX()][posTank.getY()] + 1;
                if (posTank.getDir() != Direction.UP) {
                    copyMatrix[posTank.getX()][posTank.getY() - 1] += 1;
                }
                Tank tank1 = new Tank(posTank.getX(), posTank.getY() - 1);
                tank1.setDir(Direction.UP);
                queue.add(tank1);
            }
            //down
            if (validPoint(posTank.getX(), posTank.getY() + 1) && copyMatrix[posTank.getX()][posTank.getY() + 1] == -1) {
                copyMatrix[posTank.getX()][posTank.getY() + 1] = copyMatrix[posTank.getX()][posTank.getY()] + 1;
                if (posTank.getDir() != Direction.DOWN) {
                    copyMatrix[posTank.getX()][posTank.getY() + 1] += 1;
                }
                Tank tank1 = new Tank(posTank.getX(), posTank.getY() + 1);
                tank1.setDir(Direction.DOWN);
                queue.add(tank1);
            }

//            printMatrix(copyMatrix);
            //признак конца
        }

        return null;

    }

    private void printMatrix(int[][] copyMatrix) {
        for (int i = 0; i < copyMatrix[0].length; i++) {
            for (int j = 0; j < copyMatrix.length; j++) {
                System.out.print(copyMatrix[j][i] + "\t");
            }
            System.out.println();
        }
        System.out.println("===============================");
    }

    private Stack<Position> findReversePath(int[][] copyMatrix, int pointx, int pointy) {
        Stack<Position> stack = new Stack();
        stack.push(new Position(pointx, pointy));
        int posx = pointx;
        int posy = pointy;
        while (copyMatrix[posx][posy] > 0) {
            if (validBorder(posx - 1, posy)
                    && (copyMatrix[posx][posy] - copyMatrix[posx - 1][posy] == 1 || copyMatrix[posx][posy] - copyMatrix[posx - 1][posy] == 2)) {
                stack.push(new Position(posx - 1, posy));
            } else if (validBorder(posx + 1, posy)
                    && (copyMatrix[posx][posy] - copyMatrix[posx + 1][posy] == 1 || copyMatrix[posx][posy] - copyMatrix[posx + 1][posy] == 2)) {
                stack.push(new Position(posx + 1, posy));
            } else if (validBorder(posx, posy - 1)
                    && (copyMatrix[posx][posy] - copyMatrix[posx][posy - 1] == 1 || copyMatrix[posx][posy] - copyMatrix[posx][posy - 1] == 2)) {
                stack.push(new Position(posx, posy - 1));
            } else if (validBorder(posx, posy + 1)
                    && (copyMatrix[posx][posy] - copyMatrix[posx][posy + 1] == 1 || copyMatrix[posx][posy] - copyMatrix[posx][posy + 1] == 2)) {
                stack.push(new Position(posx, posy + 1));
            }
            posx = stack.peek().getX();
            posy = stack.peek().getY();
        }
        return stack;
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

    public static void main(String[] args) {
        String s = "{\"size\":{\"height\":24,\"width\":32},\"tanks\":[{\"x\":3,\"y\":1,\"id\":1001,\"teamId\":646,\"coolDown\":false,\"dir\":2,\"oldDir\":2},{\"x\":28,\"y\":1,\"id\":1002,\"teamId\":186,\"coolDown\":false,\"dir\":4,\"oldDir\":4},{\"x\":3,\"y\":6,\"id\":1003,\"teamId\":646,\"coolDown\":false,\"dir\":2,\"oldDir\":2},{\"x\":28,\"y\":6,\"id\":1004,\"teamId\":186,\"coolDown\":false,\"dir\":4,\"oldDir\":4},{\"x\":3,\"y\":12,\"id\":1005,\"teamId\":646,\"coolDown\":false,\"dir\":2,\"oldDir\":2},{\"x\":28,\"y\":12,\"id\":1006,\"teamId\":186,\"coolDown\":false,\"dir\":4,\"oldDir\":4},{\"x\":3,\"y\":17,\"id\":1007,\"teamId\":646,\"coolDown\":false,\"dir\":2,\"oldDir\":2},{\"x\":28,\"y\":17,\"id\":1008,\"teamId\":186,\"coolDown\":false,\"dir\":4,\"oldDir\":4},{\"x\":3,\"y\":22,\"id\":1009,\"teamId\":646,\"coolDown\":false,\"dir\":2,\"oldDir\":2},{\"x\":28,\"y\":22,\"id\":1010,\"teamId\":186,\"coolDown\":false,\"dir\":4,\"oldDir\":4}],\"bases\":[{\"x\":0,\"y\":12,\"id\":1,\"teamId\":646,\"dir\":1},{\"x\":31,\"y\":12,\"id\":2,\"teamId\":186,\"dir\":1}],\"bricks\":[{\"x\":11,\"y\":0,\"id\":2001},{\"x\":20,\"y\":0,\"id\":2002},{\"x\":11,\"y\":1,\"id\":2003},{\"x\":20,\"y\":1,\"id\":2004},{\"x\":11,\"y\":2,\"id\":2005},{\"x\":20,\"y\":2,\"id\":2006},{\"x\":11,\"y\":3,\"id\":2007},{\"x\":20,\"y\":3,\"id\":2008},{\"x\":0,\"y\":4,\"id\":2009},{\"x\":1,\"y\":4,\"id\":2010},{\"x\":2,\"y\":4,\"id\":2011},{\"x\":3,\"y\":4,\"id\":2012},{\"x\":4,\"y\":4,\"id\":2013},{\"x\":5,\"y\":4,\"id\":2014},{\"x\":6,\"y\":4,\"id\":2015},{\"x\":7,\"y\":4,\"id\":2016},{\"x\":11,\"y\":4,\"id\":2017},{\"x\":20,\"y\":4,\"id\":2018},{\"x\":24,\"y\":4,\"id\":2019},{\"x\":25,\"y\":4,\"id\":2020},{\"x\":26,\"y\":4,\"id\":2021},{\"x\":27,\"y\":4,\"id\":2022},{\"x\":28,\"y\":4,\"id\":2023},{\"x\":29,\"y\":4,\"id\":2024},{\"x\":30,\"y\":4,\"id\":2025},{\"x\":31,\"y\":4,\"id\":2026},{\"x\":11,\"y\":5,\"id\":2027},{\"x\":20,\"y\":5,\"id\":2028},{\"x\":11,\"y\":6,\"id\":2029},{\"x\":20,\"y\":6,\"id\":2030},{\"x\":11,\"y\":7,\"id\":2031},{\"x\":20,\"y\":7,\"id\":2032},{\"x\":11,\"y\":8,\"id\":2033},{\"x\":20,\"y\":8,\"id\":2034},{\"x\":11,\"y\":9,\"id\":2035},{\"x\":20,\"y\":9,\"id\":2036},{\"x\":0,\"y\":11,\"id\":2037},{\"x\":1,\"y\":11,\"id\":2038},{\"x\":7,\"y\":11,\"id\":2039},{\"x\":24,\"y\":11,\"id\":2040},{\"x\":30,\"y\":11,\"id\":2041},{\"x\":31,\"y\":11,\"id\":2042},{\"x\":1,\"y\":12,\"id\":2043},{\"x\":7,\"y\":12,\"id\":2044},{\"x\":24,\"y\":12,\"id\":2045},{\"x\":30,\"y\":12,\"id\":2046},{\"x\":0,\"y\":13,\"id\":2047},{\"x\":1,\"y\":13,\"id\":2048},{\"x\":7,\"y\":13,\"id\":2049},{\"x\":24,\"y\":13,\"id\":2050},{\"x\":30,\"y\":13,\"id\":2051},{\"x\":31,\"y\":13,\"id\":2052},{\"x\":11,\"y\":14,\"id\":2053},{\"x\":20,\"y\":14,\"id\":2054},{\"x\":11,\"y\":15,\"id\":2055},{\"x\":20,\"y\":15,\"id\":2056},{\"x\":11,\"y\":16,\"id\":2057},{\"x\":20,\"y\":16,\"id\":2058},{\"x\":11,\"y\":17,\"id\":2059},{\"x\":20,\"y\":17,\"id\":2060},{\"x\":11,\"y\":18,\"id\":2061},{\"x\":20,\"y\":18,\"id\":2062},{\"x\":0,\"y\":19,\"id\":2063},{\"x\":1,\"y\":19,\"id\":2064},{\"x\":2,\"y\":19,\"id\":2065},{\"x\":3,\"y\":19,\"id\":2066},{\"x\":4,\"y\":19,\"id\":2067},{\"x\":5,\"y\":19,\"id\":2068},{\"x\":6,\"y\":19,\"id\":2069},{\"x\":7,\"y\":19,\"id\":2070},{\"x\":11,\"y\":19,\"id\":2071},{\"x\":20,\"y\":19,\"id\":2072},{\"x\":24,\"y\":19,\"id\":2073},{\"x\":25,\"y\":19,\"id\":2074},{\"x\":26,\"y\":19,\"id\":2075},{\"x\":27,\"y\":19,\"id\":2076},{\"x\":28,\"y\":19,\"id\":2077},{\"x\":29,\"y\":19,\"id\":2078},{\"x\":30,\"y\":19,\"id\":2079},{\"x\":31,\"y\":19,\"id\":2080},{\"x\":11,\"y\":20,\"id\":2081},{\"x\":20,\"y\":20,\"id\":2082},{\"x\":11,\"y\":21,\"id\":2083},{\"x\":20,\"y\":21,\"id\":2084},{\"x\":11,\"y\":22,\"id\":2085},{\"x\":20,\"y\":22,\"id\":2086},{\"x\":11,\"y\":23,\"id\":2087},{\"x\":20,\"y\":23,\"id\":2088}],\"indestructibles\":[{\"x\":15,\"y\":5,\"id\":-1},{\"x\":16,\"y\":5,\"id\":-1},{\"x\":15,\"y\":6,\"id\":-1},{\"x\":16,\"y\":6,\"id\":-1},{\"x\":0,\"y\":8,\"id\":-1},{\"x\":1,\"y\":8,\"id\":-1},{\"x\":2,\"y\":8,\"id\":-1},{\"x\":7,\"y\":8,\"id\":-1},{\"x\":24,\"y\":8,\"id\":-1},{\"x\":29,\"y\":8,\"id\":-1},{\"x\":30,\"y\":8,\"id\":-1},{\"x\":31,\"y\":8,\"id\":-1},{\"x\":7,\"y\":9,\"id\":-1},{\"x\":24,\"y\":9,\"id\":-1},{\"x\":7,\"y\":10,\"id\":-1},{\"x\":15,\"y\":10,\"id\":-1},{\"x\":16,\"y\":10,\"id\":-1},{\"x\":24,\"y\":10,\"id\":-1},{\"x\":5,\"y\":11,\"id\":-1},{\"x\":15,\"y\":11,\"id\":-1},{\"x\":16,\"y\":11,\"id\":-1},{\"x\":26,\"y\":11,\"id\":-1},{\"x\":15,\"y\":12,\"id\":-1},{\"x\":16,\"y\":12,\"id\":-1},{\"x\":5,\"y\":13,\"id\":-1},{\"x\":15,\"y\":13,\"id\":-1},{\"x\":16,\"y\":13,\"id\":-1},{\"x\":26,\"y\":13,\"id\":-1},{\"x\":7,\"y\":14,\"id\":-1},{\"x\":24,\"y\":14,\"id\":-1},{\"x\":7,\"y\":15,\"id\":-1},{\"x\":24,\"y\":15,\"id\":-1},{\"x\":0,\"y\":16,\"id\":-1},{\"x\":1,\"y\":16,\"id\":-1},{\"x\":2,\"y\":16,\"id\":-1},{\"x\":29,\"y\":16,\"id\":-1},{\"x\":30,\"y\":16,\"id\":-1},{\"x\":31,\"y\":16,\"id\":-1},{\"x\":15,\"y\":17,\"id\":-1},{\"x\":16,\"y\":17,\"id\":-1},{\"x\":15,\"y\":18,\"id\":-1},{\"x\":16,\"y\":18,\"id\":-1}]}";
        MapState mapState = deserializeInitialMapState(s);
        TankPlayer03Test test = new TankPlayer03Test();
        test.setMyId(646);
        System.out.println(test.nextMoves(mapState));
    }
}


