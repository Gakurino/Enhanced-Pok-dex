import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Central repository for managing all trainer data in the Enhanced Pokedex system.
 * Handles storage, retrieval, and search operations for trainer records.
 */
public class TrainerDatabase {
    private List<Trainer> trainers;
    private int lastAssignedId;

    /**
     * Initializes a new TrainerDatabase with empty storage
     */
    public TrainerDatabase() {
        this.trainers = new ArrayList<>();
        this.lastAssignedId = 0;
    }

    /**
     * Registers a new trainer and assigns a unique ID
     * @param name The trainer's name
     * @return The newly created Trainer object
     * @throws IllegalArgumentException if name is blank
     */
    public Trainer addTrainer(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Trainer name cannot be blank");
        }

        // Check for duplicate names
        if (trainers.stream().anyMatch(t -> t.getName().equalsIgnoreCase(name))) {
            throw new IllegalArgumentException("Trainer name already exists");
        }

        Trainer newTrainer = new Trainer(++lastAssignedId, name);
        trainers.add(newTrainer);
        return newTrainer;
    }

    /**
     * Retrieves a trainer by their unique ID
     * @param id The trainer ID to search for
     * @return The Trainer object, or null if not found
     */
    public Trainer getTrainerById(int id) {
        return trainers.stream()
                .filter(t -> t.getTrainerId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Searches for trainers by name (case-insensitive partial match)
     * @param name The name or partial name to search
     * @return List of matching trainers (empty if none found)
     */
    public List<Trainer> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String searchTerm = name.toLowerCase();
        return trainers.stream()
                .filter(t -> t.getName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    /**
     * Searches for trainers by Pokemon in their active team
     * @param pokemonName The Pokemon name to search for
     * @return List of trainers with matching Pokemon
     */
    public List<Trainer> searchByPokemon(String pokemonName) {
        if (pokemonName == null || pokemonName.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String searchTerm = pokemonName.toLowerCase();
        return trainers.stream()
                .filter(t -> t.getActiveTeam().stream()
                        .anyMatch(p -> p.getName().toLowerCase().contains(searchTerm)))
                .collect(Collectors.toList());
    }


    public List<Trainer> getAllTrainers() {
        return new ArrayList<>(trainers);
    }

    /**
     * Removes a trainer from the database
     * @param id The ID of the trainer to remove
     * @return true if removal was successful, false otherwise
     */
    public boolean removeTrainer(int id) {
        return trainers.removeIf(t -> t.getTrainerId() == id);
    }
    public int getTrainerCount() {
        return trainers.size();
    }
    public boolean trainerExists(int id) {
        return trainers.stream().anyMatch(t -> t.getTrainerId() == id);
    }
}