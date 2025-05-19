package se.su.inlupp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Gui extends Application {

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
  //kanske fult

  private Scene scene;
  private BorderPane root;
  private ListGraph<Node> listGraph; 

  public void start(Stage stage) {
      this.stage = stage;
      stage.setResizable(false);
      root = new BorderPane();
      listGraph = new ListGraph<>();

      VBox layout = createTopLayout();
      root.setTop(layout);

      //Meny eventhandlers
      newMapBtn.setOnAction(e -> newMapCreation());
      openBtn.setOnAction(e -> openGraph());

      scene = new Scene(root, 550, 480);
      stage.setScene(scene);
      stage.show();
    }

  public static void main(String[] args) {
      launch(args);
  }

  private VBox createTopLayout() {
      menuBar = createMenuBar();
      toolBar = createToolBar();

      return new VBox(menuBar, toolBar);
  }

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

  private ToolBar createToolBar() {
      newPlaceBtn = new Button("Add place");
      newConnectionBtn = new Button("Add new connection");
      showConnectionBtn = new Button("Show connections");
      changeConnectionBtn = new Button("Change a connection");
      findPathBtn = new Button("Find a path");

      return new ToolBar(newPlaceBtn, newConnectionBtn, showConnectionBtn, changeConnectionBtn, findPathBtn);
  }

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

  private void openMap(File fileName) {
    Image image = new Image(fileName.toURI().toString());
    ImageView view = new ImageView(image);

    view.setPreserveRatio(true);
    view.fitWidthProperty().bind(root.widthProperty());
    view.fitHeightProperty().bind(root.heightProperty().subtract(menuBar.getHeight() + toolBar.getHeight()));

    root.setCenter(view);
  }

  private void openGraph(){
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open");
    fileChooser.setInitialDirectory(new File("images"));

    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Graph Files", "*.graph"));

    File selectedGraph = fileChooser.showOpenDialog(stage);

    if (selectedGraph != null) {
      createGraph(selectedGraph);
    }
  }

  private void createGraph(File fileName){
    try (BufferedReader bReader = new BufferedReader(new FileReader(fileName))){
      String line = bReader.readLine();
      String[] fileLine = line.split(":");
      File image = new File(fileName.getParentFile(), fileLine[1].trim());
      openMap(image);

      line = bReader.readLine();
      String[] nodeLine = line.split(";");

      for (int i = 0; i < nodeLine.length; i += 3){
        listGraph.add(new Node(nodeLine[i], Double.parseDouble(nodeLine[i + 1].trim()), Double.parseDouble(nodeLine[i + 2].trim())));
      }

      Set<Node> nodeSet = listGraph.getNodes();

      while ((line = bReader.readLine()) != null){
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
        } catch (IllegalStateException e) {}
      }
      System.out.println(listGraph.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
