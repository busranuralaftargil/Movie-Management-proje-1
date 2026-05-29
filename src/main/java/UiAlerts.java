import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

public class UiAlerts {

    public static void info(Window owner, String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setTitle(title);
        a.setHeaderText(null);
        if (owner != null) a.initOwner(owner);
        a.showAndWait();
    }

    public static void warn(Window owner, String title, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        a.setTitle(title);
        a.setHeaderText(null);
        if (owner != null) a.initOwner(owner);
        a.showAndWait();
    }

    public static void error(Window owner, String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle(title);
        a.setHeaderText(null);
        if (owner != null) a.initOwner(owner);
        a.showAndWait();
    }

    public static boolean confirm(Window owner, String title, String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        a.setTitle(title);
        a.setHeaderText(null);
        if (owner != null) a.initOwner(owner);
        return a.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }
}