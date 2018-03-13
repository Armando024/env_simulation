package earth;
import java.util.Random;

class Carnivore extends Animal {
    /**
     * Creates a Carnivore object.
     * @param x - x-coordinate
     * @param y - y-coordinate
     * @param earth - Earth class the object will be on
     */
    public Carnivore(int x, int y, Earth earth, int initEnergy, int ageExpectancy) {
        super(x, y, earth, initEnergy, ageExpectancy,
                1, //tickMoveEvery
                5, // minBirthTick
                20, //minBirthEnergy
                30  //maxHungryEnergy
        );
    }

    @Override
    public char toChar() {return '@'; }

    @Override
    public boolean canEat(Organism org, boolean considerEnergy) {
        return org instanceof Herbivore && (!considerEnergy || getEnergy() < getMaxHungryEnergy());
    }

    @Override
    Animal newAnimal(Mappable pos) {
        return new Carnivore(pos.getX(),pos.getY(),getEarth(),
                getEnergy()/2-1,
                getExpectancy() - 1 + new Random().nextInt(4)
        );
    }
}
