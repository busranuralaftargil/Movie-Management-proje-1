import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ActorController {

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField birthYearField;

    @FXML
    public void onSave(ActionEvent e) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            String sql = "INSERT INTO actor(name, birth_year) VALUES (?, ?) RETURNING actor_id";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, nameField.getText());
            ps.setInt(2, Integer.parseInt(birthYearField.getText()));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idField.setText(String.valueOf(rs.getInt(1)));
            }

            UiAlerts.info((Stage) idField.getScene().getWindow(), "SAVE", "Actor saved ✅");

        } catch (Exception ex) {
            UiAlerts.error((Stage) idField.getScene().getWindow(), "Error", ex.getMessage());
        }
    }

    @FXML
    public void onGet(ActionEvent e) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            String sql = "SELECT * FROM actor WHERE actor_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(idField.getText()));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                birthYearField.setText(String.valueOf(rs.getInt("birth_year")));
            } else {
                UiAlerts.warn((Stage) idField.getScene().getWindow(), "GET", "Actor not found!");
            }

        } catch (Exception ex) {
            UiAlerts.error((Stage) idField.getScene().getWindow(), "Error", ex.getMessage());
        }
    }

    @FXML
    public void onUpdate(ActionEvent e) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            String sql = "UPDATE actor SET name=?, birth_year=? WHERE actor_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, nameField.getText());
            ps.setInt(2, Integer.parseInt(birthYearField.getText()));
            ps.setInt(3, Integer.parseInt(idField.getText()));

            ps.executeUpdate();
            UiAlerts.info((Stage) idField.getScene().getWindow(), "UPDATE", "Actor updated ✅");

        } catch (Exception ex) {
            UiAlerts.error((Stage) idField.getScene().getWindow(), "Error", ex.getMessage());
        }
    }

    @FXML
    public void onDelete(ActionEvent e) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            String sql = "DELETE FROM actor WHERE actor_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(idField.getText()));

            ps.executeUpdate();
            onClear(null);

            UiAlerts.info((Stage) idField.getScene().getWindow(), "DELETE", "Actor deleted ✅");

        } catch (Exception ex) {
            UiAlerts.error((Stage) idField.getScene().getWindow(), "Error", ex.getMessage());
        }
    }

    @FXML
    public void onClear(ActionEvent e) {
        idField.clear();
        nameField.clear();
        birthYearField.clear();
    }

    @FXML
    public void onClose(ActionEvent e) {
        ((Stage) idField.getScene().getWindow()).close();
    }
}