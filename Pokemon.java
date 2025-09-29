import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The Pokemon class represents a Pokemon creature with its attributes and characteristics.
 * It encapsulates all the data related to a Pokemon including its stats, types, moves,
 * and held items as specified in the Enhanced Pokedex requirements.
 */
public class Pokemon {
    private int pokedexNumber;
    private String name;
    private String type1;
    private String type2;
    private int level;  // Changed from baseLevel to level
    private int hp;
    private int attack;
    private int defense;
    private int speed;
    private List<Move> moveSet = new ArrayList<>();
    private Item heldItem;
    private int evolvesFrom;  // Pokedex number of pre-evolution
    private int evolvesTo;    // Pokedex number of evolution
    private int evolutionLevel;
    private String evolutionMethod;
    private String evolutionStoneType;

    /**
     * Constructs a new Pokemon with the specified attributes.
     * Initializes the Pokemon with default moves "Tackle" and "Defend" as required.
     *
     * @param pokedexNumber The unique Pokedex number (1-999)
     * @param name          The name of the Pokemon
     * @param type1         Primary type (e.g., "Fire", "Water")
     * @param type2         Secondary type (optional, null if none)
     * @param level         Starting level (1-100)
     * @param hp            Base HP stat
     * @param attack        Base Attack stat
     * @param defense       Base Defense stat
     * @param speed         Base Speed stat
     * @param evolvesFrom   Pokedex number of the pre-evolution (0 if none)
     * @param evolvesTo     Pokedex number of the evolution (0 if none)
     * @param evolutionLevel The level at which the Pokemon evolves (0 if not level-based)
     */
    public Pokemon(int pokedexNumber, String name, String type1, String type2,
                   int level, int hp, int attack, int defense, int speed,
                   int evolvesFrom, int evolvesTo, int evolutionLevel) {
        this(pokedexNumber, name, type1, type2, level, hp, attack, defense, speed,
                evolvesFrom, evolvesTo, evolutionLevel, null, null);
    }

    /**
     * Extended constructor with evolution information including method and stone type.
     */
    public Pokemon(int pokedexNumber, String name, String type1, String type2,
                   int level, int hp, int attack, int defense, int speed,
                   int evolvesFrom, int evolvesTo, int evolutionLevel,
                   String evolutionMethod, String evolutionStoneType) {
        this.pokedexNumber = pokedexNumber;
        this.name = name;
        this.type1 = type1;
        this.type2 = type2;
        this.level = level;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.evolvesFrom = evolvesFrom;
        this.evolvesTo = evolvesTo;
        this.evolutionLevel = evolutionLevel;
        this.evolutionMethod = evolutionMethod;
        this.evolutionStoneType = evolutionStoneType;
        this.moveSet = new ArrayList<>();
        addDefaultMoves();
    }

    /**
     * Adds default moves ("Tackle" and "Defend") to the Pokemon's moveset if they are not already present.
     */
    private void addDefaultMoves() {
        if (moveSet.stream().noneMatch(m -> m.getName().equals("Tackle"))) {
            moveSet.add(new Move("Tackle", "A basic physical attack", "Physical", "Normal", null));
        }
        if (moveSet.stream().noneMatch(m -> m.getName().equals("Defend"))) {
            moveSet.add(new Move("Defend", "Raises defense", "Status", "Normal", null));
        }
    }

    /**
     * Levels up the Pokemon, increasing its stats by 10%
     * and checks if it should evolve.
     */
    public boolean levelUp() {
        if (level >= 100) {
            System.out.println(name + " is already at maximum level!");
            return false;
        }

        level++;
        hp = (int)(hp * 1.1);
        attack = (int)(attack * 1.1);
        defense = (int)(defense * 1.1);
        speed = (int)(speed * 1.1);

        System.out.println(name + " grew to level " + level + "!");
        cry();

        // Check for evolution and return true if it occurred
        if (canEvolveByLevel()) {
            return true;
        }
        return false;
    }

    /**
     * Evolves the Pokemon by updating its Pokedex number, name, types, and stats
     * to that of its evolved form.
     *
     * @param newDexNumber The Pokedex number of the evolved form.
     * @param newName The name of the evolved form.
     * @param newType1 The primary type of the evolved form.
     * @param newType2 The secondary type of the evolved form (can be null).
     * @param baseHp The base HP stat of the evolved form.
     * @param baseAttack The base Attack stat of the evolved form.
     * @param baseDefense The base Defense stat of the evolved form.
     * @param baseSpeed The base Speed stat of the evolved form.
     */
    public void evolve(int newDexNumber, String newName, String newType1, String newType2,
                       int baseHp, int baseAttack, int baseDefense, int baseSpeed) {
        this.pokedexNumber = newDexNumber;
        this.name = newName;
        this.type1 = newType1;
        this.type2 = newType2;

        // Take the higher of current or evolved base stats
        this.hp = Math.max(this.hp, baseHp);
        this.attack = Math.max(this.attack, baseAttack);
        this.defense = Math.max(this.defense, baseDefense);
        this.speed = Math.max(this.speed, baseSpeed);
    }

    /**
     * Checks if the Pokemon can evolve based on its current level and predefined evolution level.
     * @return true if the Pokemon is eligible for level-based evolution, false otherwise.
     */
    public boolean canEvolveByLevel() {
        return evolvesTo != 0 && level >= evolutionLevel;
    }

    /**
     * Checks if the Pokemon can evolve using a stone.
     * @param stone The evolution stone item
     * @return true if stone can trigger evolution
     */
    public boolean canEvolveByStone(Item stone) {
        if (stone == null || !stone.getCategory().equals("Evolution Stone")) {
            return false;
        }
        return "stone".equals(evolutionMethod) &&
                stone.getName().equalsIgnoreCase(evolutionStoneType);
    }


    public void cry() {
        System.out.println(name + " says: " + (name.toLowerCase().contains("chu") ? "Pika pika!" : "Rawr!"));
    }

    /**
     * Returns a formatted string representation of the Pokemon's key attributes.
     * @return A string containing the Pokedex number, name, level, types, and stats.
     */
    @Override
    public String toString() {
        return String.format("#%03d %s - Lv.%d [%s%s] HP:%d ATK:%d DEF:%d SPD:%d",
                pokedexNumber, name, level, type1,
                type2 != null ? "/" + type2 : "",
                hp, attack, defense, speed);

    }

    // Getters and setters
    public int getPokedexNumber() { return pokedexNumber; }
    public String getName() { return name; }
    public String getType1() { return type1; }
    public String getType2() { return type2; }
    public int getLevel() { return level; }
    public List<Move> getMoveSet() { return moveSet; }
    public int getHp() {return hp;}
    public int getAttack() {return attack;}
    public int getDefense() {return defense;}
    public int getSpeed() {return speed;}
    public Item getHeldItem() { return heldItem; }
    public int getEvolvesFrom() { return evolvesFrom; }
    public int getEvolvesTo() { return evolvesTo; }
    public int getEvolutionLevel() { return evolutionLevel; }
    public String getEvolutionMethod() { return evolutionMethod;}
    public String getEvolutionStoneType() {return evolutionStoneType;}

    public void setHeldItem(Item item) {
        if (heldItem != null) {
            System.out.println(name + " is holding " + heldItem.getName() + ". It will be discarded.");
        }
        heldItem = item;
    }
    /**
     * Copy constructor - creates a new Pokemon with same attributes
     * @param other The Pokemon to copy
     */
    public Pokemon(Pokemon other) {
        this.pokedexNumber = other.pokedexNumber;
        this.name = other.name;
        this.type1 = other.type1;
        this.type2 = other.type2;
        this.level = other.level;
        this.hp = other.hp;
        this.attack = other.attack;
        this.defense = other.defense;
        this.speed = other.speed;
        this.evolvesFrom = other.evolvesFrom;
        this.evolvesTo = other.evolvesTo;
        this.evolutionLevel = other.evolutionLevel;
        this.evolutionMethod = other.evolutionMethod;
        this.evolutionStoneType = other.evolutionStoneType;
        this.moveSet = new ArrayList<>(other.moveSet);
        this.heldItem = other.heldItem;
    }

    /**
     * Teaches this Pokemon a new move
     * @param move The move to learn
     * @return true if move was learned successfully
     */
    public boolean learnMove(Move move) {
        // Check if move is already known
        if (moveSet.stream().anyMatch(m -> m.getName().equalsIgnoreCase(move.getName()))) {
            System.out.println(name + " already knows " + move.getName());
            return false;
        }

        // HM moves bypass the 4-move limit
        if (move.isHM()) {
            moveSet.add(move);
            return true;
        }

        // Normal moves have 4-move limit
        if (moveSet.size() >= 4) {
            System.out.println(name + " already knows 4 moves. Forget a move first to learn " + move.getName());
            return false;
        }

        moveSet.add(move);
        return true;
    }

    /**
     * Attempts to forget a move
     * @param moveName The move to forget
     * @return true if successful, false if move is HM or not found
     */
    public boolean forgetMove(String moveName) {
        if (moveName == null || moveName.trim().isEmpty()) {
            return false;
        }

        Optional<Move> moveToForget = moveSet.stream()
                .filter(m -> m.getName().equalsIgnoreCase(moveName))
                .findFirst();

        if (!moveToForget.isPresent()) {
            System.out.println(name + " doesn't know " + moveName);
            return false;
        }

        if (moveToForget.get().isHM()) {
            System.out.println("HM moves cannot be forgotten!");
            return false;
        }

        boolean removed = moveSet.removeIf(m -> m.getName().equalsIgnoreCase(moveName));
        if (removed) {
            // Ensure we still have at least Tackle and Defend
            addDefaultMoves();
            return true;
        }
        return false;
    }

    /**
     * Replaces an old move with a new one
     * @param oldMove The move to replace
     * @param newMove The move to learn
     * @return true if successful
     */
    public boolean replaceMove(Move oldMove, Move newMove) {
        if (oldMove.isHM()) {
            System.out.println("Cannot replace HM moves!");
            return false;
        }
        if (!moveSet.contains(oldMove)) return false;

        moveSet.remove(oldMove);
        return learnMove(newMove);
    }
    public void setLevel(int level) {
        this.level = level;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}