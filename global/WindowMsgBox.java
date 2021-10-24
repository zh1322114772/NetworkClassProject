package b451_Project.global;

import javafx.scene.control.Alert;

/**
 * to display message box
 * */
public class WindowMsgBox {

    private WindowMsgBox(){};

    public static void ErrorMessage(String header, String txt)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(txt);
        alert.show();
    }

}
