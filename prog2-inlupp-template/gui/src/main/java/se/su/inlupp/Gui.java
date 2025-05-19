package se.su.inlupp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.border.Border;
import javax.xml.stream.Location;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Gui extends Application {

  //En klass som gör att vi enkelt kan store koordinaterna i samband med noden
  class Node {
    private String name;
    private double yCoordinate;
    private double xCoordinate;

    public Node(String name, double x, double y) {
        this.name = name;
        this.yCoordinate = y;
        this.xCoordinate = x;
    }

    public String getName() {
        return name;
    }

    public double getyCoordinate() {
        return yCoordinate;
    }

    public double getxCoordinate() {
        return xCoordinate;
    }

    public String toString() {
        return name;
    }
  }

  private Stage stage;
  //Meny-knappar
  private MenuBar menuBar;
  private MenuItem newMapBtn;
  private MenuItem openBtn;
  private MenuItem saveMapBtn;
  private MenuItem saveImageBtn;
  private MenuItem exitBtn;

  //Toolbar-knappar
  private ToolBar toolBar;
  private Button newPlaceBtn;
  private Button newConnectionBtn;
  private Button showConnectionBtn;
  private Button changeConnectionBtn;
  private Button findPathBtn;
  //kanske fult, kan lägga in i en lista möjligtvis?

  private Scene scene;
  private BorderPane root;
  private Pane graphPane;
  private ListGraph<Node> listGraph; 
  private List<Button> nodeButtons;

  public void start(Stage stage) {
      this.stage = stage;
      stage.setResizable(false);
      root = new BorderPane();
      graphPane = new Pane();
      listGraph = new ListGraph<>();
      nodeButtons = new ArrayList<>();

      VBox layout = createTopLayout();
      root.setTop(layout);

      //Meny eventhandlers
      newMapBtn.setOnAction(e -> newMapCreation());
      openBtn.setOnAction(e -> openGraph());

      root.setCenter(graphPane);
      scene = new Scene(root, 550, 480);
      stage.setScene(scene);
      stage.show();
    }

  public static void main(String[] args) {
      launch(args);
  }

  //Skapar en VBox som innehåller menybaren och toolbaren
  private VBox createTopLayout() {
      menuBar = createMenuBar();
      toolBar = createToolBar();

      return new VBox(menuBar, toolBar);
  }

  //Skapar File-menyn
  private MenuBar createMenuBar() {
      newMapBtn = new MenuItem("New map");
      openBtn = new MenuItem("Open");
      saveMapBtn = new MenuItem("Save");
      saveImageBtn = new MenuItem("Save image");
      exitBtn = new MenuItem("Exit");

      Menu file = new Menu("File");
      file.getItems().addAll(newMapBtn, openBtn, saveMapBtn, saveImageBtn, exitBtn);

      MenuBar menuBar = new MenuBar(file);
      return menuBar;
  }

  //Skapar raden med knappar
  private ToolBar createToolBar() {
      newPlaceBtn = new Button("Add place");
      newConnectionBtn = new Button("Add new connection");
      showConnectionBtn = new Button("Show connections");
      changeConnectionBtn = new Button("Change a connection");
      findPathBtn = new Button("Find a path");

      return new ToolBar(newPlaceBtn, newConnectionBtn, showConnectionBtn, changeConnectionBtn, findPathBtn);
  }

  //Öppnar en fileChooser som låter användaren välja vilken bild den vill använda
  private void newMapCreation() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("New map");
    fileChooser.setInitialDirectory(new File("images"));

    fileChooser.getExtensionFilters().addAll(
      new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp")
    );

    File selectedImage = fileChooser.showOpenDialog(stage);

    if (selectedImage != null) {
      openMap(selectedImage);
    }
  }

  //Öppnar en bild och anpassar fönstrets storlek efter bilden
  private void openMap(File fileName) {
    Image image = new Image(fileName.toURI().toString());
    ImageView view = new ImageView(image);

    double imageWidth = image.getWidth();
    double imageHeight = image.getHeight();

    graphPane.getChildren().clear();
    graphPane.getChildren().add(view);
    graphPane.setPrefSize(imageWidth, imageHeight);
    stage.setWidth(imageWidth);
    stage.setHeight(imageHeight);
    }

  //Öppnar FileChoosern och väljer en fil, skickar sedan vidare till övriga metoder som bygger upp scenen
  private void openGraph(){
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open");
    fileChooser.setInitialDirectory(new File("images"));

    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Graph Files", "*.graph"));

    File selectedGraph = fileChooser.showOpenDialog(stage);

    if (selectedGraph != null) {
      createGraph(selectedGraph);
      createButtons();
      drawLines();
    }
  }

  //Skapar en graf baserat på filer i det givna formatet
  private void createGraph(File fileName){
    try (BufferedReader bReader = new BufferedReader(new FileReader(fileName))){
      String line = bReader.readLine();
      String[] fileLine = line.split(":");
      File image = new File(fileName.getParentFile(), fileLine[1].trim()); //Hittar filnamnet i första raden
      openMap(image);

      line = bReader.readLine();
      String[] nodeLine = line.split(";"); //Delar upp informationen om noderna och lägger in det i listGraph

      for (int i = 0; i < nodeLine.length; i += 3){
        listGraph.add(new Node(nodeLine[i], Double.parseDouble(nodeLine[i + 1].trim()), Double.parseDouble(nodeLine[i + 2].trim())));
      }

      Set<Node> nodeSet = listGraph.getNodes();

      while ((line = bReader.readLine()) != null){ //Skapar edges baserat på efterföljande linjer
        String[] connectionLine = line.split(";");
        Node from = null;
        Node to = null;
        for (Node node : nodeSet) {
          if (node.getName().equals(connectionLine[0].trim())) {
            from = node;
          } else if (node.getName().equals(connectionLine[1].trim())) {
            to = node;
          }
        }
        try {
          listGraph.connect(from, to, connectionLine[2], Integer.parseInt(connectionLine[3]));
        } catch (IllegalStateException e) {} //På grund av formatet i connect() så throwas ett illegalstateexception om man försöker lägga till en edge som redan finns, då den givna filen försöker göra detta så ignorerar vi helt enkelt den andra instansen med en trycatch 
      }
      System.out.println(listGraph.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //Drar linjer mellan olika noder
  private void drawLines() {
    for (Node node : listGraph.getNodes()) {
      for (Edge<Node> edge : listGraph.getEdgesFrom(node)) {
        Node from = node;
        Node to = edge.getDestination();
        Line line = new Line(from.getxCoordinate(), from.getyCoordinate(), to.getxCoordinate(), to.getyCoordinate());
        line.setStroke(Color.GRAY);
        line.setStrokeWidth(2);
        graphPane.getChildren().add(line);
      }
    }
  }

  //Skapar en knapp för varje nod och placerar dem på nodens koordinater
  private void createButtons() {
    for (Node node : listGraph.getNodes()) {
      Button btn = new Button(node.getName());
      btn.setShape(new Circle(20));
      btn.setStyle("-fx-background-color: lightblue;");
      btn.setLayoutX(node.getxCoordinate() - 20);
      btn.setLayoutY(node.getyCoordinate() - 20);
      btn.toFront();
      btn.setOnAction(e -> System.out.println(node.getName())); //Skriver ut namnet på noden i terminalen, felhantering
      //Lägg till eventhandlers för nodknappar här, skicka sedan btn vidare till metoderna så får ni rätt instans
      nodeButtons.add(btn);
      graphPane.getChildren().add(btn);
    }
  }
}
