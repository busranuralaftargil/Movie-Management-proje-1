import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MovieActorController {

    @FXML private TextField idField;
    @FXML private TextField movieIdField;
    @FXML private TextField actorIdField;

    @FXML
    public void onGet(ActionEvent e) {
        Stage owner = (Stage) idField.getScene().getWindow();

        String idText = safe(idField.getText());
        String mText = safe(movieIdField.getText());
        String aText = safe(actorIdField.getText());

        // Öncelik: ID varsa ID ile getir
        if (!idText.isEmpty()) {
            Integer id = parseInt(owner, idText, "ID sayı olmalı.");
            if (id == null) return;

            String sql = "SELECT id, movie_id, actor_id FROM movie_actor WHERE id = ?";
            try (Connection c = DatabaseConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idField.setText(String.valueOf(rs.getInt("id")));
                        movieIdField.setText(String.valueOf(rs.getInt("movie_id")));
                        actorIdField.setText(String.valueOf(rs.getInt("actor_id")));
                        UiAlerts.info(owner, "GET", "Kayıt bulundu ✅");
                    } else {
                        UiAlerts.warn(owner, "GET", "Bu ID ile kayıt yok.");
                    }
                }
            } catch (Exception ex) {
                UiAlerts.error(owner, "DB Hatası", ex.getMessage());
            }
            return;
        }

        // ID yoksa Movie+Actor ile getir
        Integer movieId = parseInt(owner, mText, "Movie ID sayı olmalı.");
        if (movieId == null) return;
        Integer actorId = parseInt(owner, aText, "Actor ID sayı olmalı.");
        if (actorId == null) return;

        String sql = "SELECT id, movie_id, actor_id FROM movie_actor WHERE movie_id = ? AND actor_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, movieId);
            ps.setInt(2, actorId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idField.setText(String.valueOf(rs.getInt("id")));
                    movieIdField.setText(String.valueOf(rs.getInt("movie_id")));
                    actorIdField.setText(String.valueOf(rs.getInt("actor_id")));
                    UiAlerts.info(owner, "GET", "Kayıt bulundu ✅");
                } else {
                    UiAlerts.warn(owner, "GET", "Bu Movie+Actor eşleşmesi yok.");
                }
            }
        } catch (Exception ex) {
            UiAlerts.error(owner, "DB Hatası", ex.getMessage());
        }
    }

    @FXML
    public void onDelete(ActionEvent e) {
        Stage owner = (Stage) idField.getScene().getWindow();

        String idText = safe(idField.getText());
        String mText = safe(movieIdField.getText());
        String aText = safe(actorIdField.getText());

        // Delete öncelik: ID varsa ID ile sil
        if (!idText.isEmpty()) {
            Integer id = parseInt(owner, idText, "ID sayı olmalı.");
            if (id == null) return;

            if (!UiAlerts.confirm(owner, "DELETE", "ID=" + id + " kaydını silmek istiyor musun?")) return;

            String sql = "DELETE FROM movie_actor WHERE id = ?";
            try (Connection c = DatabaseConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, id);
                int affected = ps.executeUpdate();
                if (affected > 0) {
                    clearFields();
                    UiAlerts.info(owner, "DELETE", "Silindi ✅");
                } else {
                    UiAlerts.warn(owner, "DELETE", "Silinecek kayıt bulunamadı.");
                }
            } catch (Exception ex) {
                UiAlerts.error(owner, "DB Hatası", ex.getMessage());
            }
            return;
        }

        // ID yoksa Movie+Actor ile sil
        Integer movieId = parseInt(owner, mText, "Movie ID sayı olmalı.");
        if (movieId == null) return;
        Integer actorId = parseInt(owner, aText, "Actor ID sayı olmalı.");
        if (actorId == null) return;

        if (!UiAlerts.confirm(owner, "DELETE", "Movie=" + movieId + " & Actor=" + actorId + " ilişkisini silmek istiyor musun?")) return;

        String sql = "DELETE FROM movie_actor WHERE movie_id = ? AND actor_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, movieId);
            ps.setInt(2, actorId);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                clearFields();
                UiAlerts.info(owner, "DELETE", "Silindi ✅");
            } else {
                UiAlerts.warn(owner, "DELETE", "Silinecek eşleşme bulunamadı.");
            }
        } catch (Exception ex) {
            UiAlerts.error(owner, "DB Hatası", ex.getMessage());
        }
    }

    @FXML
    public void onClear(ActionEvent e) {
        clearFields();
    }

    @FXML
    public void onClose(ActionEvent e) {
        ((Stage) idField.getScene().getWindow()).close();
    }

    private void clearFields() {
        idField.clear();
        movieIdField.clear();
        actorIdField.clear();
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

    @FXML
    public void onSave(ActionEvent e) {
        Stage owner = (Stage) idField.getScene().getWindow();

        Integer movieId = parseInt(owner, safe(movieIdField.getText()), "Movie ID sayı olmalı.");
        if (movieId == null) return;

        Integer actorId = parseInt(owner, safe(actorIdField.getText()), "Actor ID sayı olmalı.");
        if (actorId == null) return;

        // Not: movie_actor tablonuzda unique (movie_id, actor_id) varsa duplicate engellenir.
        String sql = "INSERT INTO movie_actor(movie_id, actor_id) VALUES (?, ?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, movieId);
            ps.setInt(2, actorId);

            ps.executeUpdate();
            UiAlerts.info(owner, "SAVE", "İlişki kaydedildi ✅");

        } catch (Exception ex) {
            UiAlerts.error(owner, "DB Hatası", ex.getMessage());
        }
    }

    @FXML
    public void onUpdate(ActionEvent e) {
        Stage owner = (Stage) idField.getScene().getWindow();

        Integer id = parseInt(owner, safe(idField.getText()), "ID sayı olmalı.");
        if (id == null) return;

        Integer movieId = parseInt(owner, safe(movieIdField.getText()), "Movie ID sayı olmalı.");
        if (movieId == null) return;

        Integer actorId = parseInt(owner, safe(actorIdField.getText()), "Actor ID sayı olmalı.");
        if (actorId == null) return;

        String sql = "UPDATE movie_actor SET movie_id = ?, actor_id = ? WHERE id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, movieId);
            ps.setInt(2, actorId);
            ps.setInt(3, id);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                UiAlerts.info(owner, "UPDATE", "Güncellendi ✅");
            } else {
                UiAlerts.warn(owner, "UPDATE", "Bu ID ile kayıt yok.");
            }
        } catch (Exception ex) {
            UiAlerts.error(owner, "DB Hatası", ex.getMessage());
        }
    }
}