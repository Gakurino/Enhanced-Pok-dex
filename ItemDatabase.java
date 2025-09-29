import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemDatabase {
    private List<Item> itemList = new ArrayList<>();
    /**
     * Adds a new item to the database.
     *
     * @param item The Item object to be added to the database
     */
    public void addItem(Item item) {
        itemList.add(item);
    }

    /**
     * Retrieves a copy of all items in the database.
     *
     * @return A List containing all items in the database
     */
    public List<Item> getAllItems() {
        return new ArrayList<>(itemList);
    }
    /**
     * Searches for items whose names contain the specified search term (case-insensitive).
     *
     * @param name The search term to match against item names
     * @return A List of items matching the search criteria
     */
    public List<Item> searchByName(String name) {
        String searchTerm = name.toLowerCase();
        return itemList.stream()
                .filter(i -> i.getName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }
    /**
     * Searches for items whose categories contain the specified search term (case-insensitive).
     *
     * @param category The search term to match against item categories
     * @return A List of items matching the search criteria
     */
    public List<Item> searchByCategory(String category) {
        String searchTerm = category.toLowerCase();
        return itemList.stream()
                .filter(i -> i.getCategory().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }
}