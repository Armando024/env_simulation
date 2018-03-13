package earth;
import java.util.Random;

class Herbivore extends Animal {
    /**
     * Creates a Herbivore object
     * @param x - x-coordinate
     * @param y - y-coordinate
     * @param earth - Earth class the object will be on
     */
    public Herbivore(int x, int y, Earth earth, int initEnergy, int ageExpectancy) {
        super(x, y, earth, initEnergy, ageExpectancy,
                2, //tickMoveEvery
                4, //minBirthTick
                20, //minBirthEnergy
                32 //maxHungryEnergy
        );
    }

    @Override
    public char toChar() {return '&'; }

    @Override
    public boolean canEat(Organism org, boolean considerEnergy) {
        return org instanceof Plant && (!considerEnergy || getEnergy() < getMaxHungryEnergy());
    }

    @Override
    Animal newAnimal(Mappable pos) {
        return new Herbivore(pos.getX(),pos.getY(),getEarth(),
                getEnergy()/2-1,
                getExpectancy() - 1 + new Random().nextInt(4)
        );
    }
}
