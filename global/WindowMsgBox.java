package b451_Project.global;

import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * to display message box
 * */
public class WindowMsgBox {

    private WindowMsgBox(){};

    public static void InfoMessage(String header, String txt)
    {
        Platform.runLater(() ->
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(header);
            alert.setContentText(txt);
            alert.show();
        });
    }

}
