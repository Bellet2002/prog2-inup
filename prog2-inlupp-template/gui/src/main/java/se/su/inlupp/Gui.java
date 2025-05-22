package se.su.inlupp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.border.Border;
import javax.xml.stream.Location;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
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

  //Meny-knappar
  private MenuBar menuBar;
  private MenuItem newMapBtn;
  private MenuItem openBtn;
  private MenuItem saveBtn;
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

  private Stage stage;
  private Scene scene;
  private BorderPane root;
  private Pane graphPane;
  private ListGraph<Node> listGraph; 
  private List<Button> nodeButtons;
  private List<Button> selectedButtons;
  private File map;
  private Boolean unsavedChanges = false; //Håller koll på osparade ändringar, om någonting läggs till, ändras eller tas bort sätt denna till true

  public void start(Stage stage) {
      this.stage = stage;
      stage.setResizable(false);
      root = new BorderPane();
      graphPane = new Pane();
      listGraph = new ListGraph<>();
      nodeButtons = new ArrayList<>();
      selectedButtons = new ArrayList<>();
      

      VBox layout = createTopLayout();
      root.setTop(layout);

      //Meny eventhandlers
      newMapBtn.setOnAction(e -> newMapCreation());
      openBtn.setOnAction(e -> openGraph());
      saveBtn.setOnAction(e -> saveGraph());
      saveImageBtn.setOnAction(e -> saveImage());
      exitBtn.setOnAction(e -> exit());
      stage.setOnCloseRequest(e -> {
        e.consume();
        exit();
      });

      //Knappar eventhandlers
      newPlaceBtn.setOnAction(e -> addPlace());
      newConnectionBtn.setOnAction(e -> addConnection());
      showConnectionBtn.setOnAction(e -> showConnection());
      changeConnectionBtn.setOnAction(e -> changeConnection());
      findPathBtn.setOnAction(e -> findPath());

      root.setCenter(graphPane);
      scene = new Scene(root, 550, 100);
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

      disableBtn();

      return new VBox(menuBar, toolBar);
  }

  //Skapar File-menyn
  private MenuBar createMenuBar() {
      newMapBtn = new MenuItem("New map");
      openBtn = new MenuItem("Open");
      saveBtn = new MenuItem("Save");
      saveImageBtn = new MenuItem("Save image");
      exitBtn = new MenuItem("Exit");

      Menu file = new Menu("File");
      file.getItems().addAll(newMapBtn, openBtn, saveBtn, saveImageBtn, exitBtn);

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

  //Metod för att sätta på knapparna
  private void enableBtn() {
    for (javafx.scene.Node btn : toolBar.getItems()) {
      if (btn instanceof Button) {
        btn.setDisable(false);
      }
    }
  }

  //Metod för att stänga av knapparna
  private void disableBtn() {
    for (javafx.scene.Node btn : toolBar.getItems()) {
      if (btn instanceof Button) {
        btn.setDisable(true);
      }
    }
  }

  private void addPlace() {
    disableBtn();
    graphPane.setCursor(Cursor.CROSSHAIR);

    graphPane.setOnMouseClicked(e -> {
      double x = e.getX();
      double y = e.getY();

      boolean validInput = false;

      while (!validInput) {
      TextInputDialog dialog = new TextInputDialog();
      dialog.setTitle("Add new node");
      dialog.setHeaderText("Name of place: ");

      Optional<String> result = dialog.showAndWait();

      if (result.isPresent()) {
        boolean validName = true;
        String name = result.get();
        for (Node node : listGraph.getNodes()) {
          if (node.getName().equals(name)) {
            validName = false;
          }
        }
        if (!name.isEmpty() && validName) {
          validInput = true;
          Node node = new Node(name, x, y);
          listGraph.add(node);
          createButton(node);
          graphPane.setCursor(Cursor.DEFAULT);
          enableBtn();
          graphPane.setOnMouseClicked(null);
          unsavedChanges = true;
        } else {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Invalid name");
          if (name.isEmpty()) {
            alert.setHeaderText("The name cannot be empty");
          } else {
            alert.setHeaderText("The name is already used");
          }
          alert.showAndWait();
        }
      } else {
          validInput = true;
          graphPane.setCursor(Cursor.DEFAULT);
          enableBtn();
          graphPane.setOnMouseClicked(null);
      }
    }
  });
  }

  private void addConnection() {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    if (selectedButtons.size() == 2) {
      ArrayList<Node> nodes = getNodesFromBtn();
      Node to = nodes.get(0);
      Node from = nodes.get(1);
      if (listGraph.getEdgeBetween(from, to) == null) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Connection");

        TextField nameInput = new TextField();
        TextField weightInput = new TextField();

        VBox popupLayout = new VBox(10);
        popupLayout.getChildren().addAll(new Label("Name:"), nameInput, new Label("Time:"), weightInput);
        dialog.getDialogPane().setContent(popupLayout);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        boolean validInput = false;

        while (!validInput) {
          Optional<ButtonType> result = dialog.showAndWait();
          if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
              String name = nameInput.getText();
              int weight = Integer.parseInt(weightInput.getText());

              if (name != null && !name.isEmpty()) {
                listGraph.connect(to, from, name, weight);
                drawLines(from, to);
                validInput = true;
                unsavedChanges = true;
              } else {
                alert.setTitle("Illegal name");
                alert.setHeaderText("Connection must have a name");
                alert.showAndWait();
              }
            } catch (NumberFormatException e) {
              alert.setTitle("Illegal weight");
              alert.setHeaderText("Connection must have a weight");
              alert.showAndWait();
            }
          } else if (result.get() == ButtonType.CANCEL) {
            validInput = true;
          }
        }
      } else {
        alert.setTitle("Error!");
        alert.setHeaderText("There can only be one path between places");
        alert.showAndWait();
      }
    } else {
        alert.setTitle("Error!");
        alert.setHeaderText("Tvo places must be selected!");
        alert.showAndWait();
    }
  }

  private void showConnection() {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    if (selectedButtons.size() == 2) {
      ArrayList<Node> nodes = getNodesFromBtn();
      Edge<Gui.Node> edge = listGraph.getEdgeBetween(nodes.get(0), nodes.get(1));

      if (edge != null) {
        alert.setAlertType(Alert.AlertType.INFORMATION);
        alert.setTitle("Showing connection");
        alert.setHeaderText(null);

        Label nameLabel = new Label("Connection name: " + edge.getName());
        Label weightLabel = new Label("Connection time: " + Integer.toString(edge.getWeight()));

        VBox popupLayout = new VBox(5);
        popupLayout.getChildren().addAll(nameLabel, weightLabel);
        alert.getDialogPane().setContent(popupLayout);
        alert.showAndWait();
      } else {
        alert.setTitle("No connection found");
        alert.setHeaderText("There is no connection between these places");
        alert.showAndWait();
      }
    } else {
      alert.setTitle("Error!");
      alert.setHeaderText("Not enough places marked");
      alert.showAndWait();
    }
  }

  private void changeConnection() {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    if (selectedButtons.size() == 2) {
      ArrayList<Node> nodes = getNodesFromBtn();
      Edge<Gui.Node> edge = listGraph.getEdgeBetween(nodes.get(0), nodes.get(1));

      if (edge != null) {
      Dialog<ButtonType> dialog = new Dialog<>();
      dialog.setTitle("Change a connection");

      Label nameLabel = new Label("Connection name: " + edge.getName());
      TextField weighTextField = new TextField();

      VBox popupLayout = new VBox(5);
      popupLayout.getChildren().addAll(nameLabel, new Label("New time: "), weighTextField);
      dialog.getDialogPane().setContent(popupLayout);
      dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
      boolean isValid = false;

      while (!isValid) {
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
          try {
            int weight = Integer.parseInt(weighTextField.getText());
            listGraph.setConnectionWeight(nodes.get(0),nodes.get(1), weight);
            isValid = true;
            unsavedChanges = true;
          } catch (NumberFormatException e) {
            alert.setTitle("Illegal weight");
            alert.setHeaderText("Not a valid weight");
            alert.showAndWait();
          }
        } else if (result.get() == ButtonType.CANCEL) {
          isValid = true;
        }
      }

      } else {
        alert.setTitle("No connection");
        alert.setHeaderText("No connection found between these places");
        alert.showAndWait();
      }
    } else {
      alert.setTitle("Error!");
      alert.setHeaderText("Not enough places selected");
      alert.showAndWait();
    }
  }

  private void findPath() {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    if (selectedButtons.size() == 2) {
      ArrayList<Node> nodes = getNodesFromBtn();
      if (listGraph.pathExists(nodes.get(0), nodes.get(1))) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Find path");

        TextArea output = new TextArea();
        output.setEditable(false);
        output.setWrapText(true);
        output.setPrefRowCount(8);

        VBox popupLayout = new VBox();
        popupLayout.getChildren().addAll(new Label("Path: "), output);
        dialog.getDialogPane().setContent(popupLayout);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        List<Edge<Gui.Node>> path = listGraph.getPath(nodes.get(0), nodes.get(1));
        StringBuilder outputString = new StringBuilder();
        int counter = 0;

        for (Edge<Gui.Node> edge : path) {
          outputString.append("-> " + edge.getDestination() + " by " + edge.getName() + " takes " + edge.getWeight() + "\n");
          counter += edge.getWeight();
        }
        outputString.append("\n Total time travelled: " + counter + "\n Total stops: " + path.size());

        output.appendText(outputString.toString());

        dialog.showAndWait();
      } else {
        alert.setTitle("No path");
        alert.setHeaderText("No path exists between these places");
        alert.showAndWait();
      }
    } else {
      alert.setTitle("Error!");
      alert.setHeaderText("Not enough places selected");
      alert.showAndWait();
    }
  }

  private ArrayList<Node> getNodesFromBtn() {
    Node n1 = null;
    Node n2 = null;
    String btn1 = selectedButtons.get(0).getText();
    String btn2 = selectedButtons.get(1).getText();

    for (Node node : listGraph.getNodes()) {
      if (btn1.equals(node.getName())) {
        n1 = node;
      } else if (btn2.equals(node.getName())) {
        n2 = node;
      }
    }

    if (n1 != null && n2 != null) {
      ArrayList<Node> result = new ArrayList<>();
      result.add(n1);
      result.add(n2);
      return result;
    } else {
      return new ArrayList<>();
    }
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
      enableBtn();
    }
  }

  //Öppnar en bild och anpassar fönstrets storlek efter bilden
  private void openMap(File fileName) {
    map = fileName;
    Image image = new Image(fileName.toURI().toString());
    ImageView view = new ImageView(image);

    double imageWidth = image.getWidth();
    double imageHeight = image.getHeight();

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
      graphPane.getChildren().clear();
      selectedButtons.clear();
      createGraph(selectedGraph);
      enableBtn();
      unsavedChanges = false;
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
        Node node = new Node(nodeLine[i], Double.parseDouble(nodeLine[i + 1].trim()), Double.parseDouble(nodeLine[i + 2].trim()));
        listGraph.add(node);
        createButton(node);

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
          drawLines(from, to);
        } catch (IllegalStateException e) {} //På grund av formatet i connect() så throwas ett illegalstateexception om man försöker lägga till en edge som redan finns, då den givna filen försöker göra detta så ignorerar vi helt enkelt den andra instansen med en trycatch 
      }
      System.out.println(listGraph.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //Drar linjer mellan olika noder
  private void drawLines(Node from, Node to) {
    Line line = new Line(from.getxCoordinate(), from.getyCoordinate(), to.getxCoordinate(), to.getyCoordinate());
    line.setStroke(Color.GRAY);
    line.setStrokeWidth(2);
    graphPane.getChildren().add(line);
  }

  //Ändrade så man direkt skickar en node som läggs till, för att kunna kombinera med att skapa nya noder
  private void createButton(Node node) {
    Button btn = new Button(node.getName());
    btn.setShape(new Circle(20));
    btn.setStyle("-fx-background-color: lightblue;");
    btn.setLayoutX(node.getxCoordinate() - 10);
    btn.setLayoutY(node.getyCoordinate() - 10);
    btn.toFront();
    btn.setOnAction(e -> {
      if (selectedButtons.contains(btn)) {
        selectedButtons.remove(btn);
        btn.setStyle("-fx-background-color: lightblue;");
      } else {
        if (selectedButtons.size() < 2) {
          selectedButtons.add(btn);
          btn.setStyle("-fx-background-color: red;");
        }
      }
    });
    //Lägg till eventhandlers för nodknappar här, skicka sedan btn vidare till metoderna så får ni rätt instans
    nodeButtons.add(btn);
    graphPane.getChildren().add(btn);
  }

  private void saveGraph() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Graph");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Graph Files", "*.graph"));
    fileChooser.setInitialDirectory(new File("images"));

    File file = fileChooser.showSaveDialog(stage);

    if (file != null) {
      try (PrintWriter writer = new PrintWriter(file)) {
        writer.println("File:" + map.getName());

        for (Node node : listGraph.getNodes()) {
          writer.printf("%s;%f;%f;", node.getName(), node.getxCoordinate(), node.getyCoordinate());
        }
        writer.println();

        for (Node node : listGraph.getNodes()) {
          for (Edge<Node> edge : listGraph.getEdgesFrom(node)) {
            writer.printf("%s;%s;%s;%d;%n", node.getName(), edge.getDestination().getName(), edge.getName(), edge.getWeight());
          }
        }

      unsavedChanges = false;

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void saveImage() {
    try {
      WritableImage image = scene.snapshot(null);
      File file = new File("capture.png");
      ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void exit() {
    if (unsavedChanges) {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Unsaved Changes");
      alert.setHeaderText("You have unsaved changes");
      alert.setContentText("You will lose your changes if you exit without saving");

      ButtonType ok = new ButtonType("OK");
      ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

      alert.getButtonTypes().setAll(ok, cancel);

      Optional<ButtonType> result = alert.showAndWait();

      if (result.isPresent()) {
        if (result.get() == ok) {
          Platform.exit();
        }
      }
    } else {
      Platform.exit();
    }
  }
}
