package com.example.filescanner.GUI;

import com.example.filescanner.GUI.Controllers.SceneController;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class KeyboardNavigationManager {

    public static void enableGlobalArrowNavigation(Scene scene) {

        scene.setOnKeyPressed(event -> {
            KeyCode key = event.getCode();

            switch (key) {
                case UP -> moveFocus(scene, -1);
                case DOWN -> moveFocus(scene, 1);
                case LEFT -> moveFocusSidebar(scene, -1);
                case RIGHT -> moveFocusSidebar(scene, 1);
                case ENTER -> activateFocusedNode(scene);
                case ESCAPE -> SceneController.goBack();
                default -> {}
            }
        });
    }

    // Vertical focus movement
    private static void moveFocus(Scene scene, int direction) {
        List<Node> focusables = getFocusableNodes(scene);
        Node current = scene.getFocusOwner();
        int index = focusables.indexOf(current);
        if (index == -1) return;

        int nextIndex = (index + direction + focusables.size()) % focusables.size();
        focusables.get(nextIndex).requestFocus();
    }

    // Horizontal sidebar navigation
    private static void moveFocusSidebar(Scene scene, int direction) {
        VBox sidebar = (VBox) scene.lookup("#sidebar");
        if (sidebar == null) return;

        List<Node> buttons = sidebar.getChildren().stream()
                .filter(Node::isFocusTraversable)
                .collect(Collectors.toList());

        Node current = scene.getFocusOwner();
        int index = buttons.indexOf(current);
        if (index == -1) return;

        int nextIndex = (index + direction + buttons.size()) % buttons.size();
        buttons.get(nextIndex).requestFocus();
    }

    // ENTER activates buttons or text fields
    private static void activateFocusedNode(Scene scene) {
        Node focused = scene.getFocusOwner();
        if (focused instanceof Button btn) {
            btn.fire();
        } else if (focused instanceof TextField tf) {
            tf.fireEvent(new javafx.event.ActionEvent());
        }
    }

    // Collect all focusable nodes
    private static List<Node> getFocusableNodes(Scene scene) {
        return scene.getRoot().lookupAll("*").stream()
                .filter(Node::isFocusTraversable)
                .collect(Collectors.toList());
    }
}
