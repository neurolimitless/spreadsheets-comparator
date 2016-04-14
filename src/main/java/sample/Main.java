package sample;

import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Thread GUI = new Thread(new Runnable() {
            @Override
            public void run() {
                Menu menu = new Menu();
                menu.launch();
            }
        });
        GUI.start();


    }
}
