/**
 * Represents an item in the Pokemon world that can be held by Pokemon or used by trainers.
 * Items have properties such as name, description, category, price, and effect.
 */
public class Item {
    private String name;
    private String description;
    private String category;
    private int buyPrice;
    private int sellPrice;
    private String effect;
    private int quantity;

    /**
     * Constructs a new Item with the specified attributes.
     *
     * @param name        The name of the item (e.g., "Rare Candy", "Fire Stone")
     * @param description A brief description of the item's appearance or purpose
     * @param category    The classification of the item (e.g., "Vitamin", "Evolution Stone")
     * @param buyPrice    The purchase price in Pokémon Dollars (PKD). 0 indicates not for sale
     * @param sellPrice   The selling price in Pokémon Dollars (PKD)
     * @param effect      A description of the item's effect when used (e.g., "+10 HP EVs")
     */
    public Item(String name, String description, String category,
                int buyPrice, int sellPrice, String effect) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.effect = effect;
        this.quantity = 1; // Default quantity
    }

    /**
     * Copy constructor for creating a new Item object from an existing one.
     * @param other The Item object to copy.
     */
    public Item(Item other) {
        this.name = other.name;
        this.description = other.description;
        this.category = other.category;
        this.buyPrice = other.buyPrice;
        this.sellPrice = other.sellPrice;
        this.effect = other.effect;
        this.quantity = other.quantity;
    }



    // Getters
    public String getName() { return name; }
    public String getDescription() { return description;}
    public String getCategory() { return category; }
    public String getEffect() { return effect; }
    public int getBuyPrice() { return buyPrice; }
    public int getSellPrice() { return sellPrice; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int qty) { quantity = qty; }
    /**
     * Returns a string representation of the Item.
     * @return A formatted string showing item details.
     */
    @Override
    public String toString() {
        String priceInfo = (buyPrice == 0) ?
                "Not for sale" :
                String.format("Buy: ₱%,d | Sell: ₱%,d", buyPrice, sellPrice);

        return String.format("%-15s [%-12s] %-40s | %-20s | Effect: %s",
                name,
                category,
                description,
                priceInfo,
                effect);
    }
}