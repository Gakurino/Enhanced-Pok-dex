/**
 * Represents a Pokemon move with battle properties
 */
public class Move {
    private String name;
    private String description;
    private String classification;
    private String type1;
    private String type2;
    private final boolean isHM;

    /**
     * Constructs a new Move instance
     * @param name Move name
     * @param description Effect description
     * @param classification "TM"/"HM"/etc.
     * @param type1 Primary type
     * @param type2 Secondary type (nullable)
     */
    public Move(String name, String description, String classification,
                String type1, String type2) {
        this.name = name;
        this.description = description;
        this.classification = classification;
        this.type1 = type1;
        this.type2 = type2;
        this.isHM = classification.equalsIgnoreCase("HM");
    }

    /**
     * String representation of move
     * @return Formatted move info
     */
    public String toString() {
        return String.format("%s [%s%s] (%s) - %s",
                name, type1,
                type2 != null ? "/" + type2 : "",
                classification,
                description);
    }

    public boolean isHM() {
        return isHM;
    }
    // Getters
    public String getName() { return name; }
    public String getClassification() { return classification; }
    public String getType1() { return type1; }
    public String getType2() { return type2 != null ? type2 : ""; }
    public String getDescription() { return description;}
}