import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 * The Pokedex class manages the collection of Pokemon in the Enhanced Pokedex application.
 * It provides functionality to add new Pokemon, retrieve all Pokemon, and search for Pokemon
 * by name or type while enforcing uniqueness constraints on Pokedex numbers and names.
 */
public class Pokedex {
    private List<Pokemon> pokemonList = new ArrayList<>();

    /**
     * Adds a new Pokemon to the Pokedex after validating its uniqueness.
     *
     * @param pokemon The Pokemon to be added
     * @throws IllegalArgumentException if the Pokedex number or name already exists
     */
    public void addPokemon(Pokemon pokemon) {
        if (pokemonList.stream().anyMatch(p -> p.getPokedexNumber() == pokemon.getPokedexNumber())) {
            throw new IllegalArgumentException("Duplicate Pokedex number");
        }
        if (pokemonList.stream().anyMatch(p -> p.getName().equalsIgnoreCase(pokemon.getName()))) {
            throw new IllegalArgumentException("Duplicate Pokemon name");
        }
        pokemonList.add(pokemon);
    }

    public List<Pokemon> getAllPokemon() {

        return new ArrayList<>(pokemonList);
    }

    /**
     * Searches for Pokemon whose names contain the specified search term (case-insensitive).
     *
     * @param name The search term to match against Pokemon names
     * @return A List of Pokemon matching the search criteria
     */
    public List<Pokemon> searchByName(String name) {
        String searchTerm = name.toLowerCase();
        return pokemonList.stream()
                .filter(p -> p.getName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }
    /**
     * Searches for Pokemon whose primary or secondary type matches the specified type (case-insensitive).
     *
     * @param type The type to search for (e.g., "Fire", "Water")
     * @return A List of Pokemon matching the type criteria
     */
    public List<Pokemon> searchByType(String type) {
        String searchTerm = type.toLowerCase();
        return pokemonList.stream()
                .filter(p -> p.getType1().toLowerCase().contains(searchTerm) ||
                        (p.getType2() != null && p.getType2().toLowerCase().contains(searchTerm)))
                .collect(Collectors.toList());
    }
}