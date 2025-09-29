import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a Pokemon trainer with their profile, team, and inventory.
 * Handles all trainer-specific operations including Pokemon management,
 * item transactions, and move teaching.
 */
public class Trainer {
    private static final int MAX_ACTIVE_POKEMON = 6;
    private static final int MAX_UNIQUE_ITEMS = 10;
    private static final int MAX_TOTAL_ITEMS = 50;

    private final int trainerId;
    private String name;
    private double money;
    private final List<Pokemon> activeTeam;
    private final List<Pokemon> storage;
    private final List<Item> inventory;
    private String birthdate;
    private String sex;
    private String hometown;
    private String description;

    /**
     * Constructs a new Trainer with initial values
     * @param trainerId Unique identifier for the trainer
     * @param name The trainer's display name
     * @throws IllegalArgumentException if name is null or empty
     */
    public Trainer(int trainerId, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Trainer name cannot be blank");
        }

        this.trainerId = trainerId;
        this.name = name;
        this.money = 1000000.00; // Starting money as per specs
        this.activeTeam = new ArrayList<>(MAX_ACTIVE_POKEMON);
        this.storage = new ArrayList<>();
        this.inventory = new ArrayList<>();
    }


    /**
     * Adds a Pokemon to the trainer's collection
     * @param pokemon The Pokemon to add
     * @return true if added successfully
     */
    public boolean addPokemon(Pokemon pokemon) {
        if (pokemon == null) return false;

        if (activeTeam.size() < MAX_ACTIVE_POKEMON) {
            activeTeam.add(new Pokemon(pokemon)); // Add a copy
            System.out.println(pokemon.getName() + " added to active team!");
            return true;
        } else {
            storage.add(new Pokemon(pokemon));
            System.out.println(pokemon.getName() + " added to storage (active team full)");
            return true;
        }
    }

    /**
     * Releases a Pokemon from the trainer's collection
     * @param pokemon The Pokemon to release
     * @return true if released successfully
     */
    public boolean releasePokemon(Pokemon pokemon) {
        if (pokemon == null) return false;

        boolean releasedFromTeam = activeTeam.removeIf(p -> p.equals(pokemon));
        boolean releasedFromStorage = storage.removeIf(p -> p.equals(pokemon));

        if (releasedFromTeam || releasedFromStorage) {
            return true;
        }
        System.out.println("Pokemon not found in collection");
        return false;
    }

    /**
     * Switches a Pokemon between active team and storage
     * @param pokemon The Pokemon to switch
     * @return true if switched successfully
     */
    public boolean switchPokemon(Pokemon pokemon) {
        if (pokemon == null) return false;

        if (activeTeam.contains(pokemon)) {
            if (storage.size() >= MAX_ACTIVE_POKEMON) {
                System.out.println("Storage is full! Cannot switch.");
                return false;
            }
            activeTeam.remove(pokemon);
            storage.add(pokemon);
            System.out.println(pokemon.getName() + " moved to storage");
            return true;
        }
        else if (storage.contains(pokemon)) {
            if (activeTeam.size() >= MAX_ACTIVE_POKEMON) {
                System.out.println("Active team is full! Cannot switch.");
                return false;
            }
            storage.remove(pokemon);
            activeTeam.add(pokemon);
            System.out.println(pokemon.getName() + " moved to active team");
            return true;
        }

        System.out.println("Pokemon not found in collection");
        return false;
    }


    public boolean addItem(Item item, int quantity) {
        if (item == null || quantity <= 0) {
            System.out.println("Invalid item or quantity");
            return false;
        }

        if (!canAddItem(item, quantity)) {
            return false;
        }

        Optional<Item> existing = inventory.stream()
                .filter(i -> i.getName().equalsIgnoreCase(item.getName()))
                .findFirst();

        if (existing.isPresent()) {
            Item existingItem = existing.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            Item newItem = new Item(item);
            newItem.setQuantity(quantity);
            inventory.add(newItem);
        }

        System.out.println("Added " + quantity + " " + item.getName() + "(s) to inventory");
        return true;
    }
    /**
     * Purchases an item and adds to inventory
     *
     * @param item     The item to purchase
     * @param quantity
     * @return true if purchase was successful
     */
    public boolean buyItem(Item item, int quantity) {
        if (item == null || item.getBuyPrice() <= 0) {
            System.out.println("Item cannot be purchased");
            return false;
        }

        int totalCost = item.getBuyPrice() * quantity;
        if (money < totalCost) {
            System.out.println("Not enough money");
            return false;
        }

        // Check inventory limits (max 50 total items, max 99 of one item)
        int currentTotalItems = inventory.stream()
                .mapToInt(Item::getQuantity)
                .sum();

        if (currentTotalItems + quantity > MAX_TOTAL_ITEMS) {
            System.out.println("Cannot carry more than 50 items total");
            return false;
        }

        // Check if we already have this item
        Optional<Item> existingItem = inventory.stream()
                .filter(i -> i.getName().equalsIgnoreCase(item.getName()))
                .findFirst();

        if (existingItem.isPresent()) {
            int newQuantity = existingItem.get().getQuantity() + quantity;
            if (newQuantity > 99) {
                System.out.println("Cannot carry more than 99 of one item");
                return false;
            }
            existingItem.get().setQuantity(newQuantity);
        } else {
            // Check unique item limit
            if (inventory.size() >= MAX_UNIQUE_ITEMS) {
                System.out.println("Cannot carry more than 10 unique items");
                return false;
            }
            Item newItem = new Item(
                    item.getName(),
                    item.getDescription(),
                    item.getCategory(),
                    item.getBuyPrice(),
                    item.getSellPrice(),
                    item.getEffect()
            );
            newItem.setQuantity(quantity);
            inventory.add(newItem);
        }
        money -= totalCost;
        return true;
    }

    /**
     * Sells an item from inventory
     * @param itemName The item to sell
     * @return true if sale was successful
     */
    public boolean sellItem(String itemName, int quantity) {
        if (itemName == null || quantity <= 0) {
            System.out.println("Invalid input");
            return false;
        }

        Optional<Item> itemOpt = inventory.stream()
                .filter(i -> i.getName().equalsIgnoreCase(itemName))
                .findFirst();

        if (!itemOpt.isPresent()) {
            System.out.println("Item not found in inventory");
            return false;
        }

        Item item = itemOpt.get();
        if (item.getQuantity() < quantity) {
            System.out.println("Not enough items to sell");
            return false;
        }

        int totalValue = item.getSellPrice() * quantity;
        money += totalValue;

        if (item.getQuantity() == quantity) {
            inventory.remove(item);
        } else {
            item.setQuantity(item.getQuantity() - quantity);
        }

        System.out.printf("Sold %d %s for ₱%,d\n", quantity, item.getName(), totalValue);
        return true;
    }

    /**
     * Uses an item on a Pokemon.
     * @param item The item to use
     * @param pokemon The target Pokemon
     * @param pokedex The active Pokedex database to find evolution data
     * @param parent The JFrame parent component for dialogs
     * @return true if item was used successfully
     */
    public boolean useItem(Item item, Pokemon pokemon, Pokedex pokedex, JFrame parent) { // ADDED JFrame parent HERE
        if (!inventory.contains(item) || pokemon == null) {
            System.out.println("Item not in inventory or no Pokemon selected.");
            return false;
        }

        boolean evolutionTriggered = false;


        // Handle different item types
        switch (item.getCategory()) {
            case "Vitamin":
                System.out.println("Used " + item.getName() + " on " + pokemon.getName() + ". Effect: " + item.getEffect());
                switch (item.getName()) {
                    case "HP Up": pokemon.setHp((int)(pokemon.getHp() * 1.1)); break;
                    case "Protein": pokemon.setAttack((int)(pokemon.getAttack() * 1.1)); break;
                    case "Iron": pokemon.setDefense((int)(pokemon.getDefense() * 1.1)); break;
                    case "Carbos": pokemon.setSpeed((int)(pokemon.getSpeed() * 1.1)); break;
                    case "Zinc": // Affects multiple stats slightly as a fallback
                        pokemon.setHp((int)(pokemon.getHp() * 1.05));
                        pokemon.setAttack((int)(pokemon.getAttack() * 1.05));
                        pokemon.setDefense((int)(pokemon.getDefense() * 1.05));
                        pokemon.setSpeed((int)(pokemon.getSpeed() * 1.05));
                        break;
                }
                System.out.println(pokemon.getName() + "'s stats increased!");
                break;

            case "Evolution Stone":
                if (pokemon.canEvolveByStone(item)) {
                    evolutionTriggered = true;
                } else {
                    System.out.println("This stone has no effect on " + pokemon.getName());
                    return false;
                }
                break;

            case "Leveling Item":
                if (item.getName().equals("Rare Candy")) {
                // Show level-up message before potentially evolving
                JOptionPane.showMessageDialog(parent,
                        pokemon.getName() + " grew to level " + (pokemon.getLevel() + 1) + "!",
                        "Level Up!",
                        JOptionPane.INFORMATION_MESSAGE);

                if (pokemon.levelUp()) { // levelUp returns true if evolution conditions are met
                    evolutionTriggered = true;
                }
            }
                break;

            default:
                System.out.println("Item cannot be used this way");
                return false;
        }

        // Handle evolution separately after determining if it was triggered
        if (evolutionTriggered) {
            System.out.println("What? " + pokemon.getName() + " is evolving!");
            evolvePokemon(pokemon, pokedex, parent); // Pass 'parent' here
        }

        // Remove item from inventory after use
        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
        } else {
            inventory.remove(item);
        }
        return true;
    }

    /**
     * Evolves a Pokemon using data from the provided Pokedex.
     * @param pokemon The Pokemon to evolve
     * @param pokedex The Pokedex database to look up the evolved form
     * @param parent The JFrame parent component for dialogs
     */
    private void evolvePokemon(Pokemon pokemon, Pokedex pokedex, JFrame parent) {
        String originalName = pokemon.getName();

        // Find the evolved form in the provided Pokedex
        Pokemon evolvedForm = pokedex.getAllPokemon().stream()
                .filter(p -> p.getPokedexNumber() == pokemon.getEvolvesTo())
                .findFirst()
                .orElse(null);

        if (evolvedForm == null) {
            System.out.println("Evolution data not found!");
            JOptionPane.showMessageDialog(parent, "Evolution data not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Store current state that should be preserved
        List<Move> currentMoves = new ArrayList<>(pokemon.getMoveSet());
        Item heldItem = pokemon.getHeldItem();
        int currentLevel = pokemon.getLevel();

        // Perform the evolution by updating stats and info
        pokemon.evolve(
                evolvedForm.getPokedexNumber(),
                evolvedForm.getName(),
                evolvedForm.getType1(),
                evolvedForm.getType2(),
                evolvedForm.getHp(),
                evolvedForm.getAttack(),
                evolvedForm.getDefense(),
                evolvedForm.getSpeed()
        );

        // Restore attributes that don't change on evolution
        pokemon.setLevel(currentLevel);
        pokemon.setHeldItem(heldItem); // setHeldItem might not exist, but good practice

        // Re-learn moves
        currentMoves.forEach(pokemon::learnMove);

        System.out.println("Congratulations! Your " + originalName + " evolved into " + pokemon.getName() + "!");
        JOptionPane.showMessageDialog(parent,
                "Congratulations! Your " + originalName + " evolved into " + pokemon.getName() + "!",
                "Pokemon Evolution",
                JOptionPane.INFORMATION_MESSAGE);
    }



    /**
     * Teaches a move to a Pokemon
     * @param pokemon The Pokemon to teach
     * @param move The move to teach
     * @return true if move was taught successfully
     */
    public boolean teachMove(Pokemon pokemon, Move move) {
        if (!activeTeam.contains(pokemon) && !storage.contains(pokemon)) {
            System.out.println("Pokemon not in your collection");
            return false;
        }

        if (!isMoveCompatible(pokemon, move)) {
            System.out.println(move.getName() + " is incompatible with " + pokemon.getName());
            return false;
        }

        if (pokemon.learnMove(move)) {
            System.out.println(pokemon.getName() + " learned " + move.getName() + "!");
            return true;
        }

        System.out.println("Failed to teach move");
        return false;
    }


    private boolean canAddItem(Item item, int quantity) {
        // Check unique item limit
        boolean isNewItem = inventory.stream()
                .noneMatch(i -> i.getName().equalsIgnoreCase(item.getName()));

        if (isNewItem && inventory.size() >= MAX_UNIQUE_ITEMS) {
            System.out.println("Cannot carry more than " + MAX_UNIQUE_ITEMS + " unique items");
            return false;
        }

        // Check total items limit
        int currentTotal = inventory.stream()
                .mapToInt(Item::getQuantity)
                .sum();

        if (currentTotal + quantity > MAX_TOTAL_ITEMS) {
            System.out.println("Cannot carry more than " + MAX_TOTAL_ITEMS + " total items");
            return false;
        }

        // Check per-item quantity limit
        Optional<Item> existing = inventory.stream()
                .filter(i -> i.getName().equalsIgnoreCase(item.getName()))
                .findFirst();

        if (existing.isPresent() && existing.get().getQuantity() + quantity > 99) {
            System.out.println("Cannot carry more than 99 of one item");
            return false;
        }

        return true;
    }

    private boolean isMoveCompatible(Pokemon pokemon, Move move) {
        // Get all of Pokemon's types
        List<String> pokemonTypes = new ArrayList<>();
        pokemonTypes.add(pokemon.getType1());
        if (pokemon.getType2() != null) {
            pokemonTypes.add(pokemon.getType2());
        }

        // Get all of move's types
        List<String> moveTypes = new ArrayList<>();
        moveTypes.add(move.getType1());
        if (move.getType2() != null) {
            moveTypes.add(move.getType2());
        }

        // Check for any shared types
        return pokemonTypes.stream()
                .anyMatch(pType -> moveTypes.stream()
                        .anyMatch(mType -> mType.equalsIgnoreCase(pType)));
    }

    public boolean forgetMove(Pokemon pokemon, String moveName) {
        if (!activeTeam.contains(pokemon) && !storage.contains(pokemon)) {
            System.out.println("Pokemon not in your collection");
            return false;
        }

        if (pokemon.forgetMove(moveName)) {
            System.out.println(pokemon.getName() + " forgot " + moveName + "!");
            return true;
        }
        return false;
    }

    public int getTrainerId() { return trainerId; }
    public String getName() { return name; }
    public double getMoney(int i) { return money; }
    public List<Pokemon> getActiveTeam() { return new ArrayList<>(activeTeam); }
    public List<Pokemon> getStorage() { return new ArrayList<>(storage); }
    public List<Item> getInventory() { return new ArrayList<>(inventory); }
    public String getBirthdate() { return birthdate;}
    public void setBirthdate(String birthdate) { this.birthdate = birthdate;}
    public String getSex() { return sex; }

    public void setSex(String sex) {
        if (sex != null && (sex.equals("M") || sex.equals("F") || sex.equals("O"))) {
            this.sex = sex;
        } else {
            throw new IllegalArgumentException("Sex must be M, F, or O");
        }
    }


    public String getHometown() { return hometown;}
    public void setHometown(String hometown) { this.hometown = hometown;}
    public String getDescription() { return description;}

    public void setDescription(String description) { this.description = description;}

    /**
     * Returns a formatted string representation of the trainer
     * @return String containing trainer details
     */
    @Override
    public String toString() {
        return String.format("Trainer #%d: %s (Sex: %s, Hometown: %s)\nBirthdate: %s\nDescription: %s\nMoney: ₱%,.2f\nPokemon: %d active, %d in storage",
                trainerId, name, sex, hometown, birthdate, description, money,
                activeTeam.size(), storage.size());
    }
}