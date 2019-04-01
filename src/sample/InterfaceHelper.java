package sample;

import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class InterfaceHelper {

    // Dialog
    public static String getInputDialog(String title, String header, String inputRequest)
    {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(inputRequest);

        Optional<String> result = dialog.showAndWait();

        return result.get();

    }
}
