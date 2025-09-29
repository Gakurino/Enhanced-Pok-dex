/**
 * Database for storing and querying Pokemon moves
 */
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MoveDatabase {
    private List<Move> moveList = new ArrayList<>();
    /**
     * Adds a move to the database
     * @param move The move to add
     */
    public void addMove(Move move) {
        moveList.add(move);
    }

    public List<Move> getAllMoves() {
        return new ArrayList<>(moveList);
    }
    /**
     * Searches moves by name (case-insensitive partial match)
     * @param name Name or partial name to search
     * @return List of matching moves
     */
    public List<Move> searchByName(String name) {
        String searchTerm = name.toLowerCase();
        return moveList.stream()
                .filter(m -> m.getName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }
    /**
     * Searches moves by type (checks both primary and secondary types)
     * @param type Type to search for
     * @return List of matching moves
     */
    public List<Move> searchByType(String type) {
        String searchTerm = type.toLowerCase();
        return moveList.stream()
                .filter(m -> m.getType1().toLowerCase().contains(searchTerm) ||
                        (m.getType2() != null && m.getType2().toLowerCase().contains(searchTerm)))
                .collect(Collectors.toList());
    }
    /**
     * Searches moves by classification (TM/HM/etc.)
     * @param classification Classification to search
     * @return List of matching moves
     */
    public List<Move> searchByClassification(String classification) {
        String searchTerm = classification.toLowerCase();
        return moveList.stream()
                .filter(m -> m.getClassification().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }
}