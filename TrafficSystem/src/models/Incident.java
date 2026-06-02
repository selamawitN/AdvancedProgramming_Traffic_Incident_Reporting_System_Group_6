package models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Incident implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String type;
    private String location;
    private String severity;
    private String description;
    private String status;
    private int reportedById;
    private String reporterName;
    private LocalDateTime reportedAt;
    private Double latitude;   
    private Double longitude;  

    public Incident(String type, String location, String severity,
                    String description, String reporterName) {
        this.type = type;
        this.location = location;
        this.severity = severity;
        this.description = description;
        this.reporterName = reporterName;
        this.reportedById = 0;
        this.status = "Open";
        this.reportedAt = LocalDateTime.now();
        this.latitude = null;
        this.longitude = null;
    }

    public Incident(String type, String location, String severity,
                    String description, int reportedById) {
        this.type = type;
        this.location = location;
        this.severity = severity;
        this.description = description;
        this.reportedById = reportedById;
        this.reporterName = "";
        this.status = "Open";
        this.reportedAt = LocalDateTime.now();
        this.latitude = null;
        this.longitude = null;
    }

    public int getId() { return id; }
    public String getType() { return type; }
    public String getLocation() { return location; }
    public String getSeverity() { return severity; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public int getReportedById() { return reportedById; }
    public String getReporterName() { return reporterName; }
    public LocalDateTime getReportedAt() { return reportedAt; }
    public Double getLatitude() { return latitude; }      
    public Double getLongitude() { return longitude; }    

    public void setId(int id) { this.id = id; }
    public void setStatus(String status) { this.status = status; }
    public void setReporterName(String name) { this.reporterName = name; }
    public void setReportedAt(LocalDateTime t) { this.reportedAt = t; }
    public void setLatitude(Double lat) { this.latitude = lat; }     
    public void setLongitude(Double lng) { this.longitude = lng; }    

    @Override
    public String toString() {
        return "Incident[id=" + id + ", type=" + type +
               ", location=" + location + ", severity=" + severity +
               ", status=" + status + ", reporter=" + reporterName + "]";
    }
}
