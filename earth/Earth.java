package earth;
import java.util.*;

public final class Earth {
	private int currentTick = 0;
	
	private Organism land[][];
	private int maxX = 0;
	private int maxY = 0;

	private Set<Plant> plants = new HashSet<>();
	private Set<Herbivore> herbivores = new HashSet<>();
	private Set<Carnivore> carnivores = new HashSet<>();

	private Set<Organism> toAdd = new HashSet<>();

	private int nextPlantSpawn;

	public enum OrgType {
		plant, herbivore, carnivore
	}

	/**
	 * This initializes the map where plants, herbivores, and carnivores will
	 * stay, reproduce, and devour one another in.
	 * 
	 * @param x
	 *            - Width of the map
	 * @param y
	 *            - Height of the map
	 */
	public Earth(int x, int y) {
		// create map
		land = new Organism[y][x];
		maxX = x;
		maxY = y;
		nextPlantSpawn = new Random().nextInt(3) + 4;
	}

	/**
	 * Trigger a tick on all organisms.
	 */
	public void tick() {
		// Tick all organisms
		Iterator<Plant> p = plants.iterator();
		while (p.hasNext()) {
			Plant pe = p.next();
			if (pe.isDead()) {
				if (land[pe.getY()][pe.getX()] == pe)
					land[pe.getY()][pe.getX()] = null;
				p.remove();
				continue;
			}
			pe.tick();
		}
		Iterator<Herbivore> h = herbivores.iterator();
		while (h.hasNext()) {
			Herbivore he = h.next();
			if (he.isDead()) {
				if (land[he.getY()][he.getX()] == he)
					land[he.getY()][he.getX()] = null;
				h.remove();
				continue;
			}
			he.tick();
		}
		Iterator<Carnivore> c = carnivores.iterator();
		while (c.hasNext()) {
			Carnivore ce = c.next();
			if (ce.isDead()) {
				if (land[ce.getY()][ce.getX()] == ce)
					land[ce.getY()][ce.getX()] = null;
				c.remove();
				continue;
			}
			ce.tick();
		}

		// Add random plants
		if (getTick() > nextPlantSpawn) {
			nextPlantSpawn = getTick() + 4 + new Random().nextInt(3);
			addOrganism(OrgType.plant, 1);
		}

		// Add organisms to the list
		for (Organism org : toAdd) {
			if (org instanceof Plant) {
				plants.add((Plant) org);
				continue;
			}
			if (org instanceof Herbivore) {
				herbivores.add((Herbivore) org);
				continue;
			}
			if (org instanceof Carnivore) {
				carnivores.add((Carnivore) org);
				continue;
			}
		}
		toAdd.clear();

		currentTick++;
	}

	/**
	 * Returns the current tick.
	 * 
	 * @return current tick of this map.
	 */
	public int getTick() {
		return currentTick;
	}

	/**
	 * Returns the population on this map.
	 * 
	 * @return population.
	 */
	public int getPopulation() {
		return plants.size() + herbivores.size() + carnivores.size();
	}
	
	/**
	 * Returns the maximum possible population on this map.
	 * 
	 * @return maximum population.
	 */
	public int getMaxPopulation() {
		return maxX * maxY;
	}

	// Organism add queue
	private void addOrgToList(Organism org) {
		toAdd.add(org);
	}

	/**
	 * Get the height of the map.
	 * 
	 * @return x
	 */
	public int getMaxX() {
		return maxX;
	}

	/**
	 * Get the width of the map.
	 * 
	 * @return y
	 */
	public int getMaxY() {
		return maxY;
	}

	/**
	 * Adds an organism to the random location of the map.
	 * 
	 * @param type
	 *            - type of organism as defined in OrgType class
	 * @param times
	 *            - How many of the ogranisms to add
	 * @return true when organism was successfully added.
	 */
	public boolean addOrganism(OrgType type, int times) {
		while (times > 0) {
			if (getPopulation() == getMaxPopulation())
				return false;
			// Check population
			if (getPopulation() > getMaxPopulation())
				return false;

			Random rand = new Random();
			Organism organism;
			if (type == OrgType.herbivore)
				organism = new Herbivore(0, 0, this, 20 + rand.nextInt(9),
						8 + rand.nextInt(5));
			else if (type == OrgType.carnivore)
				organism = new Carnivore(0, 0, this, 18 + rand.nextInt(9),
						8 + rand.nextInt(5));
			else
				organism = new Plant(0, 0, this, 8 + rand.nextInt(7),
						6 + rand.nextInt(5));

			int x = rand.nextInt(maxX);
			int y = rand.nextInt(maxY);

			// Test coordinates
			while (!addOrganism(organism, x, y)) {
				x = rand.nextInt(maxX);
				y = rand.nextInt(maxY);
			}
			// Fix position
			organism.setPosition(x, y);
			times--;
		}
		return true;
	}

	/**
	 * Adds a specific organism to the defined coordinate location of the map.
	 * 
	 * @param organism
	 *            - Organism to add
	 * @param x
	 *            - x coordinate
	 * @param y
	 *            - y coordinate
	 * @return true when organism was successfully added.
	 */
	public boolean addOrganism(Organism organism, int x, int y) {
		// add organism to specific place. If non-empty, skip and return false.
		if (land[y][x] == null) {
			land[y][x] = organism;
			addOrgToList(organism);
			return true;
		}
		return false;
	}

	/**
	 * Gets the organism at the specific coordinates
	 * 
	 * @param x
	 *            - x coordinate
	 * @param y
	 *            - y coordinate
	 * @return an Organism object where the organism resides. If none, returns
	 *         null.
	 */
	public Organism getOrganism(int x, int y) {
		// Get organism by coordinate
		return land[y][x];
	}

	/**
	 * Moves an organism to the new specified position
	 * 
	 * @param org
	 *            - Organism to move
	 * @param pos
	 *            - Coordinate of the Mappable interface object
	 * @return true on success, false on null organism
	 */
	public boolean moveOrganism(Organism org, Mappable pos) {
		int newX = pos.getX();
		int newY = pos.getY();

		// check new pos
		Organism moveTo = getOrganism(newX, newY);
		if (moveTo != null)
			// Kill target organism
			moveTo.kill(); 

		// Move
		land[newY][newX] = org;
		land[org.getY()][org.getX()] = null;
		org.setPosition(newX, newY);
		return true;
	}

	/**
	 * Gets a list of neighboring organisms from top to bottom, left to right.
	 * 
	 * @return neighboring organisms in a List of Organisms.
	 */
	public ArrayList<Mappable> getNeighbors(Mappable org) {
		int x = org.getX();
		int y = org.getY();

		// Bounds
		int xb[];
		int yb[];
		if (maxX == 1)
			xb = new int[] { x };
		else if (x == 0)
			xb = new int[] { x, x + 1 };
		else if (x + 1 == maxX)
			xb = new int[] { x - 1, x };
		else
			xb = new int[] { x - 1, x, x + 1 };
		if (maxY == 1)
			yb = new int[] { y };
		else if (y == 0)
			yb = new int[] { y, y + 1 };
		else if (y + 1 == maxY)
			yb = new int[] { y - 1, y };
		else
			yb = new int[] { y - 1, y, y + 1 };

		// populate
		ArrayList<Mappable> neighbors = new ArrayList<>();
		for (int yt : yb) {
			for (int xt : xb) {
				if (yt == y && xt == x)
					continue;
				Mappable orgn = getOrganism(xt, yt);
				if (orgn == null)
					orgn = new Air(xt, yt);
				neighbors.add(orgn);
				// this should not go out of bounds.
			}
		}

		return neighbors;
	}

	/**
	 * 
	 * @return returns the 2d array of the map.  This may contain null variables.
	 */
	public Mappable[][] getMap() {
		return land;
	}

	/**
	 * @return true if this Earth has organisms, false otherwise
	 */
	public boolean hasAnimals() {
		return !(herbivores.isEmpty() && carnivores.isEmpty());
	}
}