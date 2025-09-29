import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class PokedexGUI extends JFrame {

    // --- MODEL ---
    private final Pokedex pokedex = new Pokedex();
    private final MoveDatabase moveDatabase = new MoveDatabase();
    private final ItemDatabase itemDatabase = new ItemDatabase();
    private final TrainerDatabase trainerDB = new TrainerDatabase();

    // --- VIEW ---
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    // Table Models
    private DefaultTableModel pokemonTableModel;
    private DefaultTableModel moveTableModel;
    private DefaultTableModel itemTableModel;
    private DefaultTableModel trainerTableModel;
    private DefaultTableModel activeTeamTableModel;
    private DefaultTableModel storageTableModel;
    private DefaultTableModel inventoryTableModel;

    // Labels for Trainer Profile
    private JLabel trainerNameLabel;
    private JLabel trainerInfoLabel;
    private JLabel trainerMoneyLabel;

    // Current selected trainer
    private Trainer currentTrainer;

    /**
     * Main constructor that sets up the entire application.
     */
    public PokedexGUI() {
        // --- MVC Setup ---
        // 1. Initialize Model data
        loadAllData();

        // 2. Initialize View (GUI components)
        setupUI();

        // 3. The Controller logic is implemented via ActionListeners within setupUI() methods.

        setTitle("Enhanced Pokedex");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800); // Increased size for better layout
        setLocationRelativeTo(null); // Center the window
        add(mainPanel);
    }

    /**
     * Loads all data from CSV files into the model classes.
     */
    private void loadAllData() {
        // It's crucial that MOVES.csv is loaded first as POKEMONS.csv depends on it.
        loadMoveData();
        loadItemData();
        loadPokemonData();
        initializeTrainersWithPokemon(); // Using the detailed initialization
        System.out.println("All data loaded successfully.");
    }

    /**
     * Sets up the main UI structure, including the CardLayout and panels.
     */
    private void setupUI() {
        JPanel menuPanel = createMenuPanel();
        JPanel pokemonManagementPanel = createPokemonManagementPanel();
        JPanel moveManagementPanel = createMoveManagementPanel();
        JPanel itemManagementPanel = createItemManagementPanel();
        JPanel trainerManagementPanel = createTrainerManagementPanel();
        JPanel trainerProfilePanel = createTrainerProfilePanel();

        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(pokemonManagementPanel, "POKEMON");
        mainPanel.add(moveManagementPanel, "MOVE");
        mainPanel.add(itemManagementPanel, "ITEM");
        mainPanel.add(trainerManagementPanel, "TRAINER");
        mainPanel.add(trainerProfilePanel, "TRAINER_PROFILE");

        cardLayout.show(mainPanel, "MENU");
    }

    // --- Panel Creation Methods ---

    /**
     * Creates the main menu panel with navigation buttons.
     * @return The configured JPanel for the main menu.
     */
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(new EmptyBorder(50, 100, 50, 100));
        panel.setBackground(new Color(210, 40, 40)); // Pokeball Red

        JLabel titleLabel = new JLabel("Enhanced Pokedex", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        JButton btnPokemon = createMenuButton("Pokemon Management");
        btnPokemon.addActionListener(e -> {
            refreshPokemonTable();
            cardLayout.show(mainPanel, "POKEMON");
        });

        JButton btnMoves = createMenuButton("Moves Management");
        btnMoves.addActionListener(e -> {
            refreshMoveTable();
            cardLayout.show(mainPanel, "MOVE");
        });

        JButton btnItems = createMenuButton("Item Management");
        btnItems.addActionListener(e -> {
            refreshItemTable();
            cardLayout.show(mainPanel, "ITEM");
        });

        JButton btnTrainers = createMenuButton("Trainer Management");
        btnTrainers.addActionListener(e -> {
            refreshTrainerTable();
            cardLayout.show(mainPanel, "TRAINER");
        });

        panel.add(btnPokemon);
        panel.add(btnMoves);
        panel.add(btnItems);
        panel.add(btnTrainers);

        return panel;
    }

    /**
     * Helper method to create styled menu buttons.
     * @param text The text for the button.
     * @return A styled JButton.
     */
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(50, 50, 50));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        return button;
    }

    /**
     * Creates the Pokemon management panel.
     * @return The configured JPanel for Pokemon management.
     */
    private JPanel createPokemonManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Title
        JLabel title = new JLabel("Pokemon Database", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"#", "Name", "Type 1", "Type 2", "Level", "HP", "Atk", "Def", "Spd", "Evolves From", "Evolves To", "Moves"};
        pokemonTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        JTable table = new JTable(pokemonTableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Controls
        JPanel controlPanel = new JPanel();
        JTextField searchField = new JTextField(20);
        JComboBox<String> searchType = new JComboBox<>(new String[]{"By Name", "By Type"});
        JButton searchButton = new JButton("Search");
        JButton viewDetailsButton = new JButton("View Details");
        JButton addButton = new JButton("Add New Pokemon");
        JButton backButton = new JButton("Back to Menu");

        controlPanel.add(new JLabel("Search:"));
        controlPanel.add(searchField);
        controlPanel.add(searchType);
        controlPanel.add(searchButton);
        controlPanel.add(viewDetailsButton);
        controlPanel.add(addButton);
        controlPanel.add(backButton);
        panel.add(controlPanel, BorderLayout.SOUTH);

        // --- CONTROLLER (Action Listeners) ---
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

        searchButton.addActionListener(e -> {
            String term = searchField.getText();
            if (term.isEmpty()) {
                refreshPokemonTable();
                return;
            }
            List<Pokemon> results;
            if (searchType.getSelectedIndex() == 0) { // By Name
                results = pokedex.searchByName(term);
            } else { // By Type
                results = pokedex.searchByType(term);
            }
            updatePokemonTable(results);
        });

        addButton.addActionListener(e -> showAddPokemonDialog());

        viewDetailsButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a Pokemon to view.", "No Pokemon Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int pokedexId = (int) pokemonTableModel.getValueAt(selectedRow, 0);
            Pokemon selectedPokemon = pokedex.getAllPokemon().stream()
                    .filter(p -> p.getPokedexNumber() == pokedexId)
                    .findFirst()
                    .orElse(null);

            if (selectedPokemon != null) {
                showPokemonDetailsDialog(selectedPokemon);
            }
        });

        return panel;
    }

    /**
     * Creates the Moves management panel.
     * @return The configured JPanel for Moves management.
     */
    private JPanel createMoveManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Move Database", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);

        String[] columnNames = {"Name", "Description", "Classification", "Type 1", "Type 2"};
        moveTableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(moveTableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JTextField searchField = new JTextField(20);
        JComboBox<String> searchType = new JComboBox<>(new String[]{"By Name", "By Type", "By Classification"});
        JButton searchButton = new JButton("Search");
        JButton addButton = new JButton("Add New Move");
        JButton backButton = new JButton("Back to Menu");

        controlPanel.add(new JLabel("Search:"));
        controlPanel.add(searchField);
        controlPanel.add(searchType);
        controlPanel.add(searchButton);
        controlPanel.add(addButton);
        controlPanel.add(backButton);
        panel.add(controlPanel, BorderLayout.SOUTH);

        // --- CONTROLLER (Action Listeners) ---
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

        searchButton.addActionListener(e -> {
            String term = searchField.getText();
            if (term.isEmpty()) {
                refreshMoveTable();
                return;
            }
            List<Move> results;
            switch (searchType.getSelectedIndex()) {
                case 0: results = moveDatabase.searchByName(term); break;
                case 1: results = moveDatabase.searchByType(term); break;
                case 2: results = moveDatabase.searchByClassification(term); break;
                default: results = new ArrayList<>();
            }
            updateMoveTable(results);
        });

        addButton.addActionListener(e -> showAddMoveDialog());

        return panel;
    }

    /**
     * Creates the Item management panel.
     * @return The configured JPanel for Item management.
     */
    private JPanel createItemManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Item Database", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);

        String[] columnNames = {"Name", "Category", "Description", "Effect", "Buy Price", "Sell Price"};
        itemTableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(itemTableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JTextField searchField = new JTextField(20);
        JComboBox<String> searchType = new JComboBox<>(new String[]{"By Name", "By Category"});
        JButton searchButton = new JButton("Search");
        JButton addButton = new JButton("Add New Item");
        JButton backButton = new JButton("Back to Menu");

        controlPanel.add(new JLabel("Search:"));
        controlPanel.add(searchField);
        controlPanel.add(searchType);
        controlPanel.add(searchButton);
        controlPanel.add(addButton);
        controlPanel.add(backButton);
        panel.add(controlPanel, BorderLayout.SOUTH);

        // --- CONTROLLER (Action Listeners) ---
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

        searchButton.addActionListener(e -> {
            String term = searchField.getText();
            if (term.isEmpty()) {
                refreshItemTable();
                return;
            }
            List<Item> results;
            if (searchType.getSelectedIndex() == 0) {
                results = itemDatabase.searchByName(term);
            } else {
                results = itemDatabase.searchByCategory(term);
            }
            updateItemTable(results);
        });

        addButton.addActionListener(e -> showAddItemDialog());

        return panel;
    }

    /**
     * Creates the Trainer management panel.
     * @return The configured JPanel for Trainer management.
     */
    private JPanel createTrainerManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Trainer Database", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Name", "Sex", "Hometown", "Active", "Storage", "Money"};
        trainerTableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(trainerTableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JTextField searchField = new JTextField(20);
        JComboBox<String> searchType = new JComboBox<>(new String[]{"By Name", "By Pokemon"});
        JButton searchButton = new JButton("Search");
        JButton registerButton = new JButton("Register New Trainer");
        JButton manageButton = new JButton("Manage Selected Trainer");
        JButton backButton = new JButton("Back to Menu");

        controlPanel.add(new JLabel("Search:"));
        controlPanel.add(searchField);
        controlPanel.add(searchType);
        controlPanel.add(searchButton);
        controlPanel.add(registerButton);
        controlPanel.add(manageButton);
        controlPanel.add(backButton);
        panel.add(controlPanel, BorderLayout.SOUTH);

        // --- CONTROLLER (Action Listeners) ---
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

        registerButton.addActionListener(e -> showRegisterTrainerDialog());

        searchButton.addActionListener(e -> {
            String term = searchField.getText();
            if(term.isEmpty()) {
                refreshTrainerTable();
                return;
            }
            List<Trainer> results;
            if(searchType.getSelectedIndex() == 0) { // By Name
                results = trainerDB.searchByName(term);
            } else { // By Pokemon
                results = trainerDB.searchByPokemon(term);
            }
            updateTrainerTable(results);
        });

        manageButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a trainer to manage.", "No Trainer Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int trainerId = (int) trainerTableModel.getValueAt(selectedRow, 0); // Column 0 is now ID
            currentTrainer = trainerDB.getTrainerById(trainerId);
            if (currentTrainer != null) {
                refreshTrainerProfileView();
                cardLayout.show(mainPanel, "TRAINER_PROFILE");
            }
        });

        return panel;
    }

    /**
     * Creates the detailed trainer profile panel.
     * @return The configured JPanel for a single trainer's profile.
     */
    private JPanel createTrainerProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(1, 3));
        trainerNameLabel = new JLabel("Trainer Name", SwingConstants.LEFT);
        trainerNameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        trainerInfoLabel = new JLabel("Sex: | Hometown:", SwingConstants.CENTER);
        trainerInfoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        trainerMoneyLabel = new JLabel("Money: ₱0.00", SwingConstants.RIGHT);
        trainerMoneyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        infoPanel.add(trainerNameLabel);
        infoPanel.add(trainerInfoLabel);
        infoPanel.add(trainerMoneyLabel);
        panel.add(infoPanel, BorderLayout.NORTH);

        // Center Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Active Team Tab
        String[] teamCols = {"Name", "Lvl", "Type", "HP", "Atk", "Def", "Spd", "Moves"};
        activeTeamTableModel = new DefaultTableModel(teamCols, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable activeTeamTable = new JTable(activeTeamTableModel);
        tabbedPane.addTab("Active Team", new JScrollPane(activeTeamTable));

        // Storage Tab
        String[] storageCols = {"Name", "Lvl", "Type", "HP", "Atk", "Def", "Spd"};
        storageTableModel = new DefaultTableModel(storageCols, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable storageTable = new JTable(storageTableModel);
        tabbedPane.addTab("Storage", new JScrollPane(storageTable));

        // Inventory Tab
        String[] invCols = {"Item", "Qty", "Category", "Effect"};
        inventoryTableModel = new DefaultTableModel(invCols, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable inventoryTable = new JTable(inventoryTableModel);
        tabbedPane.addTab("Inventory", new JScrollPane(inventoryTable));

        panel.add(tabbedPane, BorderLayout.CENTER);

        // Bottom Control Panel
        JPanel controlPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        JButton addPokemonButton = new JButton("Add Pokemon");
        JButton releasePokemonButton = new JButton("Release Pokemon");
        JButton switchPokemonButton = new JButton("Switch Pokemon");
        JButton teachMoveButton = new JButton("Teach/Forget Move");
        JButton useItemButton = new JButton("Use Item");
        JButton buyItemButton = new JButton("Buy Item");
        JButton sellItemButton = new JButton("Sell Item");
        JButton backButton = new JButton("Back to Trainer List");

        controlPanel.add(addPokemonButton);
        controlPanel.add(releasePokemonButton);
        controlPanel.add(switchPokemonButton);
        controlPanel.add(teachMoveButton);
        controlPanel.add(useItemButton);
        controlPanel.add(buyItemButton);
        controlPanel.add(sellItemButton);
        controlPanel.add(backButton);
        panel.add(controlPanel, BorderLayout.SOUTH);

        // --- CONTROLLER (Action Listeners) ---
        backButton.addActionListener(e -> {
            currentTrainer = null;
            cardLayout.show(mainPanel, "TRAINER");
        });

        addPokemonButton.addActionListener(e -> showAddPokemonToTrainerDialog());
        releasePokemonButton.addActionListener(e -> showReleasePokemonDialog());
        switchPokemonButton.addActionListener(e -> showSwitchPokemonDialog());
        buyItemButton.addActionListener(e -> showBuyItemDialog());
        sellItemButton.addActionListener(e -> showSellItemDialog());
        useItemButton.addActionListener(e -> showUseItemDialog());
        teachMoveButton.addActionListener(e -> showTeachMoveDialog());

        return panel;
    }


    // --- Table Refresh and Update Methods ---

    private void refreshPokemonTable() {
        updatePokemonTable(pokedex.getAllPokemon());
    }

    private void updatePokemonTable(List<Pokemon> pokemons) {
        pokemonTableModel.setRowCount(0);
        for (Pokemon p : pokemons) {
            String evolvesFrom = p.getEvolvesFrom() == 0 ? "None" : getPokemonNameById(p.getEvolvesFrom());
            String evolvesTo = p.getEvolvesTo() == 0 ? "None" : getPokemonNameById(p.getEvolvesTo());
            String moves = p.getMoveSet().stream().map(Move::getName).collect(Collectors.joining(", "));
            pokemonTableModel.addRow(new Object[]{
                    p.getPokedexNumber(), p.getName(), p.getType1(), p.getType2(),
                    p.getLevel(), p.getHp(), p.getAttack(), p.getDefense(), p.getSpeed(),
                    evolvesFrom, evolvesTo, moves
            });
        }
    }

    private String getPokemonNameById(int id) {
        return pokedex.getAllPokemon().stream()
                .filter(p -> p.getPokedexNumber() == id)
                .map(Pokemon::getName)
                .findFirst()
                .orElse("Unknown");
    }

    private void refreshMoveTable() {
        updateMoveTable(moveDatabase.getAllMoves());
    }

    private void updateMoveTable(List<Move> moves) {
        moveTableModel.setRowCount(0);
        for (Move m : moves) {
            moveTableModel.addRow(new Object[]{
                    m.getName(), m.getDescription(), m.getClassification(), m.getType1(), m.getType2()
            });
        }
    }

    private void refreshItemTable() {
        updateItemTable(itemDatabase.getAllItems());
    }

    private void updateItemTable(List<Item> items) {
        itemTableModel.setRowCount(0);
        for (Item i : items) {
            itemTableModel.addRow(new Object[]{
                    i.getName(), i.getCategory(), i.getDescription(), i.getEffect(),
                    i.getBuyPrice() == 0 ? "N/A" : i.getBuyPrice(),
                    i.getSellPrice()
            });
        }
    }

    private void refreshTrainerTable() {
        updateTrainerTable(trainerDB.getAllTrainers());
    }

    private void updateTrainerTable(List<Trainer> trainers) {
        trainerTableModel.setRowCount(0);
        for (Trainer t : trainers) {
            trainerTableModel.addRow(new Object[]{
                    t.getTrainerId(), t.getName(), t.getSex(), t.getHometown(),
                    t.getActiveTeam().size(), t.getStorage().size(),
                    String.format("₱%,.2f", t.getMoney(0))
            });
        }
    }

    private void refreshTrainerProfileView() {
        if (currentTrainer == null) return;

        // Update labels
        trainerNameLabel.setText(currentTrainer.getName());
        trainerInfoLabel.setText("Sex: " + currentTrainer.getSex() + " | Hometown: " + currentTrainer.getHometown());
        trainerMoneyLabel.setText(String.format("Money: ₱%,.2f", currentTrainer.getMoney(0)));

        // Update tables
        activeTeamTableModel.setRowCount(0);
        for (Pokemon p : currentTrainer.getActiveTeam()) {
            String moves = p.getMoveSet().stream().map(Move::getName).collect(Collectors.joining(", "));
            activeTeamTableModel.addRow(new Object[]{
                    p.getName(), p.getLevel(), p.getType1() + (p.getType2() != null ? "/" + p.getType2() : ""),
                    p.getHp(), p.getAttack(), p.getDefense(), p.getSpeed(), moves
            });
        }

        storageTableModel.setRowCount(0);
        for (Pokemon p : currentTrainer.getStorage()) {
            storageTableModel.addRow(new Object[]{
                    p.getName(), p.getLevel(), p.getType1() + (p.getType2() != null ? "/" + p.getType2() : ""),
                    p.getHp(), p.getAttack(), p.getDefense(), p.getSpeed()
            });
        }

        inventoryTableModel.setRowCount(0);
        for (Item i : currentTrainer.getInventory()) {
            inventoryTableModel.addRow(new Object[]{
                    i.getName(), i.getQuantity(), i.getCategory(), i.getEffect()
            });
        }
    }


    // --- Dialog Methods (Controller Logic) ---

    private void showPokemonDetailsDialog(Pokemon pokemon) {
        JTextArea textArea = new JTextArea(20, 40);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(pokemon.getName()).append(" ===\n");
        sb.append("Pokedex #: ").append(pokemon.getPokedexNumber()).append("\n");
        sb.append("Type: ").append(pokemon.getType1());
        if (pokemon.getType2() != null) {
            sb.append("/").append(pokemon.getType2());
        }
        sb.append("\nLevel: ").append(pokemon.getLevel()).append("\n");
        sb.append("Stats: HP=").append(pokemon.getHp())
                .append(" ATK=").append(pokemon.getAttack())
                .append(" DEF=").append(pokemon.getDefense())
                .append(" SPD=").append(pokemon.getSpeed()).append("\n\n");

        // Evolution Info
        String evolvesFrom = pokemon.getEvolvesFrom() == 0 ? "None" : getPokemonNameById(pokemon.getEvolvesFrom());
        String evolvesTo = pokemon.getEvolvesTo() == 0 ? "None" : getPokemonNameById(pokemon.getEvolvesTo());
        sb.append("--- Evolution ---\n");
        sb.append("Evolves From: ").append(evolvesFrom).append("\n");
        sb.append("Evolves To: ").append(evolvesTo).append("\n");
        if (pokemon.getEvolutionLevel() > 0) {
            sb.append("Evolution Level: ").append(pokemon.getEvolutionLevel()).append("\n");
        }
        if (pokemon.getEvolutionMethod() != null) {
            sb.append("Evolution Method: ").append(pokemon.getEvolutionMethod()).append("\n");
        }
        if (pokemon.getEvolutionStoneType() != null) {
            sb.append("Evolution Stone: ").append(pokemon.getEvolutionStoneType()).append("\n");
        }

        // Moves Info
        sb.append("\n--- Moves ---\n");
        if (pokemon.getMoveSet().isEmpty()) {
            sb.append("No moves known.\n");
        } else {
            for (Move move : pokemon.getMoveSet()) {
                sb.append("- ").append(move.getName())
                        .append(" [").append(move.getType1()).append(move.getType2() != null ? "/" + move.getType2() : "").append("]")
                        .append(" (").append(move.getClassification()).append(")\n");
                sb.append("  ").append(move.getDescription()).append("\n");
            }
        }

        textArea.setText(sb.toString());
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane, "Pokemon Details: " + pokemon.getName(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAddItemDialog() {
        JTextField nameField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField descField = new JTextField();
        JTextField effectField = new JTextField();
        JTextField buyPriceField = new JTextField();
        JTextField sellPriceField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("Description:"));
        panel.add(descField);
        panel.add(new JLabel("Effect:"));
        panel.add(effectField);
        panel.add(new JLabel("Buy Price (0 if not sold):"));
        panel.add(buyPriceField);
        panel.add(new JLabel("Sell Price:"));
        panel.add(sellPriceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Item item = new Item(
                        nameField.getText(),
                        descField.getText(),
                        categoryField.getText(),
                        Integer.parseInt(buyPriceField.getText()),
                        Integer.parseInt(sellPriceField.getText()),
                        effectField.getText()
                );
                itemDatabase.addItem(item);
                JOptionPane.showMessageDialog(this, "Item added successfully!");
                refreshItemTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number for price. Please check your inputs.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding item: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddPokemonDialog() {
        // Create components for the dialog
        JTextField numField = new JTextField(5);
        JTextField nameField = new JTextField(10);
        JTextField type1Field = new JTextField(10);
        JTextField type2Field = new JTextField(10);
        JTextField levelField = new JTextField(5);
        JTextField hpField = new JTextField(5);
        JTextField attackField = new JTextField(5);
        JTextField defenseField = new JTextField(5);
        JTextField speedField = new JTextField(5);
        JTextField evolvesFromField = new JTextField(5);
        JTextField evolvesToField = new JTextField(5);
        JTextField evolutionLevelField = new JTextField(5);
        JTextField evolutionMethodField = new JTextField(10);
        JTextField evolutionStoneField = new JTextField(10);

        // Layout the components in a panel
        JPanel myPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        myPanel.add(new JLabel("Pokedex #:"));
        myPanel.add(numField);
        myPanel.add(new JLabel("Name:"));
        myPanel.add(nameField);
        myPanel.add(new JLabel("Type 1:"));
        myPanel.add(type1Field);
        myPanel.add(new JLabel("Type 2 (optional):"));
        myPanel.add(type2Field);
        myPanel.add(new JLabel("Level:"));
        myPanel.add(levelField);
        myPanel.add(new JLabel("HP:"));
        myPanel.add(hpField);
        myPanel.add(new JLabel("Attack:"));
        myPanel.add(attackField);
        myPanel.add(new JLabel("Defense:"));
        myPanel.add(defenseField);
        myPanel.add(new JLabel("Speed:"));
        myPanel.add(speedField);
        myPanel.add(new JLabel("Evolves From # (0 if none):"));
        myPanel.add(evolvesFromField);
        myPanel.add(new JLabel("Evolves To # (0 if none):"));
        myPanel.add(evolvesToField);
        myPanel.add(new JLabel("Evolution Level (0 if none):"));
        myPanel.add(evolutionLevelField);
        myPanel.add(new JLabel("Evolution Method (level/stone):"));
        myPanel.add(evolutionMethodField);
        myPanel.add(new JLabel("Evolution Stone (if method is stone):"));
        myPanel.add(evolutionStoneField);

        int result = JOptionPane.showConfirmDialog(this, myPanel,
                "Add New Pokemon", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Pokemon pokemon = new Pokemon(
                        Integer.parseInt(numField.getText()),
                        nameField.getText(),
                        type1Field.getText(),
                        type2Field.getText().isEmpty() ? null : type2Field.getText(),
                        Integer.parseInt(levelField.getText()),
                        Integer.parseInt(hpField.getText()),
                        Integer.parseInt(attackField.getText()),
                        Integer.parseInt(defenseField.getText()),
                        Integer.parseInt(speedField.getText()),
                        Integer.parseInt(evolvesFromField.getText()),
                        Integer.parseInt(evolvesToField.getText()),
                        Integer.parseInt(evolutionLevelField.getText()),
                        evolutionMethodField.getText().isEmpty() ? null : evolutionMethodField.getText(),
                        evolutionStoneField.getText().isEmpty() ? null : evolutionStoneField.getText()
                );
                pokedex.addPokemon(pokemon);
                JOptionPane.showMessageDialog(this, "Pokemon added successfully!");
                refreshPokemonTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format. Please check your inputs.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Error adding Pokemon: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddMoveDialog() {
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        JTextField classField = new JTextField();
        JTextField type1Field = new JTextField();
        JTextField type2Field = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descField);
        panel.add(new JLabel("Classification (TM/HM/Other):"));
        panel.add(classField);
        panel.add(new JLabel("Type 1:"));
        panel.add(type1Field);
        panel.add(new JLabel("Type 2 (optional):"));
        panel.add(type2Field);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Move", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Move move = new Move(
                        nameField.getText(),
                        descField.getText(),
                        classField.getText(),
                        type1Field.getText(),
                        type2Field.getText().isEmpty() ? null : type2Field.getText()
                );
                moveDatabase.addMove(move);
                JOptionPane.showMessageDialog(this, "Move added successfully!");
                refreshMoveTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding move: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showRegisterTrainerDialog() {
        JTextField nameField = new JTextField();
        JTextField birthdateField = new JTextField();
        JTextField sexField = new JTextField();
        JTextField hometownField = new JTextField();
        JTextField descField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Birthdate (YYYY-MM-DD):"));
        panel.add(birthdateField);
        panel.add(new JLabel("Sex (M/F/O):"));
        panel.add(sexField);
        panel.add(new JLabel("Hometown:"));
        panel.add(hometownField);
        panel.add(new JLabel("Description:"));
        panel.add(descField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Register New Trainer", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Trainer newTrainer = trainerDB.addTrainer(nameField.getText());
                newTrainer.setBirthdate(birthdateField.getText());
                newTrainer.setSex(sexField.getText());
                newTrainer.setHometown(hometownField.getText());
                newTrainer.setDescription(descField.getText());
                JOptionPane.showMessageDialog(this, "Trainer registered successfully!");
                refreshTrainerTable();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddPokemonToTrainerDialog() {
        List<Pokemon> allPokemon = pokedex.getAllPokemon();
        String[] pokemonNames = allPokemon.stream().map(p -> "#" + p.getPokedexNumber() + " " + p.getName()).toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(this, "Choose a Pokemon to add:",
                "Add Pokemon to " + currentTrainer.getName(), JOptionPane.QUESTION_MESSAGE, null,
                pokemonNames, pokemonNames[0]);

        if (selected != null) {
            int selectedIndex = -1;
            for(int i=0; i<pokemonNames.length; i++){
                if(pokemonNames[i].equals(selected)){
                    selectedIndex = i;
                    break;
                }
            }

            if(selectedIndex != -1){
                Pokemon pokemonToAdd = allPokemon.get(selectedIndex);
                // Important: Add a copy, not the original from the Pokedex
                if (currentTrainer.addPokemon(new Pokemon(pokemonToAdd))) {
                    // Message is now handled in Trainer class
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add " + pokemonToAdd.getName() + ". Team or storage might be full.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                refreshTrainerProfileView();
            }
        }
    }

    private void showReleasePokemonDialog() {
        List<Pokemon> allOwnedPokemon = new ArrayList<>();
        allOwnedPokemon.addAll(currentTrainer.getActiveTeam());
        allOwnedPokemon.addAll(currentTrainer.getStorage());

        if (allOwnedPokemon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "This trainer has no Pokemon to release.", "Empty Collection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] pokemonNames = allOwnedPokemon.stream()
                .map(p -> p.getName() + " (Lvl " + p.getLevel() + ")")
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(this, "Choose a Pokemon to release:",
                "Release Pokemon", JOptionPane.QUESTION_MESSAGE, null,
                pokemonNames, pokemonNames[0]);

        if (selected != null) {
            int selectedIndex = -1;
            for(int i=0; i<pokemonNames.length; i++){
                if(pokemonNames[i].equals(selected)){
                    selectedIndex = i;
                    break;
                }
            }
            if (selectedIndex != -1) {
                Pokemon toRelease = allOwnedPokemon.get(selectedIndex);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to release " + toRelease.getName() + "?", "Confirm Release", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if(currentTrainer.releasePokemon(toRelease)) {
                        JOptionPane.showMessageDialog(this, toRelease.getName() + " was released.");
                        refreshTrainerProfileView();
                    }
                }
            }
        }
    }

    private void showSwitchPokemonDialog() {
        Object[] options = {"Team -> Storage", "Storage -> Team"};
        int direction = JOptionPane.showOptionDialog(this, "Choose switch direction:", "Switch Pokemon",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (direction == -1) return; // User closed dialog

        List<Pokemon> sourceList = (direction == 0) ? currentTrainer.getActiveTeam() : currentTrainer.getStorage();
        String sourceName = (direction == 0) ? "Active Team" : "Storage";

        if (sourceList.isEmpty()) {
            JOptionPane.showMessageDialog(this, sourceName + " is empty.", "Empty List", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] pokemonNames = sourceList.stream().map(p -> p.getName() + " (Lvl " + p.getLevel() + ")").toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(this, "Choose Pokemon from " + sourceName + " to switch:",
                "Switch Pokemon", JOptionPane.QUESTION_MESSAGE, null, pokemonNames, pokemonNames[0]);

        if (selected != null) {
            int selectedIndex = -1;
            for(int i=0; i<pokemonNames.length; i++){
                if(pokemonNames[i].equals(selected)){
                    selectedIndex = i;
                    break;
                }
            }
            if (selectedIndex != -1) {
                Pokemon toSwitch = sourceList.get(selectedIndex);
                if (!currentTrainer.switchPokemon(toSwitch)) {
                    JOptionPane.showMessageDialog(this, "Could not switch " + toSwitch.getName() + ". Destination might be full.", "Switch Failed", JOptionPane.ERROR_MESSAGE);
                }
                refreshTrainerProfileView();
            }
        }
    }

    private void showBuyItemDialog() {
        List<Item> purchasable = itemDatabase.getAllItems().stream()
                .filter(i -> i.getBuyPrice() > 0)
                .collect(Collectors.toList());

        String[] itemNames = purchasable.stream()
                .map(i -> i.getName() + " - ₱" + i.getBuyPrice())
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(this, "Choose item to buy:",
                "Poke Mart", JOptionPane.QUESTION_MESSAGE, null, itemNames, itemNames[0]);

        if (selected != null) {
            int selectedIndex = -1;
            for(int i=0; i<itemNames.length; i++){
                if(itemNames[i].equals(selected)){
                    selectedIndex = i;
                    break;
                }
            }
            if (selectedIndex != -1) {
                Item toBuy = purchasable.get(selectedIndex);
                String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity for " + toBuy.getName() + ":", "1");
                try {
                    int quantity = Integer.parseInt(qtyStr);
                    if (!currentTrainer.buyItem(toBuy, quantity)) {
                        JOptionPane.showMessageDialog(this, "Purchase failed. Not enough money or inventory full.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    refreshTrainerProfileView();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void showSellItemDialog() {
        List<Item> inventory = currentTrainer.getInventory();
        if (inventory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inventory is empty.", "Sell Item", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] itemNames = inventory.stream()
                .map(i -> i.getName() + " (x" + i.getQuantity() + ") - Sell for ₱" + i.getSellPrice())
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(this, "Choose item to sell:",
                "Sell Item", JOptionPane.QUESTION_MESSAGE, null, itemNames, itemNames[0]);

        if (selected != null) {
            int selectedIndex = -1;
            for(int i=0; i<itemNames.length; i++){
                if(itemNames[i].equals(selected)){
                    selectedIndex = i;
                    break;
                }
            }
            if (selectedIndex != -1) {
                Item toSell = inventory.get(selectedIndex);
                String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity of " + toSell.getName() + " to sell:", "1");
                try {
                    int quantity = Integer.parseInt(qtyStr);
                    if (!currentTrainer.sellItem(toSell.getName(), quantity)) {
                        JOptionPane.showMessageDialog(this, "Sale failed. Not enough items.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    refreshTrainerProfileView();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void showUseItemDialog() {
        List<Item> inventory = currentTrainer.getInventory();
        if (inventory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inventory is empty.", "Use Item", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<Pokemon> team = currentTrainer.getActiveTeam();
        if (team.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No Pokemon in active team to use an item on.", "Use Item", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Select Item
        String[] itemNames = inventory.stream().map(i -> i.getName() + " (x" + i.getQuantity() + ")").toArray(String[]::new);
        String selectedItemStr = (String) JOptionPane.showInputDialog(this, "Choose item to use:",
                "Use Item - Step 1/2", JOptionPane.QUESTION_MESSAGE, null, itemNames, itemNames[0]);
        if (selectedItemStr == null) return;

        int itemIndex = -1;
        for(int i=0; i<itemNames.length; i++){ if(itemNames[i].equals(selectedItemStr)) itemIndex = i; }
        Item selectedItem = inventory.get(itemIndex);

        // Select Pokemon
        String[] teamNames = team.stream().map(p -> p.getName() + " (Lvl " + p.getLevel() + ")").toArray(String[]::new);
        String selectedPokemonStr = (String) JOptionPane.showInputDialog(this, "Use " + selectedItem.getName() + " on:",
                "Use Item - Step 2/2", JOptionPane.QUESTION_MESSAGE, null, teamNames, teamNames[0]);
        if (selectedPokemonStr == null) return;

        int pokemonIndex = -1;
        for(int i=0; i<teamNames.length; i++){ if(teamNames[i].equals(selectedPokemonStr)) pokemonIndex = i; }
        Pokemon targetPokemon = team.get(pokemonIndex);

        // Pass 'this' as the parent JFrame
        if (!currentTrainer.useItem(selectedItem, targetPokemon, this.pokedex, this)) {
            JOptionPane.showMessageDialog(this, "This item had no effect.", "Item Use Failed", JOptionPane.WARNING_MESSAGE);
        }
        refreshTrainerProfileView();
    }

    private void showTeachMoveDialog() {
        List<Pokemon> team = currentTrainer.getActiveTeam();
        if (team.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No Pokemon in active team.", "Teach Move", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Select Pokemon
        String[] teamNames = team.stream().map(p -> p.getName() + " (Lvl " + p.getLevel() + ")").toArray(String[]::new);
        String selectedPokemonStr = (String) JOptionPane.showInputDialog(this, "Choose a Pokemon:", "Teach Move", JOptionPane.QUESTION_MESSAGE, null, teamNames, teamNames[0]);
        if (selectedPokemonStr == null) return;
        int pokemonIndex = -1;
        for(int i=0; i<teamNames.length; i++){ if(teamNames[i].equals(selectedPokemonStr)) pokemonIndex = i; }
        Pokemon targetPokemon = team.get(pokemonIndex);

        // Ask to Teach or Forget
        Object[] options = {"Teach New Move", "Forget a Move"};
        int choice = JOptionPane.showOptionDialog(this, "What would you like to do with " + targetPokemon.getName() + "?",
                "Move Management", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 0) { // Teach New Move
            List<Move> allMoves = moveDatabase.getAllMoves();
            String[] moveNames = allMoves.stream().map(Move::getName).toArray(String[]::new);
            String selectedMoveStr = (String) JOptionPane.showInputDialog(this, "Choose a move to teach " + targetPokemon.getName() + ":",
                    "Teach Move", JOptionPane.QUESTION_MESSAGE, null, moveNames, moveNames[0]);
            if (selectedMoveStr == null) return;

            Optional<Move> selectedMoveOpt = allMoves.stream().filter(m -> m.getName().equals(selectedMoveStr)).findFirst();
            if (!selectedMoveOpt.isPresent()) return;
            Move selectedMove = selectedMoveOpt.get();

            if (targetPokemon.getMoveSet().size() >= 4 && !selectedMove.isHM()) {
                List<Move> currentMoves = targetPokemon.getMoveSet();
                String[] currentMoveNames = currentMoves.stream().map(Move::getName).toArray(String[]::new);
                String move_to_forget_str = (String) JOptionPane.showInputDialog(this,
                        targetPokemon.getName() + " already knows 4 moves. Choose one to forget:",
                        "Forget a Move", JOptionPane.QUESTION_MESSAGE, null, currentMoveNames, currentMoveNames[0]);

                if (move_to_forget_str != null) {
                    if (!currentTrainer.forgetMove(targetPokemon, move_to_forget_str)) {
                        // The trainer method now handles the dialog for HM moves
                        return;
                    }
                } else {
                    return;
                }
            }

            if (!currentTrainer.teachMove(targetPokemon, selectedMove)) {
                JOptionPane.showMessageDialog(this, "Failed to teach move. It might be incompatible.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else if (choice == 1) { // Forget a Move
            List<Move> currentMoves = targetPokemon.getMoveSet();
            if (currentMoves.isEmpty()) {
                JOptionPane.showMessageDialog(this, targetPokemon.getName() + " knows no moves to forget.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String[] currentMoveNames = currentMoves.stream().map(Move::getName).toArray(String[]::new);
            String move_to_forget_str = (String) JOptionPane.showInputDialog(this, "Choose a move to forget:",
                    "Forget a Move", JOptionPane.QUESTION_MESSAGE, null, currentMoveNames, currentMoveNames[0]);

            if (move_to_forget_str != null) {
                currentTrainer.forgetMove(targetPokemon, move_to_forget_str);
            }
        }
        refreshTrainerProfileView();
    }


    // --- Data Loading Methods (from original EnhancedPokedex.java) ---

    private void loadPokemonData() {
        try (BufferedReader br = new BufferedReader(new FileReader("POKEMONS.csv"))) {
            String line;
            br.readLine(); // Skip Header
            while ((line = br.readLine()) != null) {
                // Use split with a negative limit to keep trailing empty strings
                String[] data = line.split(",", -1);
                Pokemon pokemon = new Pokemon(
                        Integer.parseInt(data[0]), data[1], data[2],
                        data[3].isEmpty() ? null : data[3],
                        Integer.parseInt(data[4]), Integer.parseInt(data[5]),
                        Integer.parseInt(data[6]), Integer.parseInt(data[7]),
                        Integer.parseInt(data[8]), Integer.parseInt(data[9]),
                        Integer.parseInt(data[10]), Integer.parseInt(data[11]),
                        data.length > 12 && !data[12].isEmpty() ? data[12] : null,
                        data.length > 13 && !data[13].isEmpty() ? data[13] : null
                );

                // Learn moves from CSV. Loop increments by 2 to get move name and description.
                for (int i = 14; i < data.length; i += 2) {
                    String moveName = data[i].trim();
                    // Check that there is a name and a description column available
                    if (!moveName.isEmpty() && (i + 1) < data.length) {
                        String moveDescription = data[i + 1].trim();

                        // Use a default description if the CSV one is blank
                        if (moveDescription.isEmpty()) {
                            moveDescription = "A special ability or move.";
                        }

                        // Check if the move exists in the main move database
                        List<Move> foundMoves = moveDatabase.searchByName(moveName);
                        if (!foundMoves.isEmpty()) {
                            // If it exists (e.g., a TM), learn that version
                            pokemon.learnMove(foundMoves.get(0));
                        } else {
                            // Otherwise, create a new custom move with the unique description from POKEMONS.csv
                            Move customMove = new Move(moveName, moveDescription, "Ability", "Normal", null);
                            pokemon.learnMove(customMove);
                        }
                    }
                }
                pokedex.addPokemon(pokemon);
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error loading Pokemon data: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading POKEMONS.csv: " + e.getMessage(), "File Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadItemData() {
        try (BufferedReader br = new BufferedReader(new FileReader("ITEMS.csv"))) {
            String line;
            br.readLine(); // Skip Header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                Item item = new Item(data[0], data[2], data[1], Integer.parseInt(data[4]), Integer.parseInt(data[5]), data[3]);
                itemDatabase.addItem(item);
            }
        } catch (IOException e) {
            System.err.println("Error loading item data: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading ITEMS.csv: " + e.getMessage(), "File Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMoveData() {
        try (BufferedReader br = new BufferedReader(new FileReader("MOVES.csv"))) {
            String line;
            br.readLine(); // Skip Header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                Move move = new Move(data[0], data[1], data[2], data[3], data.length > 4 && !data[4].isEmpty() ? data[4] : null);
                moveDatabase.addMove(move);
            }
        } catch (IOException e) {
            System.err.println("Error loading move data: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading MOVES.csv: " + e.getMessage(), "File Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeTrainersWithPokemon() {
        // Create 5 trainers
        Trainer[] trainers = new Trainer[5];
        trainers[0] = trainerDB.addTrainer("Ash Ketchum");
        trainers[0].setSex("M");
        trainers[0].setHometown("Pallet Town");

        trainers[1] = trainerDB.addTrainer("Misty");
        trainers[1].setSex("F");
        trainers[1].setHometown("Cerulean City");

        trainers[2] = trainerDB.addTrainer("Brock");
        trainers[2].setSex("M");
        trainers[2].setHometown("Pewter City");

        trainers[3] = trainerDB.addTrainer("Gary Oak");
        trainers[3].setSex("M");
        trainers[3].setHometown("Pallet Town");

        trainers[4] = trainerDB.addTrainer("Professor Oak");
        trainers[4].setSex("M");
        trainers[4].setHometown("Pallet Town");

        // Get all loaded Pokemon
        List<Pokemon> allPokemon = pokedex.getAllPokemon();

        // Assign 5 Pokemon to each trainer
        for (int i = 0; i < trainers.length; i++) {
            Trainer trainer = trainers[i];
            int startIndex = i * 5;

            for (int j = 0; j < 5; j++) {
                if (startIndex + j < allPokemon.size()) {
                    Pokemon original = allPokemon.get(startIndex + j);
                    trainer.addPokemon(new Pokemon(original)); // Add a copy
                }
            }

            // Add Rare Candy and Moon Stone directly
            Item rareCandy = itemDatabase.searchByName("Rare Candy").stream().findFirst().orElse(null);
            Item moonStone = itemDatabase.searchByName("Moon Stone").stream().findFirst().orElse(null);

            if (rareCandy != null) {
                trainer.addItem(rareCandy, 10);
            }
            if (moonStone != null) {
                trainer.addItem(moonStone, 5);
            }

            // Add some random items to each trainer by buying them
            List<Item> allItems = itemDatabase.getAllItems().stream()
                    .filter(item -> item.getBuyPrice() > 0)
                    .collect(Collectors.toList());

            if (!allItems.isEmpty()) {
                int itemIndex = i * 3;
                Random random = new Random();

                if (itemIndex < allItems.size()) {
                    Item item1 = allItems.get(itemIndex % allItems.size());
                    int quantity1 = random.nextInt(5) + 1;
                    trainer.buyItem(item1, quantity1);

                    if ((itemIndex + 1) < allItems.size()) {
                        Item item2 = allItems.get((itemIndex + 1) % allItems.size());
                        int quantity2 = random.nextInt(5) + 1;
                        trainer.buyItem(item2, quantity2);
                    }

                    if ((itemIndex + 2) < allItems.size()) {
                        Item item3 = allItems.get((itemIndex + 2) % allItems.size());
                        int quantity3 = random.nextInt(5) + 1;
                        trainer.buyItem(item3, quantity3);
                    }
                }
            }
        }
        System.out.println("\nInitialized 5 trainers with Pokemon teams and items.");
    }


    /**
     * Main method to run the application.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Set a modern Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Run the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            PokedexGUI gui = new PokedexGUI();
            gui.setVisible(true);
        });
    }
}