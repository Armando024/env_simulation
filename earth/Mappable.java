package earth;

public interface Mappable {
    /**
     *
     * @return the X-position of this organism in the Earth
     */
    int getX();

    /**
     *
     * @return the Y-position of this organism in the Earth
     */
    int getY();

    /**
     * @see #toChar()
     */
    @Override
    String toString();

    /**
     * Prints the character symbol of the organism.
     * @return the character code
     */
    default char toChar() {
    	return ' ';
    }
}
