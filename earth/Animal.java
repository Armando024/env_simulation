package earth;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

abstract class Animal extends Organism {
    private int nextMoveTick;
    private int nextBirthTick;

    private final int tickMoveEvery; // lower => faster
    private final int minBirthTick;
    private final int minBirthEnergy;
    private final int maxHungryEnergy;

    private byte beenHere[][];

    /**
     * Creates an Animal object.  This is not to be used to create an object.
     * @param x - x-coordinate
     * @param y - y-coordinate
     * @param earth - Earth class the object will be on
     * @param initEnergy - Initial energy
     * @param tickMoveEvery - Ticks in which animals should wait until they can move.
     */
    Animal(int x, int y, Earth earth, int initEnergy, int ageExpectancy,
           int tickMoveEvery, int minBirthTick, int minBirthEnergy, int maxHungryEnergy) {
        super(x, y, earth, initEnergy, ageExpectancy);
        this.tickMoveEvery = tickMoveEvery;
        this.minBirthTick = minBirthTick;
        this.minBirthEnergy = minBirthEnergy;
        int tick = earth.getTick();
        nextMoveTick = tick + tickMoveEvery;
        nextBirthTick = tick + minBirthTick + 2; // Child Buffer
        this.maxHungryEnergy = maxHungryEnergy;
        // Set the current position
        beenHere = new byte[earth.getMaxY()][earth.getMaxX()];
        beenHere[y][x]++;
    }

    /**
     * CHecks if this can eat the organism
     * @param org the organism to check edibility against
     * @return true if this can eat org, false otherwise
     */
    abstract boolean canEat(Organism org, boolean considerEnergy);

    /**
     * Ticks the organism. Checks if the organism should die
     * based on energy and tooOld() implementation. (superclass)
     * It then checks the lovableness and the next move.
     * Should ideally be used only by earth.
     */
    @Override
    public void tick() {
        super.tick();

        if (isDead())
            return;

        // Get child
        offspring();

        // Move
        movement();
    }

    /**
     *
     * @return the maximum energy needed to ignore prey
     */
    final int getMaxHungryEnergy() {
        return maxHungryEnergy;
    }

    /**
     * Checks if the animal can move.  If able, moves the animal.
     * @return true on success, false if animal cannot be moved.
     */
    public void movement() {
        if (nextMoveTick > getEarth().getTick())
            // Too soon to move
            return;

        List<Mappable> neigh = getEarth().getNeighbors(this);
        Collections.shuffle(neigh);
        Collections.sort(neigh,new Comparator<Mappable>() {
            @Override
            public int compare(Mappable a, Mappable b) {
                // Smallest beenhere first
                return beenHere[a.getY()][a.getX()] - beenHere[b.getY()][b.getX()];
            }
        });

        for (Mappable pos : neigh) {
            if (dangerous(pos)) continue;

            // Checks if empty or edible
            if (pos instanceof Air || canEat((Organism) pos, true)) {
                // Move over to the location org is in.
                moveTo(pos);
                return;
            }
        }
    }

    private void moveTo(Mappable pos) {
        if (pos instanceof Air) {
            // moved to an empty spot
            subEnergy(1);
        } else {
            // moved to an eatable organism
            addEnergy(this.getEatenEnergy());
            if (getEnergy() > maxHungryEnergy)
                // Increase lifespan for this and its offsprings
                extendExpectancy(new Random().nextInt(2) + 1);
        }
        getEarth().moveOrganism(this, pos);
        nextMoveTick = getEarth().getTick() + tickMoveEvery;
        beenHere[pos.getY()][pos.getX()]++;
    }

    private void offspring() {
        if ((nextBirthTick > getEarth().getTick() || minBirthEnergy > getEnergy()) || new Random().nextInt(5) > 2)
            return;
        // Get neighbors
        List<Mappable> neigh = getEarth().getNeighbors(this);
        Collections.shuffle(neigh);
        Collections.sort(neigh,new Comparator<Mappable>() {
            @Override
            public int compare(Mappable a, Mappable b) {
                return beenHere[b.getY()][b.getX()] - beenHere[a.getY()][a.getX()];
                // Opposite of that in movement() function
            }
        });

        // Reproduce if there's room
        for (Mappable pos : neigh) {
            if (dangerous(pos)) continue;

            if (pos instanceof Air) {
                // There is room for love to happen.
                getEarth().addOrganism(newAnimal(pos), pos.getX(), pos.getY());

                // Next birth
                int tick = getEarth().getTick();
                this.nextBirthTick = tick + minBirthTick;
                subEnergy(getEnergy()/2);
                break;
            }
        }
    }

    private boolean dangerous(Mappable pos) {
        // Check if there's predator near the selected location
        for (Mappable orgc : getEarth().getNeighbors(pos)) {
            // Checks if there is a predator
            if (orgc instanceof Animal && ((Animal) orgc).canEat(this, false)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Gets the new animal based on this
     * @param pos Mappable Position
     * @return
     */
    abstract Animal newAnimal(Mappable pos);
}
