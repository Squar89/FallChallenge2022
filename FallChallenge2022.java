import java.util.*;
import java.io.*;
import java.math.*;

class Cord {
    private final int x;
    private final int y;
    private int hashCode;

    public Cord(int x, int y) {
        this.x = x;
        this.y = y;
        this.hashCode = Objects.hash(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Cord that = (Cord) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }
}

class FieldInfo {
    Cord field;
    int scrapAmount;
    int owner;
    int units;
    int recycler;
    int canBuild;
    int canSpawn;
    int inRangeOfRecycler;

    FieldInfo(Cord field, int scrapAmount, int owner, int units, int recycler,
              int canBuild, int canSpawn, int inRangeOfRecycler) {
        this.field = field;
        this.scrapAmount = scrapAmount;
        this.owner = owner;
        this.units = units;
        this.recycler = recycler;
        this.canBuild = canBuild;
        this.canSpawn = canSpawn;
        this.inRangeOfRecycler = inRangeOfRecycler;
    }

    int getFieldRecycleValue() {
        if (owner == 1 && inRangeOfRecycler != 1) {
            return scrapAmount;
        }

        return 0;
    }
}

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
    static HashMap<Cord, FieldInfo> board;
    static ArrayList<Cord> unownedMovableCords;
    static ArrayList<Cord> canSpawnCords;
    static int width;
    static int height;

    static Cord selectRecycler(ArrayList<Cord> possibleRecyclers) {
        int maxValue = 0;
        Cord bestCord = new Cord(0, 0);
        
        for (Cord cord : possibleRecyclers) {
            int val = getRecyclerValue(cord);
            if (val > maxValue) {
                maxValue = val;
                bestCord = cord;
            }
        }

        return maxValue == 0 ? possibleRecyclers.get(0) : bestCord;
    }

    static int getRecyclerValue(Cord cord) {
        int scrapSum = 0;
        
        FieldInfo fieldCenter = board.get(cord);
        scrapSum += fieldCenter.getFieldRecycleValue();

        if (cord.getX() > 0) {
            scrapSum += board.get(new Cord(cord.getX() - 1, cord.getY())).getFieldRecycleValue();
        }
        if (cord.getX() < width - 1) {
            scrapSum += board.get(new Cord(cord.getX() + 1, cord.getY())).getFieldRecycleValue();
        }
        if (cord.getY() > 0) {
            scrapSum += board.get(new Cord(cord.getX(), cord.getY() - 1)).getFieldRecycleValue();
        }
        if (cord.getY() < height - 1) {
            scrapSum += board.get(new Cord(cord.getX(), cord.getY() + 1)).getFieldRecycleValue();
        }

        return scrapSum;
    }

    static Cord getRandomUnownedMovableCord() {
        int index = new Random().nextInt(unownedMovableCords.size());

        return unownedMovableCords.get(index);
    }

    static Cord getRandomUnitCord(ArrayList<Cord> currentUnits) {
        int index = new Random().nextInt(currentUnits.size());

        return currentUnits.get(index);
    }

    static String selectUnitMove(Cord cord) {
        int unitsCount = board.get(cord).units;

        if (cord.getX() < width - 1) {
            FieldInfo rightField = board.get(new Cord(cord.getX() + 1, cord.getY()));
            if (rightField.owner != 1 && rightField.scrapAmount > 0 && rightField.recycler == 0) {
                return ("MOVE " + unitsCount + " " + cord.getX() + " " + cord.getY() + " "
                        + (cord.getX() + 1) + " " + cord.getY() + ";");
            }
        }
        if (cord.getY() < height - 1) {
            FieldInfo upperField = board.get(new Cord(cord.getX(), cord.getY() + 1));
            if (upperField.owner != 1 && upperField.scrapAmount > 0 && upperField.recycler == 0) {
                return ("MOVE " + unitsCount + " " + cord.getX() + " " + cord.getY() + " "
                        + cord.getX() + " " + (cord.getY() + 1) + ";");
            }
        }
        if (cord.getX() > 0) {
            FieldInfo leftField = board.get(new Cord(cord.getX() - 1, cord.getY()));
            if (leftField.owner != 1 && leftField.scrapAmount > 0 && leftField.recycler == 0) {
                return ("MOVE " + unitsCount + " " + cord.getX() + " " + cord.getY() + " "
                        + (cord.getX() - 1) + " " + cord.getY() + ";");
            }
        }
        if (cord.getY() > 0) {
            FieldInfo bottomField = board.get(new Cord(cord.getX(), cord.getY() - 1));
            if (bottomField.owner != 1 && bottomField.scrapAmount > 0 && bottomField.recycler == 0) {
                return ("MOVE " + unitsCount + " " + cord.getX() + " " + cord.getY() + " "
                        + cord.getX() + " " + (cord.getY() - 1) + ";");
            }
        }

        if (unownedMovableCords.size() > 0) {
            Cord randomCord = getRandomUnownedMovableCord();
            return ("MOVE " + unitsCount + " " + cord.getX() + " " + cord.getY() + " "
                    + randomCord.getX() + " " + randomCord.getY() + ";");
        }

        return "";
    }


    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        width = in.nextInt();
        height = in.nextInt();
        board = new HashMap<Cord, FieldInfo>();
        unownedMovableCords = new ArrayList<Cord>();
        canSpawnCords = new ArrayList<Cord>();


        // game loop
        while (true) {
            String movesList = "";
            ArrayList<Cord> possibleRecyclers = new ArrayList<Cord>();
            ArrayList<Cord> currentUnits = new ArrayList<Cord>();
            board.clear();
            unownedMovableCords.clear();
            canSpawnCords.clear();

            int myMatter = in.nextInt();
            int oppMatter = in.nextInt();
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int scrapAmount = in.nextInt();
                    int owner = in.nextInt(); // 1 = me, 0 = foe, -1 = neutral
                    int units = in.nextInt();
                    int recycler = in.nextInt();
                    int canBuild = in.nextInt();
                    int canSpawn = in.nextInt();
                    int inRangeOfRecycler = in.nextInt();

                    board.put(new Cord(j, i),
                              new FieldInfo(new Cord(j, i), scrapAmount, owner, units, recycler, canBuild,
                                            canSpawn, inRangeOfRecycler));

                    if (owner == 1 && canBuild == 1 && inRangeOfRecycler != 1) {
                        possibleRecyclers.add(new Cord(j, i));
                    }

                    if (owner == 1 && units > 0) {
                        currentUnits.add(new Cord(j, i));
                    }

                    if (owner != 1 && recycler != 1 && scrapAmount > 0) {
                        unownedMovableCords.add(new Cord(j, i));
                    }

                    if (canSpawn == 1) {
                        canSpawnCords.add(new Cord(j, i));
                    }
                }
            }

            if (myMatter >= 10 && !possibleRecyclers.isEmpty()) {
                Cord newRecCord = selectRecycler(possibleRecyclers);

                if (getRecyclerValue(newRecCord) >= 15) {
                    movesList += "BUILD " + newRecCord.getX() + " " + newRecCord.getY() + ";";
                    myMatter -= 10;
                    canSpawnCords.remove(newRecCord);
                }
            }

            for (Cord unitCord : currentUnits) {
                movesList += selectUnitMove(unitCord);
            }

            while (myMatter >= 10) {
                Cord nextSpawnCord = !currentUnits.isEmpty()
                                        ? getRandomUnitCord(currentUnits) : getRandomUnitCord(canSpawnCords);

                int spawnCount = myMatter / 10;
                myMatter -= spawnCount * 10;
                movesList += "SPAWN " + spawnCount + " " + nextSpawnCord.getX() + " " + nextSpawnCord.getY() + ";";
            }

            movesList += "WAIT;";
            System.out.println(movesList);
        }
    }
}