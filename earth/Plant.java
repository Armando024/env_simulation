package earth;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class Plant extends Organism {
    private int nextSpreadTick;

    private static final int tickSpreadEvery = 8; // lower => faster

    /**
     * Creates a plant.
     * @param x - x-coordinate
     * @param y - y-coordinate
     * @param earth - Earth class the object will be on
     */
    public Plant(int x, int y, Earth earth, int initEnergy, int ageExpectancy) {
        super(x, y, earth, initEnergy, ageExpectancy);
        this.nextSpreadTick = earth.getTick() + tickSpreadEvery;
    }

    @Override
    public char toChar() {return '*'; }

    /**
     * Ticks the organism. Checks if the organism should die
     * based on energy and tooOld() implementation. (superclass)
     * It then checks the lovableness and the next move.
     * Should ideally be used only by earth.
     */
    @Override
    public void tick() {
        super.tick();
        // We don't need to check if plant is already dead at this point.

        // Spread
        spread();
    }

    private void spread() {
        Random rand = new Random();
        if (nextSpreadTick > getEarth().getTick() || rand.nextInt(5) > 2)
            return;

        List<Mappable> neigh = getEarth().getNeighbors(this);
        Collections.shuffle(neigh);

        for (Mappable pos : neigh) {
            if (pos instanceof Air) {
                // There is room for plant to spread.
                getEarth().addOrganism(
                        new Plant(pos.getX(),pos.getY(),getEarth(),
                                getEnergy() - 2 + rand.nextInt(5),getExpectancy() - 2 + rand.nextInt(5)),
                        pos.getX(), pos.getY());

                // Next spread
                nextSpreadTick = getEarth().getTick() + tickSpreadEvery;
                break;
            }
        }
    }
}
