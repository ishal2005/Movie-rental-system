
package project;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

class Movie {
    int id;
    String title;
    String genre;
    boolean rented;
    
    Movie(int id, String title, String genre) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.rented = false;
    }
    
    public String toString() {
        return "Movie " + id + ": " + title + " (" + genre + ") [" + (rented ? "RENTED" : "AVAILABLE") + "]";
    }
}

class Customer {
    int id;
    String name;
    Customer next;
    
    Customer(int id, String name) {
        this.id = id;
        this.name = name;
        this.next = null;
    }
    
    public String toString() {
        return "Customer " + id + ": " + name;
    }
}

class CategoryNode {
    String genre;
    java.util.List<Movie> movies;
    CategoryNode left, right;
    
    CategoryNode(String genre) {
        this.genre = genre;
        this.movies = new java.util.ArrayList<>();
        this.left = null;
        this.right = null;
    }
}

class MovieCategoryTree {
    CategoryNode root;
    
    void addMovie(Movie movie) {
        root = insertMovie(root, movie);
    }
    
    CategoryNode insertMovie(CategoryNode node, Movie movie) {
        if (node == null) {
            return new CategoryNode(movie.genre).movies.add(movie) != false ? new CategoryNode(movie.genre) : null;
        }
        
        int comparison = movie.genre.compareTo(node.genre);
        if (comparison < 0) {
            node.left = insertMovie(node.left, movie);
        } else if (comparison > 0) {
            node.right = insertMovie(node.right, movie);
        } else {
            node.movies.add(movie);
        }
        return node;
    }
    
    void displayByGenre() {
        System.out.println("\nMovies organized by genre:");
        inorderTraversal(root);
    }
    
    void inorderTraversal(CategoryNode node) {
        if (node != null) {
            inorderTraversal(node.left);
            System.out.println("  Genre: " + node.genre);
            for (Movie movie : node.movies) {
                System.out.println("    " + movie);
            }
            inorderTraversal(node.right);
        }
    }
}

class RecommendationGraph {
    java.util.Map<Integer, java.util.List<Integer>> adjacencyList = new java.util.HashMap<>();
    
    void addCustomer(int customerId) {
        adjacencyList.putIfAbsent(customerId, new java.util.ArrayList<>());
    }
    
    void addConnection(int customer1, int customer2) {
        if (!adjacencyList.containsKey(customer1)) addCustomer(customer1);
        if (!adjacencyList.containsKey(customer2)) addCustomer(customer2);
        
        adjacencyList.get(customer1).add(customer2);
        adjacencyList.get(customer2).add(customer1);
    }
    
    void displayConnections() {
        System.out.println("\nCustomer recommendation network:");
        for (java.util.Map.Entry<Integer, java.util.List<Integer>> entry : adjacencyList.entrySet()) {
            System.out.println("  Customer " + entry.getKey() + " connected to: " + entry.getValue());
        }
    }
    
    java.util.List<Integer> getRecommendations(int customerId) {
        return adjacencyList.getOrDefault(customerId, new java.util.ArrayList<>());
    }
}

class Request {
    int customerId, movieId;
    
    Request(int customerId, int movieId) {
        this.customerId = customerId;
        this.movieId = movieId;
    }
    
    public String toString() {
        return "Request: Customer " + customerId + " wants Movie " + movieId;
    }
}

class TreeVisualizationPanel extends JPanel {
    private MovieCategoryTree tree;
    private static final int NODE_WIDTH = 100;
    private static final int NODE_HEIGHT = 60;
    private static final int VERTICAL_SPACING = 80;
    private static final int HORIZONTAL_SPACING = 40;

    TreeVisualizationPanel(MovieCategoryTree tree) {
        this.tree = tree;
        setPreferredSize(new Dimension(800, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (tree != null && tree.root != null) {
            drawTree(g2d, tree.root, getWidth() / 2, 50, 0, getWidth() / 4);
        } else {
            g2d.drawString("No tree data to display", getWidth() / 2 - 50, getHeight() / 2);
        }
    }

    private void drawTree(Graphics2D g2d, CategoryNode node, int x, int y, int level, int xOffset) {
        if (node == null) return;

        String nodeText = node.genre + " (" + (node.movies != null ? node.movies.size() : 0) + " movies)";
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(x - NODE_WIDTH / 2, y - NODE_HEIGHT / 2, NODE_WIDTH, NODE_HEIGHT);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x - NODE_WIDTH / 2, y - NODE_HEIGHT / 2, NODE_WIDTH, NODE_HEIGHT);
        g2d.drawString(nodeText, x - NODE_WIDTH / 2 + 10, y - 10);
        if (node.movies != null) {
            for (int i = 0; i < node.movies.size() && i < 2; i++) {
                g2d.drawString("- " + node.movies.get(i).title, x - NODE_WIDTH / 2 + 10, y + 10 + i * 15);
            }
        }

        if (node.left != null) {
            int leftX = x - xOffset;
            int leftY = y + VERTICAL_SPACING;
            g2d.drawLine(x, y + NODE_HEIGHT / 2, leftX, leftY - NODE_HEIGHT / 2);
            drawTree(g2d, node.left, leftX, leftY, level + 1, xOffset / 2);
        }

        if (node.right != null) {
            int rightX = x + xOffset;
            int rightY = y + VERTICAL_SPACING;
            g2d.drawLine(x, y + NODE_HEIGHT / 2, rightX, rightY - NODE_HEIGHT / 2);
            drawTree(g2d, node.right, rightX, rightY, level + 1, xOffset / 2);
        }
    }
}

class GraphVisualizationPanel extends JPanel {
    private RecommendationGraph graph;
    private Customer head;
    private static final int NODE_RADIUS = 30;
    private static final double CIRCLE_RADIUS = 200;

    GraphVisualizationPanel(RecommendationGraph graph, Customer head) {
        this.graph = graph;
        this.head = head;
        setPreferredSize(new Dimension(800, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (graph.adjacencyList.isEmpty()) {
            g2d.drawString("No graph data to display", getWidth() / 2 - 50, getHeight() / 2);
            return;
        }

        java.util.Map<Integer, java.awt.Point> positions = new java.util.HashMap<>();
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        java.util.List<Integer> customers = new java.util.ArrayList<>(graph.adjacencyList.keySet());
        int n = customers.size();
        if (n == 0) {
            g2d.drawString("No customers to display", getWidth() / 2 - 50, getHeight() / 2);
            return;
        }
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            int x = (int) (centerX + CIRCLE_RADIUS * Math.cos(angle));
            int y = (int) (centerY + CIRCLE_RADIUS * Math.sin(angle));
            positions.put(customers.get(i), new java.awt.Point(x, y));
        }

        g2d.setColor(Color.BLUE);
        for (java.util.Map.Entry<Integer, java.util.List<Integer>> entry : graph.adjacencyList.entrySet()) {
            int customer1 = entry.getKey();
            java.awt.Point p1 = positions.get(customer1);
            for (int customer2 : entry.getValue()) {
                java.awt.Point p2 = positions.get(customer2);
                if (p1 != null && p2 != null) {
                    g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }

        for (int customerId : customers) {
            java.awt.Point p = positions.get(customerId);
            if (p != null) {
                g2d.setColor(Color.YELLOW);
                g2d.fillOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS, 2 * NODE_RADIUS, 2 * NODE_RADIUS);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS, 2 * NODE_RADIUS, 2 * NODE_RADIUS);
                Customer customer = findCustomer(customerId);
                String label = customer != null ? customerId + ": " + customer.name : String.valueOf(customerId);
                g2d.drawString(label, p.x - NODE_RADIUS / 2, p.y);
            }
        }
    }

    private Customer findCustomer(int id) {
        Customer current = head;
        while (current != null) {
            if (current.id == id) return current;
            current = current.next;
        }
        return null;
    }
}

public class Project {
    static Customer head = null;
    static java.util.ArrayList<Movie> movies = new java.util.ArrayList<>();
    static java.util.Queue<Request> queue = new java.util.LinkedList<>();
    static java.util.Stack<String> history = new java.util.Stack<>();
    static MovieCategoryTree categoryTree = new MovieCategoryTree();
    static RecommendationGraph recommendationGraph = new RecommendationGraph();
    static java.util.Scanner scanner = new java.util.Scanner(System.in);
    static JFrame visualizationFrame = null;

    public static void main(String[] args) {
        System.out.println("=== Advanced Movie Rental System ===");
        System.out.println("Data Structures: LinkedList, ArrayList, Queue, Stack, Tree, Graph\n");
        
        while (true) {
            showMenu();
            int choice = getIntInput("Enter choice: ");
            
            switch (choice) {
                case 1: addCustomer(); break;
                case 2: addMovie(); break;
                case 3: enqueueRental(); break;
                case 4: processRequest(); break;
                case 5: displayCustomers(); break;
                case 6: displayMovies(); break;
                case 7: displayCategories(); break;
                case 8: addConnection(); break;
                case 9: showRecommendations(); break;
                case 10: undoRental(); break;
                case 11: showHistory(); break;
                case 12: exitSystem(); break;
                case 13: visualizeTree(); break;
                case 14: visualizeGraph(); break;
                default: System.out.println("Invalid choice");
            }
        }
    }
    
    static void showMenu() {
        System.out.println("\n===== Main Menu =====");
        System.out.println("1. Add Customer");
        System.out.println("2. Add Movie");
        System.out.println("3. Enqueue Rental Request");
        System.out.println("4. Process Next Rental");
        System.out.println("5. Display Customers");
        System.out.println("6. Display Movies");
        System.out.println("7. Display Movie Categories");
        System.out.println("8. Add Friend Connection");
        System.out.println("9. Show Movie Recommendations");
        System.out.println("10. Undo Last Rental");
        System.out.println("11. View Rental History");
        System.out.println("12. Exit System");
        System.out.println("13. Visualize Movie Category Tree");
        System.out.println("14. Visualize Recommendation Graph");
    }
    
    static void addCustomer() {
        System.out.println("\n--- Add New Customer ---");
        int id = getIntInput("Enter customer ID: ");
        String name = getStringInput("Enter customer name: ");
        
        Customer newCustomer = new Customer(id, name);
        newCustomer.next = head;
        head = newCustomer;
        recommendationGraph.addCustomer(id);
        System.out.println("Customer added: " + newCustomer);
    }
    
    static void addMovie() {
        System.out.println("\n--- Add New Movie ---");
        int id = getIntInput("Enter movie ID: ");
        String title = getStringInput("Enter movie title: ");
        String genre = getStringInput("Enter movie genre: ");
        
        Movie movie = new Movie(id, title, genre);
        movies.add(movie);
        categoryTree.addMovie(movie);
        System.out.println("Movie added: " + movie);
    }
    
    static void enqueueRental() {
        System.out.println("\n--- New Rental Request ---");
        int customerId = getIntInput("Enter customer ID: ");
        int movieId = getIntInput("Enter movie ID: ");
        
        queue.add(new Request(customerId, movieId));
        System.out.println("Request added to queue");
    }
    
    static void processRequest() {
        if (queue.isEmpty()) {
            System.out.println("\nNo pending requests");
            return;
        }
        
        Request req = queue.poll();
        Customer customer = findCustomer(req.customerId);
        Movie movie = findMovie(req.movieId);
        
        if (customer == null) {
            System.out.println("Customer not found");
            return;
        }
        if (movie == null) {
            System.out.println("Movie not found");
            return;
        }
        if (movie.rented) {
            System.out.println("Movie already rented");
            return;
        }
        
        movie.rented = true;
        String record = customer.name + " rented " + movie.title;
        history.push(record);
        System.out.println("Processed: " + record);
    }
    
    static void displayCustomers() {
        System.out.println("\n--- Customer List ---");
        if (head == null) {
            System.out.println("No customers found");
            return;
        }
        
        Customer current = head;
        while (current != null) {
            System.out.println(current);
            current = current.next;
        }
    }
    
    static void displayMovies() {
        System.out.println("\n--- Movie Inventory ---");
        if (movies.isEmpty()) {
            System.out.println("No movies found");
            return;
        }
        
        for (Movie movie : movies) {
            System.out.println(movie);
        }
    }
    
    static void displayCategories() {
        if (movies.isEmpty()) {
            System.out.println("\nNo movies in categories");
            return;
        }
        categoryTree.displayByGenre();
    }
    
    static void addConnection() {
        System.out.println("\n--- Add Friend Connection ---");
        int id1 = getIntInput("Enter first customer ID: ");
        int id2 = getIntInput("Enter second customer ID: ");
        
        if (findCustomer(id1) == null || findCustomer(id2) == null) {
            System.out.println("One or both customers not found");
            return;
        }
        
        recommendationGraph.addConnection(id1, id2);
        System.out.println("Connection added between customers " + id1 + " and " + id2);
    }
    
    static void showRecommendations() {
        System.out.println("\n--- Movie Recommendations ---");
        int customerId = getIntInput("Enter customer ID: ");
        
        Customer customer = findCustomer(customerId);
        if (customer == null) {
            System.out.println("Customer not found");
            return;
        }
        
        java.util.List<Integer> connections = recommendationGraph.getRecommendations(customerId);
        if (connections.isEmpty()) {
            System.out.println("No friends found for Customer " + customerId + " (" + customer.name + ")");
            return;
        }
        
        java.util.Set<String> recommendedMovies = new java.util.HashSet<>();
        for (Integer friendId : connections) {
            Customer friend = findCustomer(friendId);
            if (friend != null) {
                for (String record : history) {
                    if (record.startsWith(friend.name + " rented ")) {
                        String movieTitle = record.substring(friend.name.length() + 8);
                        recommendedMovies.add(movieTitle);
                    }
                }
            }
        }
        
        if (recommendedMovies.isEmpty()) {
            System.out.println("No movies rented by friends of Customer " + customerId + " (" + customer.name + ")");
        } else {
            System.out.println("Recommended movies for Customer " + customerId + " (" + customer.name + "):");
            for (String movieTitle : recommendedMovies) {
                System.out.println("  - " + movieTitle);
            }
        }
    }
    
    static void undoRental() {
        if (history.isEmpty()) {
            System.out.println("\nNo rentals to undo");
            return;
        }
        
        String lastRental = history.pop();
        System.out.println("\nUndoing: " + lastRental);
        
        String movieTitle = lastRental.substring(lastRental.indexOf("rented") + 7);
        for (Movie movie : movies) {
            if (movie.title.equals(movieTitle)) {
                movie.rented = false;
                break;
            }
        }
    }
    
    static void showHistory() {
        if (history.isEmpty()) {
            System.out.println("\nNo rental history");
            return;
        }
        
        System.out.println("\n--- Rental History (Most Recent First) ---");
        java.util.Stack<String> temp = new java.util.Stack<>();
        temp.addAll(history);
        
        while (!temp.isEmpty()) {
            System.out.println("  • " + temp.pop());
        }
    }
    
    static void visualizeTree() {
        if (visualizationFrame == null) {
            visualizationFrame = new JFrame("Data Structure Visualizations");
            visualizationFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            visualizationFrame.setSize(900, 700);
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Movie Category Tree", new TreeVisualizationPanel(categoryTree));
            tabbedPane.add("Recommendation Graph", new GraphVisualizationPanel(recommendationGraph, head));
            visualizationFrame.add(tabbedPane);
        }
        visualizationFrame.setVisible(true);
        JTabbedPane tabbedPane = (JTabbedPane) visualizationFrame.getContentPane().getComponent(0);
        tabbedPane.setSelectedIndex(0);
        visualizationFrame.getContentPane().repaint();
    }
    
    static void visualizeGraph() {
        if (visualizationFrame == null) {
            visualizationFrame = new JFrame("Data Structure Visualizations");
            visualizationFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            visualizationFrame.setSize(900, 700);
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Movie Category Tree", new TreeVisualizationPanel(categoryTree));
            tabbedPane.add("Recommendation Graph", new GraphVisualizationPanel(recommendationGraph, head));
            visualizationFrame.add(tabbedPane);
        }
        visualizationFrame.setVisible(true);
        JTabbedPane tabbedPane = (JTabbedPane) visualizationFrame.getContentPane().getComponent(0);
        tabbedPane.setSelectedIndex(1);
        visualizationFrame.getContentPane().repaint();
    }
    
    static void exitSystem() {
        System.out.println("\nExiting system...");
        if (visualizationFrame != null) {
            visualizationFrame.dispose();
        }
        scanner.close();
        System.exit(0);
    }
    
    static Customer findCustomer(int id) {
        Customer current = head;
        while (current != null) {
            if (current.id == id) return current;
            current = current.next;
        }
        return null;
    }
    
    static Movie findMovie(int id) {
        for (Movie movie : movies) {
            if (movie.id == id) return movie;
        }
        return null;
    }
    
    static int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("Invalid input. " + prompt);
        }
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }
    
    static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}