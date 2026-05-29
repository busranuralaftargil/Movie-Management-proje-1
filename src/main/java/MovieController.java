import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MovieController {

    @FXML private TextField idField;
    @FXML private TextField titleField;
    @FXML private TextField genreField;
    @FXML private TextField yearField;
    @FXML private TextField durationField;

    @FXML
    public void onGet(ActionEvent e) {
        Stage owner = (Stage) idField.getScene().getWindow();

        Integer id = parseInt(owner, safe(idField.getText()), "ID sayı olmalı.");
        if (id == null) return;

        String sql = "SELECT movie_id, title, genre, release_year, duration FROM movie WHERE movie_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idField.setText(String.valueOf(rs.getInt("movie_id")));
                    titleField.setText(rs.getString("title"));
                    genreField.setText(rs.getString("genre"));
                    yearField.setText(String.valueOf(rs.getInt("release_year")));
                    durationField.setText(String.valueOf(rs.getInt("duration")));
                    UiAlerts.info(owner, "GET", "Kayıt bulundu ✅");
                } else {
                    UiAlerts.warn(owner, "GET", "Bu ID ile film yok.");
                }
            }

        } catch (Exception ex) {
            UiAlerts.error(owner, "DB Hatası", ex.getMessage());
        }
    }

    @FXML
    public void onSave(ActionEvent e) {
        Stage owner = (Stage) idField.getScene().getWindow();

        String title = safe(titleField.getText());
        String genre = safe(genreField.getText());

        if (title.isEmpty()) { UiAlerts.warn(owner, "Eksik Alan", "TITLE boş olamaz."); return; }
        if (genre.isEmpty()) { UiAlerts.warn(owner, "Eksik Alan", "GENRE boş olamaz."); return; }

        Integer year = parseInt(owner, safe(yearField.getText()), "YEAR sayı olmalı.");
        if (year == null) return;

        Integer duration = parseInt(owner, safe(durationField.getText()), "DURATION sayı olmalı.");
        if (duration == null) return;

        // movie_id SERIAL olduğu için ID eklemiyoruz, DB otomatik verir
        String sql = "INSERT INTO movie(title, genre, release_year, duration) VALUES (?, ?, ?, ?) RETURNING movie_id";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, genre);
            ps.setInt(3, year);
            ps.setInt(4, duration);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idField.setText(String.valueOf(rs.getInt(1))); // yeni ID
                }
            }

            UiAlerts.info(owner, "SAVE", "Film kaydedildi ✅");

        } catch (Exception ex) {
            UiAlerts.error(owner, "DB Hatası", ex.getMessage());
        }
    }

    @FXML
    public void onUpdate(ActionEvent e) {
        Stage owner = (Stage) idField.getScene().getWindow();

        Integer id = parseInt(owner, safe(idField.getText()), "ID sayı olmalı.");
        if (id == null) return;

        String title = safe(titleField.getText());
        String genre = safe(genreField.getText());

        if (title.isEmpty()) { UiAlerts.warn(owner, "Eksik Alan", "TITLE boş olamaz."); return; }
        if (genre.isEmpty()) { UiAlerts.warn(owner, "Eksik Alan", "GENRE boş olamaz."); return; }

        Integer year = parseInt(owner, safe(yearField.getText()), "YEAR sayı olmalı.");
        if (year == null) return;

        Integer duration = parseInt(owner, safe(durationField.getText()), "DURATION sayı olmalı.");
        if (duration == null) return;

        String sql = "UPDATE movie SET title=?, genre=?, release_year=?, duration=? WHERE movie_id=?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, genre);
            ps.setInt(3, year);
            ps.setInt(4, duration);
            ps.setInt(5, id);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                UiAlerts.info(owner, "UPDATE", "Güncellendi ✅");
            } else {
                UiAlerts.warn(owner, "UPDATE", "Bu ID ile film yok.");
            }

        } catch (Exception ex) {
            UiAlerts.error(owner, "DB Hatası", ex.getMessage());
        }
    }

    @FXML
    public void onDelete(ActionEvent e) {
        Stage owner = (Stage) idField.getScene().getWindow();

        Integer id = parseInt(owner, safe(idField.getText()), "ID sayı olmalı.");
        if (id == null) return;

        if (!UiAlerts.confirm(owner, "DELETE", "ID=" + id + " filmi silmek istiyor musun?")) return;

        String sql = "DELETE FROM movie WHERE movie_id=?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                onClear(null);
                UiAlerts.info(owner, "DELETE", "Silindi ✅");
            } else {
                UiAlerts.warn(owner, "DELETE", "Bu ID ile film yok.");
            }

        } catch (Exception ex) {
            UiAlerts.error(owner, "DB Hatası", ex.getMessage());
        }
    }

    @FXML
    public void onClear(ActionEvent e) {
        idField.clear();
        titleField.clear();
        genreField.clear();
        yearField.clear();
        durationField.clear();
    }

    @FXML
    public void onClose(ActionEvent e) {
        ((Stage) idField.getScene().getWindow()).close();
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private static Integer parseInt(Stage owner, String text, String errMsg) {
        if (text == null || text.trim().isEmpty()) {
            UiAlerts.warn(owner, "Eksik Alan", "Bu alan boş olamaz.");
            return null;
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException ex) {
            UiAlerts.error(owner, "Hatalı Giriş", errMsg);
            return null;
        }
    }
}