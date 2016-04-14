package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Optional;

public class Menu extends Application {

    public void launch() {
        Application.launch();
    }

    private static final int WIDTH = 250;   //7
    private static final int HEIGHT = 400;  //10
    private static final int BUTTON_WIDTH = 135;
    private static final int BUTTON_HEIGHT = 60;
    private static Button[] buttons;
    private static ProgressIndicator spinner;

    public static void changeVisible() {
        for (Button button : buttons) {
            button.setVisible(spinner.isVisible());
        }
        spinner.setVisible(!spinner.isVisible());
    }

    public static void notifyUser() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Calculating is finished");
        alert.setHeaderText("Processing is finished.");
        alert.setContentText("Would you like to continue or to save the result?");
        ButtonType ok = new ButtonType("Continue");
        ButtonType save = new ButtonType("Save");
        alert.getButtonTypes().setAll(ok, save);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ok) {
            alert.close();
        } else {
            ExcelProcessor.saveOutput();
            buttons[3].setDisable(true);
            alert.close();
        }
    }


    @Override
    public void start(final Stage stage) throws Exception {
        GridPane pane = new GridPane();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);
        buttons = new Button[]{new Button("Client Points"),
                new Button("Employee"),
                new Button("Execute"),
                new Button("Save")};
        spinner = new ProgressIndicator();
        buttons[3].setDisable(true);
        final FileChooser fileChooser = new FileChooser();
        File file = new File("config.ini");
        try {
            if (file.exists()) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                ExcelProcessor.setLastPath(bufferedReader.readLine());
                fileChooser.setInitialDirectory(new File(ExcelProcessor.getLastPath()));
            }
        } catch (Exception e) {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            System.out.println("Config.ini is corrupted.");
        }
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel files (*.xlsx *.xls)", "*.xlsx", "*.xls"));
        for (Button button : buttons) {
            button.setPrefHeight(BUTTON_HEIGHT);
            button.setPrefWidth(BUTTON_WIDTH);
            button.getStylesheets().add(0, "button.css");
        }
        buttons[0].setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (ExcelProcessor.getLastPath() != null)
                    fileChooser.setInitialDirectory(new File(ExcelProcessor.getLastPath()));
                ExcelProcessor.setFirstFile(fileChooser.showOpenDialog(stage));
            }
        });
        buttons[1].setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (ExcelProcessor.getLastPath() != null)
                    fileChooser.setInitialDirectory(new File(ExcelProcessor.getLastPath()));
                ExcelProcessor.setSecondFile(fileChooser.showOpenDialog(stage));
            }
        });
        buttons[2].setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (ExcelProcessor.isReady()) {
                    changeVisible();
                    buttons[0].setDisable(true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ExcelProcessor.execute();
                        }
                    }).start();
                    buttons[3].setDisable(false);
                } else {
                    System.out.println("Files aren't loaded");
                }
            }
        });
        buttons[3].setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ExcelProcessor.saveOutput();
            }
        });
        VBox vBox = new VBox(1, buttons);
        spinner.setProgress(-1d);
        spinner.setVisible(false);
        pane.getChildren().add(vBox);
        pane.getChildren().add(spinner);
        vBox.setPadding(new Insets(50, 50, 50, 50));
        stage.setTitle("EXTools");
        Scene scene = new Scene(pane);
        pane.setStyle("-fx-background: #00a7d4;");
        stage.setScene(scene);
        stage.getIcons().add(new Image(Main.class.getResource("/xls.png").toURI().toString()));
        stage.show();
    }

}
