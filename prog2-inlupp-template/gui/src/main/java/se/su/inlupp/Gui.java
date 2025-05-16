package se.su.inlupp;

import java.io.File;
import java.util.List;

import javax.swing.border.Border;

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

  private Stage stage;
  //Meny-knappar
  private MenuBar menuBar;
  private MenuItem newMapBtn;
  private MenuItem openMapBtn;
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

  public void start(Stage stage) {
      this.stage = stage;
      stage.setResizable(false);
      root = new BorderPane();

      VBox layout = createTopLayout();
      root.setTop(layout);

      //Meny eventhandlers
      newMapBtn.setOnAction(e -> newMapCreation());
      //openMapBtn.setOnAction(e -> openMap("images/europa.gif"));

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
      openMapBtn = new MenuItem("Open");
      saveMapBtn = new MenuItem("Save");
      saveImageBtn = new MenuItem("Save image");
      exitBtn = new MenuItem("Exit");

      Menu file = new Menu("File");
      file.getItems().addAll(newMapBtn, openMapBtn, saveMapBtn, saveImageBtn, exitBtn);

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
}
