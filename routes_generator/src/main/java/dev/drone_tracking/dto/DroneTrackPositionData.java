package dev.drone_tracking.dto;
import org.json.JSONObject;
public record DroneTrackPositionData(String droneName, double x, double y) {
    
    DroneTrackPositionData getDroneTrackPositionData(String json) {
        JSONObject jsonObject = new JSONObject(json);
        return new DroneTrackPositionData(jsonObject.getString(json), x, y)
    }

}
