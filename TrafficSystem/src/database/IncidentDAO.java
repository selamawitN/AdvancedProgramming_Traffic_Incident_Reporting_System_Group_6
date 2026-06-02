package database;

import models.Incident;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncidentDAO {

    public boolean saveIncident(Incident incident) {
        String sql = "INSERT INTO incidents (type, location, severity, description, status, reported_by, reporter_name, latitude, longitude) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, incident.getType());
            ps.setString(2, incident.getLocation());
            ps.setString(3, incident.getSeverity());
            ps.setString(4, incident.getDescription());
            ps.setString(5, incident.getStatus());
            if (incident.getReportedById() == 0) ps.setNull(6, Types.INTEGER);
            else ps.setInt(6, incident.getReportedById());
            ps.setString(7, incident.getReporterName() == null ? "Anonymous" : incident.getReporterName());
            if (incident.getLatitude() == null) ps.setNull(8, Types.DOUBLE);
            else ps.setDouble(8, incident.getLatitude());
            if (incident.getLongitude() == null) ps.setNull(9, Types.DOUBLE);
            else ps.setDouble(9, incident.getLongitude());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) incident.setId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<Incident> getAllIncidents() {
        List<Incident> list = new ArrayList<>();
        String sql = "SELECT id,type,location,severity,description,status,reported_by,reporter_name,reported_at,latitude,longitude FROM incidents ORDER BY reported_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Incident inc = new Incident(rs.getString("type"), rs.getString("location"), rs.getString("severity"), rs.getString("description"), rs.getInt("reported_by"));
                inc.setId(rs.getInt("id"));
                inc.setStatus(rs.getString("status"));
                inc.setReporterName(rs.getString("reporter_name"));
                double lat = rs.getDouble("latitude");
                double lng = rs.getDouble("longitude");
                if (!rs.wasNull()) { inc.setLatitude(lat); inc.setLongitude(lng); }
                list.add(inc);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Incident> getCriticalIncidents() {
        List<Incident> list = new ArrayList<>();
        String sql = "SELECT id,type,location,severity,description,status,reported_by,reporter_name,reported_at,latitude,longitude FROM incidents WHERE severity='Critical' AND status='Open'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Incident inc = new Incident(rs.getString("type"), rs.getString("location"), rs.getString("severity"), rs.getString("description"), rs.getInt("reported_by"));
                inc.setId(rs.getInt("id"));
                inc.setStatus(rs.getString("status"));
                inc.setReporterName(rs.getString("reporter_name"));
                double lat = rs.getDouble("latitude");
                double lng = rs.getDouble("longitude");
                if (!rs.wasNull()) { inc.setLatitude(lat); inc.setLongitude(lng); }
                list.add(inc);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean updateStatus(int incidentId, String newStatus) {
        String sql = "UPDATE incidents SET status=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, incidentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
