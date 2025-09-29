
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main class for the Enhanced Pokedex application.
 * Manages the overall system and coordinates between Pokemon, Move, Item, and Trainer modules.
 * It handles data loading from CSV files, menu navigation, and various management operations.
 */
public class EnhancedPokedex {
    // Static instances of our databases, accessible throughout the application
    static Pokedex pokedex = new Pokedex();
    private static MoveDatabase moveDatabase = new MoveDatabase();
    private static ItemDatabase itemDatabase = new ItemDatabase();
    private static TrainerDatabase trainerDB = new TrainerDatabase(); // Add this line
    private static Scanner scanner = new Scanner(System.in);

    /**
     * Main entry point of the application.
     * Displays and manages the main menu loop.
     * Loads all our data files, sets up some demo trainers, and keeps the main menu running.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Main menu loop
        loadPokemonData();
        loadItemData();
        loadMoveData();

        initializeTrainersWithPokemon(); // Initialize 5 trainers with 5 Pokemon each
        while (true) {
            System.out.println("\n=== Enhanced Pokedex ===");
            System.out.println("1. Pokemon Management");
            System.out.println("2. Moves Management");
            System.out.println("3. Item Management");
            System.out.println("4. Trainer Management");
            System.out.println("5. Exit");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: pokemonManagement(); break;
                case 2: movesManagement(); break;
                case 3: itemManagement(); break;
                case 4: trainerManagement(); break;
                case 5: System.exit(0);
                default: System.out.println("Invalid option!");
            }
        }
    }

    /**
     * Sets up our demo trainers with their Pokemon teams and items.
     * We created 5 trainers, for the test cases, and assign them initial Pokemon and items.
     */
    private static void initializeTrainersWithPokemon() {
        // Create 5 trainers
        Trainer[] trainers = new Trainer[5];
        trainers[0] = trainerDB.addTrainer("Ash Ketchum");
        trainers[1] = trainerDB.addTrainer("Misty");
        trainers[2] = trainerDB.addTrainer("Brock");
        trainers[3] = trainerDB.addTrainer("Gary Oak");
        trainers[4] = trainerDB.addTrainer("Professor Oak");

        // Assigns Pokemon to each trainer (first 25 Pokemon, 5 each).
        List<Pokemon> allPokemon = pokedex.getAllPokemon();
        for (int i = 0; i < trainers.length; i++) {
            Trainer trainer = trainers[i];
            int startIndex = i * 5;

            for (int j = 0; j < 5; j++) {
                if (startIndex + j < allPokemon.size()) {
                    Pokemon original = allPokemon.get(startIndex + j);

                    // Create a copy of the Pokemon to add to trainer
                    Pokemon copy = new Pokemon(
                            original.getPokedexNumber(),
                            original.getName(),
                            original.getType1(),
                            original.getType2(),
                            original.getLevel(),
                            original.getHp(),
                            original.getAttack(),
                            original.getDefense(),
                            original.getSpeed(),
                            original.getEvolvesFrom(),
                            original.getEvolvesTo(),
                            original.getEvolutionLevel(),
                            original.getEvolutionMethod(),
                            original.getEvolutionStoneType()
                    );
                    original.getMoveSet().forEach(copy::learnMove);
                    trainer.addPokemon(copy);
                }
            }
            trainer.getMoney(1000000);

            // Add Rare Candy and Moon Stone directly (bypass buy restrictions)
            Item rareCandy = itemDatabase.getAllItems().stream()
                    .filter(item -> item.getName().equals("Rare Candy"))
                    .findFirst()
                    .orElse(null);

            Item moonStone = itemDatabase.getAllItems().stream()
                    .filter(item -> item.getName().equals("Moon Stone"))
                    .findFirst()
                    .orElse(null);

            if (rareCandy != null) {
                trainer.addItem(rareCandy, 10); // Use addItem instead of buyItem
                System.out.println(trainer.getName() + " received 10 Rare Candies");
            }

            if (moonStone != null) {
                trainer.addItem(moonStone, 5); // Use addItem instead of buyItem
                System.out.println(trainer.getName() + " received 5 Moon Stones");
            }

            // Add some random items to each trainer
            List<Item> allItems = itemDatabase.getAllItems().stream()
                    .filter(item -> item.getBuyPrice() > 0) // Only items that can be bought
                    .collect(Collectors.toList());

            if (!allItems.isEmpty()) {
                int itemIndex = i * 3;
                Random random = new Random();

                if (itemIndex < allItems.size()) {
                    Item item1 = allItems.get(itemIndex % allItems.size());
                    int quantity1 = random.nextInt(5) + 1;
                    trainer.buyItem(item1, quantity1); // These can still be bought normally
                    System.out.println(trainer.getName() + " bought " + quantity1 + " " + item1.getName() + " for ₱" + (item1.getBuyPrice() * quantity1));

                    if ((itemIndex + 1) < allItems.size()) {
                        Item item2 = allItems.get((itemIndex + 1) % allItems.size());
                        int quantity2 = random.nextInt(5) + 1;
                        trainer.buyItem(item2, quantity2);
                        System.out.println(trainer.getName() + " bought " + quantity2 + " " + item2.getName() + " for ₱" + (item2.getBuyPrice() * quantity2));
                    }

                    if ((itemIndex + 2) < allItems.size()) {
                        Item item3 = allItems.get((itemIndex + 2) % allItems.size());
                        int quantity3 = random.nextInt(5) + 1;
                        trainer.buyItem(item3, quantity3);
                        System.out.println(trainer.getName() + " bought " + quantity3 + " " + item3.getName() + " for ₱" + (item3.getBuyPrice() * quantity3));
                    }
                }
            }
        }

        System.out.println("\nInitialized 5 trainers with Pokemon teams and items.");
    }

    /**
     * Presents search operation outcomes in a standardized output format.
     *
     * @param results The aggregated list of identified entities.
     * @param type The categorical designation of displayed entities (e.g., "Pokemon", "Moves").
     * @param searchCriteria A descriptive string detailing the search parameters employed.
     * @param <T> Generic type parameter representing the class of entities being displayed.
     */
    private static <T> void displaySearchResults(List<T> results, String type, String searchCriteria) {
        if (results.isEmpty()) {
            System.out.println("\nNo " + type + " found with " + searchCriteria);
        } else {
            System.out.println("\n=== Search Results (" + results.size() + " found) ===");
            results.forEach(System.out::println);
        }
    }

    /**
     * Loads all Pokemon data from the "POKEMONS.csv" file.
     * Handles the details of parsing the CSV and setting up each Pokemon's stats and moves.
     */
    private static void loadPokemonData() {
        try (BufferedReader br = new BufferedReader(new FileReader("POKEMONS.csv"))) {
            br.readLine(); // Corrected: Skips only the one header line
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                int number = Integer.parseInt(data[0]);
                String name = data[1];
                String type1 = data[2];
                String type2 = data[3].isEmpty() ? null : data[3];
                int level = Integer.parseInt(data[4]);
                int hp = Integer.parseInt(data[5]);
                int attack = Integer.parseInt(data[6]);
                int defense = Integer.parseInt(data[7]);
                int speed = Integer.parseInt(data[8]);
                int evolvesFrom = Integer.parseInt(data[9]);
                int evolvesTo = Integer.parseInt(data[10]);
                int evolutionLevel = Integer.parseInt(data[11]);
                String evolutionMethod = data.length > 12 && !data[12].isEmpty() ? data[12] : null;
                String evolutionStoneType = data.length > 13 && !data[13].isEmpty() ? data[13] : null;

                // This part remains the same
                Pokemon pokemon = new Pokemon(number, name, type1, type2, level, hp, attack, defense, speed,
                        evolvesFrom, evolvesTo, evolutionLevel, evolutionMethod, evolutionStoneType);

                // Load moves (no changes here)
                List<Move> moves = new ArrayList<>();
                for (int i = 14; i < data.length; i++) {
                    String moveName = data[i].trim();
                    if (!moveName.isEmpty()) {
                        List<Move> foundMoves = moveDatabase.searchByName(moveName);
                        if (!foundMoves.isEmpty()) {
                            moves.add(foundMoves.get(0));
                        }
                    }
                }
                moves.forEach(pokemon::learnMove);
                pokedex.addPokemon(pokemon);
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading Pokemon data: " + e.getMessage());
        }
    }

    /**
     * Loads item data from the "ITEMS.csv" file.
     * Creates item objects with their properties and adds them to our database.
     */
    private static void loadItemData() {
        try (BufferedReader br = new BufferedReader(new FileReader("ITEMS.csv"))) {
            br.readLine(); // Skip header line
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String name = data[0];
                String category = data[1];
                String description = data[2];
                String effect = data[3];
                int buyPrice = Integer.parseInt(data[4]);
                int sellPrice = Integer.parseInt(data[5]);

                Item item = new Item(name, description, category, buyPrice, sellPrice, effect);
                itemDatabase.addItem(item);
            }
        } catch (IOException e) {
            System.out.println("Error loading item data: " + e.getMessage());
        }
    }

    /**
     * Loads move data from the "MOVES.csv" file.
     * Sets up all the moves with their types and classifications.
     */
    private static void loadMoveData() {
        try (BufferedReader br = new BufferedReader(new FileReader("MOVES.csv"))) {
            br.readLine(); // Skip header line
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String name = data[0];
                String description = data[1];
                String classification = data[2];
                String type1 = data[3];
                String type2 = data.length > 4 && !data[4].isEmpty() ? data[4] : null;

                Move move = new Move(name, description, classification, type1, type2);
                moveDatabase.addMove(move);
            }
        } catch (IOException e) {
            System.out.println("Error loading move data: " + e.getMessage());
        }
    }

    /**
     * The Pokemon management hub.
     * Lets users add new Pokemon, view all Pokemon, or search for specific ones.
     */
    private static void pokemonManagement() {
        while (true) {
            System.out.println("\n=== Pokemon Management ===");
            System.out.println("1. Add New Pokemon");
            System.out.println("2. View All Pokemon");
            System.out.println("3. Search Pokemon");
            System.out.println("4. Return to Main Menu");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: addPokemon(); break;
                case 2: viewAllPokemon(); break;
                case 3: searchPokemon(); break;
                case 4: return;
                default: System.out.println("Invalid option!");
            }
        }
    }

    /**
     * The moves management section.
     * Handles adding new moves, viewing all moves, and searching the move database.
     */
    private static void movesManagement() {
        while (true) {
            System.out.println("\n=== Moves Management ===");
            System.out.println("1. Add New Move");
            System.out.println("2. View All Moves");
            System.out.println("3. Search Moves");
            System.out.println("4. Return to Main Menu");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: addMove(); break;
                case 2: viewAllMoves(); break;
                case 3: searchMoves(); break;
                case 4: return;
                default: System.out.println("Invalid option!");
            }
        }
    }

    /**
     * The item management interface.
     * Provides options to view all items or search for specific ones.
     */
    private static void itemManagement() {
        while (true) {
            System.out.println("\n=== Item Management ===");
            System.out.println("1. View All Items");
            System.out.println("2. Search Items");
            System.out.println("3. Return to Main Menu");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: viewAllItems(); break;
                case 2: searchItems(); break;
                case 3: return;
                default: System.out.println("Invalid option!");
            }
        }
    }

    // Pokemon Management methods
    private static void addPokemon() {
        System.out.println("\n=== Add New Pokemon ===");
        try {
            System.out.print("Pokedex Number: ");
            int number = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Name: ");
            String name = scanner.nextLine();

            System.out.print("Type 1: ");
            String type1 = scanner.nextLine();

            System.out.print("Type 2 (leave blank if none): ");
            String type2 = scanner.nextLine();
            if (type2.isEmpty()) type2 = null;

            System.out.print("Base Level: ");
            int level = scanner.nextInt();

            System.out.print("HP: ");
            int hp = scanner.nextInt();

            System.out.print("Attack: ");
            int attack = scanner.nextInt();

            System.out.print("Defense: ");
            int defense = scanner.nextInt();

            System.out.print("Speed: ");
            int speed = scanner.nextInt();

            System.out.print("Evolves From (0 if none): ");
            int evolvesFrom = scanner.nextInt();

            System.out.print("Evolves To (0 if none): ");
            int evolvesTo = scanner.nextInt();

            System.out.print("Evolution Level (0 if none): ");
            int evolutionLevel = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Evolution Method (level/stone/trade, leave blank if none): ");
            String evolutionMethod = scanner.nextLine();
            if (evolutionMethod.isEmpty()) evolutionMethod = null;

            String evolutionStoneType = null;
            if ("stone".equals(evolutionMethod)) {
                System.out.print("Evolution Stone Type: ");
                evolutionStoneType = scanner.nextLine();
            }

            Pokemon pokemon = new Pokemon(number, name, type1, type2, level, hp, attack, defense, speed,
                    evolvesFrom, evolvesTo, evolutionLevel, evolutionMethod, evolutionStoneType);
            pokedex.addPokemon(pokemon);
            System.out.println(name + " added successfully with moves: Tackle, Defend");
            pokemon.cry();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            scanner.nextLine();
        }
    }

    /**
     * Displays all Pokemon currently stored in the Pokedex.
     */
    private static void viewAllPokemon() {
        System.out.println("\n=== All Pokemon ===");
        // Extended the horizontal line to fit the new column
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------");
        // Added the "Moves" column to the header
        System.out.printf("%-5s %-15s %-15s %-10s %-10s %-10s %-10s %-10s %-15s %-15s %-30s\n",
                "#", "Name", "Type", "Level", "HP", "Attack", "Defense", "Speed", "Evolves From", "Evolves To", "Moves");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------");

        pokedex.getAllPokemon().forEach(p -> {
            String typeInfo = p.getType1() + (p.getType2() != null ? "/" + p.getType2() : "");

            String evolvesFrom = p.getEvolvesFrom() == 0 ? "None" :
                    pokedex.getAllPokemon().stream()
                            .filter(p2 -> p2.getPokedexNumber() == p.getEvolvesFrom())
                            .findFirst()
                            .map(Pokemon::getName)
                            .orElse("Unknown");

            String evolvesTo = p.getEvolvesTo() == 0 ? "None" :
                    pokedex.getAllPokemon().stream()
                            .filter(p2 -> p2.getPokedexNumber() == p.getEvolvesTo())
                            .findFirst()
                            .map(Pokemon::getName)
                            .orElse("Unknown");

            // Get the list of move names, joined by a comma
            String moves = p.getMoveSet().stream()
                    .map(Move::getName)
                    .collect(Collectors.joining(", "));

            // Added the moves string to the output
            System.out.printf("%-5s %-15s %-15s %-10s %-10s %-10s %-10s %-10s %-15s %-15s %-30s\n",
                    p.getPokedexNumber(),
                    p.getName(),
                    typeInfo,
                    p.getLevel(),
                    p.getHp(),
                    p.getAttack(),
                    p.getDefense(),
                    p.getSpeed(),
                    evolvesFrom,
                    evolvesTo,
                    moves);
        });
    }

    /**
     * Prompts the user to search for Pokemon by name or type and displays the results.
     */
    private static void searchPokemon() {
        System.out.println("\n=== Search Pokemon ===");
        System.out.println("1. Search by Name");
        System.out.println("2. Search by Type");
        System.out.println("3. View Pokemon Details");
        System.out.println("4. Return to Main Menu");
        System.out.print("Select an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.print("\nEnter Pokemon name: ");
                String name = scanner.nextLine();
                List<Pokemon> nameResults = pokedex.searchByName(name);
                displaySearchResultsWithEvolution(nameResults, "Pokemon", "name containing '" + name + "'");
                break;

            case 2:
                System.out.print("\nEnter Pokemon type: ");
                String type = scanner.nextLine();
                List<Pokemon> typeResults = pokedex.searchByType(type);
                displaySearchResultsWithEvolution(typeResults, "Pokemon", "type containing '" + type + "'");
                break;

            case 3:
                viewPokemonDetails();
                break;
            case 4:
                return;
            default:
                System.out.println("Invalid option!");
        }
    }

    private static void displaySearchResultsWithEvolution(List<Pokemon> results, String type, String searchCriteria) {
        if (results.isEmpty()) {
            System.out.println("\nNo " + type + " found with " + searchCriteria);
        } else {
            System.out.println("\n=== Search Results (" + results.size() + " found) ===");
            System.out.println("--------------------------------------------------------------------------------------------------------");
            System.out.printf("%-5s %-15s %-15s %-10s %-15s %-15s\n",
                    "#", "Name", "Type", "Level", "Evolves From", "Evolves To");
            System.out.println("--------------------------------------------------------------------------------------------------------");

            for (Pokemon p : results) {
                String typeInfo = p.getType1() + (p.getType2() != null ? "/" + p.getType2() : "");

                String evolvesFrom = p.getEvolvesFrom() == 0 ? "None" :
                        pokedex.getAllPokemon().stream()
                                .filter(p2 -> p2.getPokedexNumber() == p.getEvolvesFrom())
                                .findFirst()
                                .map(Pokemon::getName)
                                .orElse("Unknown");

                String evolvesTo = p.getEvolvesTo() == 0 ? "None" :
                        pokedex.getAllPokemon().stream()
                                .filter(p2 -> p2.getPokedexNumber() == p.getEvolvesTo())
                                .findFirst()
                                .map(Pokemon::getName)
                                .orElse("Unknown");

                System.out.printf("%-5d %-15s %-15s %-10d %-15s %-15s\n",
                        p.getPokedexNumber(),
                        p.getName(),
                        typeInfo,
                        p.getLevel(),
                        evolvesFrom,
                        evolvesTo);
            }
        }
    }

    // Moves Management methods
    private static void addMove() {
        System.out.println("\n=== Add New Move ===");
        try {
            System.out.print("Name: ");
            String name = scanner.nextLine();

            System.out.print("Description: ");
            String description = scanner.nextLine();

            System.out.print("Classification (TM/HM/Others): ");
            String classification = scanner.nextLine();

            System.out.print("Type 1: ");
            String type1 = scanner.nextLine();

            System.out.print("Type 2 (leave blank if none): ");
            String type2 = scanner.nextLine();
            if (type2.isEmpty()) type2 = null;

            Move move = new Move(name, description, classification, type1, type2);


            moveDatabase.addMove(move);
            System.out.println(name + " move added successfully!");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewAllMoves() {
        System.out.println("\n=== All Moves ===");
        moveDatabase.getAllMoves().forEach(System.out::println);
    }

    private static void searchMoves() {
        System.out.println("\n=== Search Moves ===");
        System.out.println("1. Search by Name");
        System.out.println("2. Search by Type");
        System.out.println("3. Search by Classification");
        System.out.println("4. Return to Moves Menu");
        System.out.print("Select an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.print("\nEnter move name: ");
                String name = scanner.nextLine();
                List<Move> nameResults = moveDatabase.searchByName(name);
                displaySearchResults(nameResults, "Moves", "name containing '" + name + "'");
                break;

            case 2:
                System.out.print("\nEnter move type: ");
                String type = scanner.nextLine();
                List<Move> typeResults = moveDatabase.searchByType(type);
                displaySearchResults(typeResults, "Moves", "type containing '" + type + "'");
                break;

            case 3:
                System.out.print("\nEnter classification: ");
                String classification = scanner.nextLine();
                List<Move> classResults = moveDatabase.searchByClassification(classification);
                displaySearchResults(classResults, "Moves", "classification containing '" + classification + "'");
                break;

            case 4:
                return;

            default:
                System.out.println("Invalid option!");
        }
    }

    private static void viewPokemonDetails() {
        System.out.print("\nEnter Pokemon name or ID: ");
        String searchTerm = scanner.nextLine();

        List<Pokemon> results;
        try {
            int id = Integer.parseInt(searchTerm);
            results = pokedex.getAllPokemon().stream()
                    .filter(p -> p.getPokedexNumber() == id)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            results = pokedex.searchByName(searchTerm);
        }

        if (results.isEmpty()) {
            System.out.println("No Pokemon found!");
            return;
        }

        Pokemon pokemon = results.get(0);
        System.out.println("\n=== " + pokemon.getName() + " ===");
        System.out.println("Pokedex #: " + pokemon.getPokedexNumber());
        System.out.println("Type: " + pokemon.getType1() +
                (pokemon.getType2() != null ? "/" + pokemon.getType2() : ""));
        System.out.println("Level: " + pokemon.getLevel());
        System.out.println("Stats: HP=" + pokemon.getHp() +
                " ATK=" + pokemon.getAttack() +
                " DEF=" + pokemon.getDefense() +
                " SPD=" + pokemon.getSpeed());

        // Add evolution info
        String evolvesFrom = pokemon.getEvolvesFrom() == 0 ? "None" :
                pokedex.getAllPokemon().stream()
                        .filter(p -> p.getPokedexNumber() == pokemon.getEvolvesFrom())
                        .findFirst()
                        .map(Pokemon::getName)
                        .orElse("Unknown");

        String evolvesTo = pokemon.getEvolvesTo() == 0 ? "None" :
                pokedex.getAllPokemon().stream()
                        .filter(p -> p.getPokedexNumber() == pokemon.getEvolvesTo())
                        .findFirst()
                        .map(Pokemon::getName)
                        .orElse("Unknown");

        System.out.println("Evolves From: " + evolvesFrom);
        System.out.println("Evolves To: " + evolvesTo);

        if (pokemon.getEvolutionLevel() > 0) {
            System.out.println("Evolution Level: " + pokemon.getEvolutionLevel());
        }
        if (pokemon.getEvolutionMethod() != null) {
            System.out.println("Evolution Method: " + pokemon.getEvolutionMethod());
        }
        if (pokemon.getEvolutionStoneType() != null) {
            System.out.println("Evolution Stone: " + pokemon.getEvolutionStoneType());
        }

        System.out.println("\nMoves:");
        pokemon.getMoveSet().forEach(move -> {
            System.out.println("- " + move.getName() +
                    " [" + move.getType1() +
                    (move.getType2() != null ? "/" + move.getType2() : "") +
                    "] (" + move.getClassification() + ")");
            System.out.println("  " + move.getDescription());
        });
    }


    // Item Management methods
    private static void viewAllItems() {
        System.out.println("\n=== All Items ===");
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-15s %-12s %-40s %-20s %s\n", "Name", "Category", "Description", "Price Info", "Effect");
        System.out.println("------------------------------------------------------------------------------------------------------------------");

        itemDatabase.getAllItems().forEach(item -> {
            String priceInfo;
            if (item.getBuyPrice() == 0) {
                priceInfo = "Cannot be bought | Sell: ₱" + item.getSellPrice();
            } else {
                priceInfo = "Buy: ₱" + item.getBuyPrice() + " | Sell: ₱" + item.getSellPrice();
            }

            System.out.printf("%-15s %-12s %-40s %-20s %s\n",
                    item.getName(),
                    item.getCategory(),
                    item.getDescription(),
                    priceInfo,
                    item.getEffect());
        });
    }

    /**
     * Prompts the user to search for items by name or category and displays the results.
     */

    private static void searchItems() {
        System.out.println("\n=== Search Items ===");
        System.out.println("1. Search by Name");
        System.out.println("2. Search by Category");
        System.out.println("3. Return to Items Menu");
        System.out.print("Select an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.print("\nEnter item name: ");
                String name = scanner.nextLine();
                List<Item> nameResults = itemDatabase.searchByName(name);
                displaySearchResults(nameResults, "Items", "name containing '" + name + "'");
                break;

            case 2:
                System.out.print("\nEnter category: ");
                String category = scanner.nextLine();
                List<Item> categoryResults = itemDatabase.searchByCategory(category);
                displaySearchResults(categoryResults, "Items", "category containing '" + category + "'");
                break;

            case 3:
                return;

            default:
                System.out.println("Invalid option!");
        }
    }

    /**
     * The trainer management interface.
     * Allows users to add new trainers, view existing trainers, or manage individual trainers.
     */
    private static void trainerManagement() {
        while (true) {
            System.out.println("\n=== Trainer Management ===");
            System.out.println("1. Register New Trainer");
            System.out.println("2. View All Trainers");
            System.out.println("3. Search Trainer by Name");
            System.out.println("4. Search Trainer by Pokemon");
            System.out.println("5. Manage Specific Trainer");
            System.out.println("6. Return to Main Menu");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: registerTrainer(); break;
                case 2: viewAllTrainers(); break;
                case 3: searchTrainerByName(); break;
                case 4: searchTrainerByPokemon(); break;
                case 5: manageSpecificTrainer(); break;
                case 6: return;
                default: System.out.println("Invalid option!");
            }
        }
    }
    /**
     * Registers a new trainer in the system
     */
    private static void registerTrainer() {
        System.out.println("\n=== Register New Trainer ===");

        try {
            // Name (required)
            System.out.print("Enter trainer name: ");
            String name = scanner.nextLine();
            if (name.trim().isEmpty()) {
                throw new IllegalArgumentException("Trainer name cannot be blank");
            }

            // Birthdate
            System.out.print("Enter birthdate (YYYY-MM-DD): ");
            String birthdate = scanner.nextLine();

            // Sex
            System.out.print("Enter sex (M/F/O): ");
            String sex = scanner.nextLine().toUpperCase();
            while (!sex.matches("[MFO]")) {
                System.out.println("Invalid input! Please enter M, F, or O");
                System.out.print("Enter sex (M/F/O): ");
                sex = scanner.nextLine().toUpperCase();
            }

            // Hometown
            System.out.print("Enter hometown: ");
            String hometown = scanner.nextLine();

            // Description
            System.out.print("Enter description: ");
            String description = scanner.nextLine();

            // Create trainer with all details
            Trainer newTrainer = trainerDB.addTrainer(name);
            // Set additional properties (we'll need to add setters to Trainer class)
            newTrainer.setBirthdate(birthdate);
            newTrainer.setSex(sex);
            newTrainer.setHometown(hometown);
            newTrainer.setDescription(description);

            System.out.println("Successfully registered trainer: " + newTrainer.getName());
            System.out.println("Assigned ID: " + newTrainer.getTrainerId());

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Displays all registered trainers
     */
    private static void viewAllTrainers() {
        System.out.println("\n=== All Registered Trainers ===");
        List<Trainer> trainers = trainerDB.getAllTrainers();

        if (trainers.isEmpty()) {
            System.out.println("No trainers registered yet!");
        } else {
            System.out.println("--------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-5s %-20s %-10s %-15s %-10s %-10s %-30s\n",
                    "ID", "Name", "Sex", "Hometown", "Active", "Storage", "Pokemon Details");
            System.out.println("--------------------------------------------------------------------------------------------------------------------");

            for (Trainer t : trainers) {
                // Get first 3 Pokemon details for display
                String pokemonDetails = t.getActiveTeam().stream()
                        .limit(3)
                        .map(p -> String.format("%s (Lv.%d)", p.getName(), p.getLevel()))
                        .collect(Collectors.joining(", "));
                if (pokemonDetails.isEmpty()) pokemonDetails = "No Pokemon";

                System.out.printf("%-5d %-20s %-10s %-15s %-10d %-10d %-30s\n",
                        t.getTrainerId(),
                        t.getName(),
                        t.getSex(),
                        t.getHometown(),
                        t.getActiveTeam().size(),
                        t.getStorage().size(),
                        pokemonDetails + (t.getActiveTeam().size() > 3 ? ", ..." : ""));
            }

            System.out.println("\nKey:");
            System.out.println("- Active: Number of Pokemon in battle lineup (max 6)");
            System.out.println("- Storage: Number of Pokemon in backup storage");
            System.out.println("- Pokemon Details: Shows first 3 active Pokemon with their levels");
        }
    }
    /**
     * Searches for trainers by name (partial match)
     */
    private static void searchTrainerByName() {
        System.out.println("\n=== Search Trainer by Name ===");
        System.out.print("Enter trainer name: ");
        String name = scanner.nextLine();

        List<Trainer> results = trainerDB.searchByName(name);
        displayTrainerResults(results, "name containing '" + name + "'");
    }
    /**
     * Searches for trainers by Pokemon in their team
     */
    private static void searchTrainerByPokemon() {
        System.out.println("\n=== Search Trainer by Pokemon ===");
        System.out.print("Enter Pokemon name: ");
        String pokemonName = scanner.nextLine();

        List<Trainer> results = trainerDB.searchByPokemon(pokemonName);
        displayTrainerResults(results, "Pokemon '" + pokemonName + "' in team");
    }

    /**
     * Displays trainer search results
     */
    private static void displayTrainerResults(List<Trainer> results, String criteria) {
        if (results.isEmpty()) {
            System.out.println("No trainers found with " + criteria);
        } else {
            System.out.println("\n=== Search Results (" + results.size() + " found) ===");

            for (Trainer t : results) {
                // Trainer basic info
                System.out.println("\n─────────────────────────────────────────────────────────────────────────────────────");
                System.out.printf("Trainer #%d: %s\n", t.getTrainerId(), t.getName());
                System.out.printf("Sex: %s | Hometown: %s | Money: ₱%,.2f\n",
                        t.getSex(), t.getHometown(), t.getMoney(1000000));

                // Active Pokemon team
                System.out.println("\nActive Pokemon (" + t.getActiveTeam().size() + "/6):");
                if (t.getActiveTeam().isEmpty()) {
                    System.out.println("  No active Pokemon");
                } else {
                    t.getActiveTeam().forEach(p ->
                            System.out.printf("  %s (Lv.%d) [%s%s] HP:%d ATK:%d DEF:%d SPD:%d\n",
                                    p.getName(),
                                    p.getLevel(),
                                    p.getType1(),
                                    p.getType2() != null ? "/" + p.getType2() : "",
                                    p.getHp(),
                                    p.getAttack(),
                                    p.getDefense(),
                                    p.getSpeed()));
                }

                // Storage count
                System.out.println("\nStorage: " + t.getStorage().size() + " Pokemon");

                // Inventory
                System.out.println("\nInventory (" + t.getInventory().size() + " unique items):");
                if (t.getInventory().isEmpty()) {
                    System.out.println("  No items");
                } else {
                    t.getInventory().forEach(item ->
                            System.out.printf("  %-15s x%-3d (%s) - %s\n",
                                    item.getName(),
                                    item.getQuantity(),
                                    item.getCategory(),
                                    item.getEffect()));
                }
                System.out.println("─────────────────────────────────────────────────────────────────────────────────────");
            }

            System.out.println("\nEnd of results");
        }
    }
    /**
     * Manages a specific trainer's profile
     */
    private static void manageSpecificTrainer() {
        System.out.println("\n=== Manage Trainer ===");
        System.out.print("Enter Trainer ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Trainer trainer = trainerDB.getTrainerById(id);
        if (trainer == null) {
            System.out.println("Trainer not found!");
            return;
        }

        trainerProfileMenu(trainer);
    }
    /**
     * Trainer profile management submenu
     * @param trainer The trainer being managed
     */
    private static void trainerProfileMenu(Trainer trainer) {
        while (true) {
            System.out.println("\n=== Trainer Profile: " + trainer.getName() + " ===");
            System.out.println("1. View Team");
            System.out.println("2. View Storage");
            System.out.println("3. View Inventory");
            System.out.println("4. Add Pokemon");
            System.out.println("5. Release Pokemon");
            System.out.println("6. Switch Pokemon");
            System.out.println("7. Teach Move");
            System.out.println("8. Buy Item");
            System.out.println("9. Sell Item");
            System.out.println("10. Use Item");
            System.out.println("11. Return to Trainer Menu");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: viewTrainerTeam(trainer); break;
                case 2: viewTrainerStorage(trainer); break;
                case 3: viewTrainerInventory(trainer); break;
                case 4: addPokemonToTrainer(trainer); break;
                case 5: releasePokemonFromTrainer(trainer); break;
                case 6: switchPokemon(trainer); break;
                case 7: teachMoveToPokemon(trainer); break;
                case 8: buyItemForTrainer(trainer); break;
                case 9: sellItemFromTrainer(trainer); break;
                case 10: useTrainerItem(trainer); break;
                case 11: return;
                default: System.out.println("Invalid option!");
            }
        }
    }
    /**
     * Displays a trainer's Pokemon team
     */
    private static void viewTrainerTeam(Trainer trainer) {
        System.out.println("\n=== " + trainer.getName() + "'s Team ===");
        System.out.printf("Active Pokemon: %d/6\n\n", trainer.getActiveTeam().size());

        if (trainer.getActiveTeam().isEmpty()) {
            System.out.println("No Pokemon in team!");
        } else {
            // Table header
            System.out.println("------------------------------------------------------------------------------------------------");
            System.out.printf("%-15s %-8s %-15s %-6s %-6s %-6s %-6s %-30s\n",
                    "Name", "Level", "Type(s)", "HP", "ATK", "DEF", "SPD", "Moves");
            System.out.println("------------------------------------------------------------------------------------------------");

            for (Pokemon pokemon : trainer.getActiveTeam()) {
                // Get types
                String types = pokemon.getType1() +
                        (pokemon.getType2() != null ? "/" + pokemon.getType2() : "");

                // Get moves (show first 2 + count of remaining)
                List<Move> moveSet = pokemon.getMoveSet();
                String moves;
                if (moveSet.isEmpty()) {
                    moves = "No moves";
                } else {
                    moves = moveSet.stream()
                            .limit(2)
                            .map(Move::getName)
                            .collect(Collectors.joining(", "));

                    if (moveSet.size() > 2) {
                        moves += " (+" + (moveSet.size() - 2) + " more)";
                    }
                }

                System.out.printf("%-15s %-8d %-15s %-6d %-6d %-6d %-6d %-30s\n",
                        pokemon.getName(),
                        pokemon.getLevel(),
                        types,
                        pokemon.getHp(),
                        pokemon.getAttack(),
                        pokemon.getDefense(),
                        pokemon.getSpeed(),
                        moves);
            }
        }
    }

    private static String getPokemonNameById(int id) {
        Pokemon pokemon = EnhancedPokedex.pokedex.getAllPokemon().stream()
                .filter(p -> p.getPokedexNumber() == id)
                .findFirst()
                .orElse(null);
        return pokemon != null ? pokemon.getName() : "Unknown";
    }


    private static void viewTrainerStorage(Trainer trainer) {
        System.out.println("\n=== " + trainer.getName() + "'s Storage ===");
        List<Pokemon> storage = trainer.getStorage();
        if (storage.isEmpty()) {
            System.out.println("Storage is empty!");
        } else {
            storage.forEach(p -> System.out.println(p.getName() + " (Lv." + p.getLevel() + ")"));
        }
    }

    private static void viewTrainerInventory(Trainer trainer) {
        System.out.println("\n=== " + trainer.getName() + "'s Inventory ===");
        System.out.printf("₱%,.2f available\n", trainer.getMoney(1000000));

        List<Item> inventory = trainer.getInventory();
        if (inventory.isEmpty()) {
            System.out.println("Inventory is empty!");
        } else {
            System.out.println("--------------------------------------------------");
            System.out.printf("%-20s %-15s %-10s %s\n", "Item", "Category", "Quantity", "Effect");
            System.out.println("--------------------------------------------------");
            inventory.forEach(item -> {
                System.out.printf("%-20s %-15s %-10d %s\n",
                        item.getName(),
                        item.getCategory(),
                        item.getQuantity(),
                        item.getEffect());
            });
        }
    }

    /**
     * Adds a Pokemon to the trainer's collection. Prioritizes active team, then storage.
     * @param trainer
     * @return true if added successfully.
     */
    private static void addPokemonToTrainer(Trainer trainer) {
        System.out.println("\n=== Add Pokemon to Team ===");
        System.out.print("Enter Pokemon name or ID: ");
        String searchTerm = scanner.nextLine();

        List<Pokemon> results;
        try {
            // Try to parse as ID first
            int id = Integer.parseInt(searchTerm);
            results = pokedex.getAllPokemon().stream()
                    .filter(p -> p.getPokedexNumber() == id)
                    .collect(Collectors.toList());

            if (results.isEmpty()) {
                System.out.println("No Pokemon found with ID: " + id);
                return;
            }
        } catch (NumberFormatException e) {
            // If not a number, search by name
            results = pokedex.searchByName(searchTerm);

            if (results.isEmpty()) {
                System.out.println("No Pokemon found with name: " + searchTerm);
                return;
            }
        }

        System.out.println("\nSelect a Pokemon to add:");
        for (int i = 0; i < results.size(); i++) {
            Pokemon p = results.get(i);
            System.out.printf("%d. %s (#%03d) [Lv.%d %s%s]\n",
                    i + 1,
                    p.getName(),
                    p.getPokedexNumber(),
                    p.getLevel(),
                    p.getType1(),
                    p.getType2() != null ? "/" + p.getType2() : "");
        }

        System.out.print("Enter choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > results.size()) {
            System.out.println("Invalid choice!");
            return;
        }

        Pokemon selected = results.get(choice-1);
        // Create new Pokemon with all parameters
        Pokemon newPokemon = new Pokemon(
                selected.getPokedexNumber(),
                selected.getName(),
                selected.getType1(),
                selected.getType2(),
                selected.getLevel(),
                selected.getHp(),
                selected.getAttack(),
                selected.getDefense(),
                selected.getSpeed(),
                selected.getEvolvesFrom(),
                selected.getEvolvesTo(),
                selected.getEvolutionLevel(),
                selected.getEvolutionMethod(),
                selected.getEvolutionStoneType()
        );

        // Copy moves from the original Pokemon
        selected.getMoveSet().forEach(newPokemon::learnMove);

        if (trainer.addPokemon(newPokemon)) {
            System.out.println(selected.getName() + " added to " + trainer.getName() + "'s team!");
        } else {
            System.out.println("Failed to add " + selected.getName() + " to team!");
        }
    }

    /**
     * Releases a Pokemon from the trainer's collection (either active team or storage).
     * @param trainer The Pokemon to release.
     * @return true if released successfully.
     */
    private static void releasePokemonFromTrainer(Trainer trainer) {
        System.out.println("\n=== Release Pokemon ===");
        System.out.println("1. From Active Team");
        System.out.println("2. From Storage");
        System.out.print("Select source: ");

        int source = scanner.nextInt();
        scanner.nextLine();

        List<Pokemon> pokemonList = (source == 1) ? trainer.getActiveTeam() : trainer.getStorage();

        if (pokemonList.isEmpty()) {
            System.out.println("No Pokemon in " + (source == 1 ? "team" : "storage"));
            return;
        }

        System.out.println("Select Pokemon to release:");
        for (int i = 0; i < pokemonList.size(); i++) {
            System.out.println((i+1) + ". " + pokemonList.get(i).getName());
        }

        System.out.print("Enter choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > pokemonList.size()) {
            System.out.println("Invalid choice!");
            return;
        }

        Pokemon toRelease = pokemonList.get(choice-1);
        if (trainer.releasePokemon(toRelease)) {
            System.out.println(toRelease.getName() + " was released!");
        }
    }

    /**
     * Switches a Pokemon between active team and storage.
     * @param trainer The Pokemon to switch.
     * @return true if switched successfully.
     */
    private static void switchPokemon(Trainer trainer) {
        System.out.println("\n=== Switch Pokemon ===");
        System.out.println("1. Team → Storage");
        System.out.println("2. Storage → Team");
        System.out.print("Select direction: ");

        int direction = scanner.nextInt();
        scanner.nextLine();

        List<Pokemon> fromList = (direction == 1) ? trainer.getActiveTeam() : trainer.getStorage();
        String fromName = (direction == 1) ? "team" : "storage";
        String toName = (direction == 1) ? "storage" : "team";

        if (fromList.isEmpty()) {
            System.out.println("No Pokemon in " + fromName);
            return;
        }

        System.out.println("Select Pokemon to move from " + fromName + " to " + toName + ":");
        for (int i = 0; i < fromList.size(); i++) {
            System.out.println((i+1) + ". " + fromList.get(i).getName());
        }

        System.out.print("Enter choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > fromList.size()) {
            System.out.println("Invalid choice!");
            return;
        }

        Pokemon toSwitch = fromList.get(choice-1);
        if (trainer.switchPokemon(toSwitch)) {
            System.out.println(toSwitch.getName() + " moved to " + toName);
        }
    }

    private static void teachMoveToPokemon(Trainer trainer) {
        System.out.println("\n=== Move Management ===");
        viewTrainerTeam(trainer);

        if (trainer.getActiveTeam().isEmpty()) {
            return;
        }

        System.out.print("Select Pokemon (1-" + trainer.getActiveTeam().size() + "): ");
        int pokeChoice = scanner.nextInt();
        scanner.nextLine();

        if (pokeChoice < 1 || pokeChoice > trainer.getActiveTeam().size()) {
            System.out.println("Invalid choice!");
            return;
        }

        Pokemon pokemon = trainer.getActiveTeam().get(pokeChoice-1);

        while (true) {
            System.out.println("\nCurrent moves for " + pokemon.getName() + ":");
            List<Move> currentMoves = pokemon.getMoveSet();
            if (currentMoves.isEmpty()) {
                System.out.println("No moves known");
            } else {
                for (int i = 0; i < currentMoves.size(); i++) {
                    System.out.println((i+1) + ". " + currentMoves.get(i) +
                            (currentMoves.get(i).isHM() ? " (HM - cannot forget)" : ""));
                }
            }

            System.out.println("\nOptions:");
            System.out.println("1. Teach new move");
            System.out.println("2. Forget a move");
            System.out.println("3. Back to trainer menu");
            System.out.print("Select option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            if (option == 1) {
                // Teach new move
                System.out.println("\nAvailable Moves:");
                List<Move> allMoves = moveDatabase.getAllMoves();
                for (int i = 0; i < allMoves.size(); i++) {
                    System.out.println((i+1) + ". " + allMoves.get(i) +
                            (allMoves.get(i).isHM() ? " (HM)" : ""));
                }

                System.out.print("Select move to teach (0 to cancel): ");
                int moveChoice = scanner.nextInt();
                scanner.nextLine();

                if (moveChoice == 0) {
                    continue;
                }

                if (moveChoice < 1 || moveChoice > allMoves.size()) {
                    System.out.println("Invalid choice!");
                    continue;
                }

                Move move = allMoves.get(moveChoice-1);

                if (move.isHM()) {
                    System.out.println("Note: HM moves bypass the 4-move limit and cannot be forgotten.");
                }

                trainer.teachMove(pokemon, move);
            }
            else if (option == 2) {
                // Forget a move
                if (currentMoves.isEmpty()) {
                    System.out.println("No moves to forget!");
                    continue;
                }

                System.out.print("Enter move number to forget (0 to cancel): ");
                int moveToForget = scanner.nextInt();
                scanner.nextLine();

                if (moveToForget == 0) {
                    continue;
                }

                if (moveToForget < 1 || moveToForget > currentMoves.size()) {
                    System.out.println("Invalid choice!");
                    continue;
                }

                String moveName = currentMoves.get(moveToForget-1).getName();
                trainer.forgetMove(pokemon, moveName);
            }
            else if (option == 3) {
                return;
            }
            else {
                System.out.println("Invalid option!");
            }
        }
    }

    /**
     * Guides a trainer through the process of selling an item from their inventory.
     * Checks if the trainer has the item and sufficient quantity.
     *
     * @param trainer The trainer who is selling the item.
     */
    private static void sellItemFromTrainer(Trainer trainer) {
        viewTrainerInventory(trainer);

        if (trainer.getInventory().isEmpty()) {
            return;
        }

        System.out.print("Enter item name to sell: ");
        String itemName = scanner.nextLine();

        System.out.print("Enter quantity to sell: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        trainer.sellItem(itemName, quantity);
    }

    private static void useTrainerItem(Trainer trainer) {
        System.out.println("\n=== Use Item ===");
        List<Item> inventory = trainer.getInventory();

        if (inventory.isEmpty()) {
            System.out.println("Your inventory is empty!");
            return;
        }

        // Display numbered list of items
        System.out.println("Your items:");
        for (int i = 0; i < inventory.size(); i++) {
            Item item = inventory.get(i);
            System.out.printf("%2d. %-15s (x%d) - %s\n",
                    i + 1,
                    item.getName(),
                    item.getQuantity(),
                    item.getEffect());
        }

        System.out.print("\nEnter item number or name: ");
        String input = scanner.nextLine().trim().toLowerCase();

        Item selectedItem = null;

        try {
            // Try to parse as number first
            int choice = Integer.parseInt(input);
            if (choice >= 1 && choice <= inventory.size()) {
                selectedItem = inventory.get(choice - 1);
            } else {
                System.out.println("Invalid item number!");
                return;
            }
        } catch (NumberFormatException e) {
            // If not a number, search by name (partial match)
            List<Item> matches = inventory.stream()
                    .filter(item -> item.getName().toLowerCase().contains(input))
                    .collect(Collectors.toList());

            if (matches.isEmpty()) {
                System.out.println("No items found matching: " + input);
                return;
            } else if (matches.size() == 1) {
                selectedItem = matches.get(0);
            } else {
                System.out.println("Multiple items match:");
                for (int i = 0; i < matches.size(); i++) {
                    System.out.printf("%d. %s\n", i + 1, matches.get(i).getName());
                }
                System.out.print("Please select one by number: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                if (choice < 1 || choice > matches.size()) {
                    System.out.println("Invalid selection!");
                    return;
                }
                selectedItem = matches.get(choice - 1);
            }
        }

        System.out.println("\nSelect target Pokemon:");
        List<Pokemon> team = trainer.getActiveTeam();
        if (team.isEmpty()) {
            System.out.println("No Pokemon in your team!");
            return;
        }

        for (int i = 0; i < team.size(); i++) {
            Pokemon p = team.get(i);
            System.out.printf("%d. %s (Lv.%d %s%s) HP:%d ATK:%d DEF:%d SPD:%d\n",
                    i + 1,
                    p.getName(),
                    p.getLevel(),
                    p.getType1(),
                    p.getType2() != null ? "/" + p.getType2() : "",
                    p.getHp(),
                    p.getAttack(),
                    p.getDefense(),
                    p.getSpeed());
        }

        System.out.print("Enter Pokemon number: ");
        int pokeChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (pokeChoice < 1 || pokeChoice > team.size()) {
            System.out.println("Invalid choice!");
            return;
        }

        Pokemon targetPokemon = team.get(pokeChoice - 1);

        if (trainer.useItem(selectedItem, targetPokemon, pokedex, null)) {
            System.out.println(" ");
        } else {
            System.out.println("Failed to use item!");
        }
    }

    /**
     * Guides a trainer through the process of buying an item from the item database.
     * Checks if the item exists and if the trainer has enough money.
     *
     * @param trainer The trainer who is buying the item.
     */
    private static void buyItemForTrainer(Trainer trainer) {
        System.out.println("\n=== Poke Mart ===");
        System.out.printf("Available funds: ₱%,.2f\n", trainer.getMoney(1000000));
        System.out.println("\nAvailable Items:");

        List<Item> purchasableItems = itemDatabase.getAllItems().stream()
                .filter(item -> item.getBuyPrice() > 0)
                .collect(Collectors.toList());

        // Display items with numbering
        for (int i = 0; i < purchasableItems.size(); i++) {
            Item item = purchasableItems.get(i);
            System.out.printf("%2d. %-15s ₱%,-8d %s\n",
                    i + 1,
                    item.getName(),
                    item.getBuyPrice(),
                    item.getEffect());
        }

        System.out.print("\nEnter item number to buy (0 to cancel): ");
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice == 0) {
                System.out.println("Purchase cancelled.");
                return;
            }

            if (choice < 1 || choice > purchasableItems.size()) {
                System.out.println("Invalid item number!");
                return;
            }

            Item selectedItem = purchasableItems.get(choice - 1);

            System.out.print("Enter quantity to buy: ");
            int quantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (quantity < 1) {
                System.out.println("Quantity must be at least 1!");
                return;
            }

            // Calculate total cost
            int totalCost = selectedItem.getBuyPrice() * quantity;

            if (trainer.getMoney(1000000) < totalCost) {
                System.out.printf("Not enough money! Need ₱%,d but only have ₱%,.2f\n",
                        totalCost, trainer.getMoney(1000000));
                return;
            }

            if (trainer.buyItem(selectedItem, quantity)) {
                System.out.printf("Purchased %d %s for ₱%,d\n",
                        quantity,
                        selectedItem.getName(),
                        totalCost);
            } else {
                System.out.println("Purchase failed! Check your inventory limits.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input! Please enter numbers only.");
            scanner.nextLine();
        }
    }
}
