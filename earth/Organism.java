package earth;

abstract class Organism implements Mappable {
    private final Earth earth;
    private final int birthTick;

    private int x;
    private int y;
    private int energy;
    private int tickExpectancy;
    private boolean isDead;

    /**
     * Creates an Organism object.  This is not to be used to create an object.
     * @param x - x-coordinate
     * @param y - y-coordinate
     * @param earth - Earth class the object will be on
     * @param initEnergy - Initial energy
     */
    Organism(int x, int y, Earth earth, int initEnergy, int ageExpectancy) {
        this.earth = earth;
        this.birthTick = earth.getTick();
        this.x = x;
        this.y = y;
        this.energy = initEnergy;
        this.tickExpectancy = birthTick + ageExpectancy;
    }

    /**
     * Checks if organism is too old and tired.
     * @return if the organism is too old to continue living
     */
    boolean tooOld() {
        if (tickExpectancy < earth.getTick() || energy < 6)
            return true;
        return false;
    }

    /**
     *
     * @return the Earth this organism is in.
     */
    final Earth getEarth() {
        return earth;
    }

    /**
     *
     * @return the age of this organism
     */
    final int getAge() {
        return earth.getTick() - birthTick;
    }

    /**
     *
     * @return the energy given to the eating organism
     */
    int getEatenEnergy() {
        return energy-2;
    }

    /**
     *
     * @return the organism's energy
     */
    final int getEnergy() {
        return energy;
    }

    /**
     *
     * @param n energy to add to the organism
     */
    final void addEnergy(int n) {
        energy += n;
    }

    /**
     *
     * @param n energy to subtract from the organism
     */
    final void subEnergy(int n) {
        energy -= n;
    }

    /**
     *
     * @return the X-position of this organism in the Earth
     */
    @Override
    public final int getX() {
        return x;
    }

    /**
     *
     * @return the Y-position of this organism in the Earth
     */
    @Override
    public final int getY() {
        return y;
    }

    /**
     * Sets the position.
     * @param x X-coordinate of the new position
     * @param y Y-coordinate of the new position
     */
    final void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    final int getExpectancy() {
        return tickExpectancy - birthTick;
    }

    /**
     * Increases the life expectancy
     * @param n ticks to increase the expectancy to
     */
    final void extendExpectancy(int n) {
        tickExpectancy += n;
    }

    /**
     * Checks if the organism is marked dead (or is corpse)
     * @return the state of this organism
     */
    final boolean isDead() {
        return isDead;
    }

    /**
     * Sets the kill flag.
     */
    final void kill() {
        isDead = true;
    }

    @Override
    public final String toString() {
        return String.valueOf(toChar());
    }

    /**
     * Ticks the organism. Checks if the organism should die
     * based on energy and tooOld() implementation.
     * Should ideally be used only by earth.
     */
    void tick() {
        if (energy == 0 || tooOld())
            kill();
    }
}
