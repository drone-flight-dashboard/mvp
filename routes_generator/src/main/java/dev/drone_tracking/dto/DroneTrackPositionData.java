package dev.drone_tracking.dto;
import org.json.JSONObject;
public record DroneTrackPositionData(String droneName, double x, double y) {
    final private static String DRONE_ID = "droneName";
    final private static String X_COORD = "x";
    final private static String Y_COORD = "y";

    DroneTrackPositionData getDroneTrackPositionData(String json) {
        JSONObject jsonObject = new JSONObject(json);
        return new DroneTrackPositionData(jsonObject.getString(DRONE_ID), jsonObject.getDouble(X_COORD), jsonObject.getDouble(Y_COORD));
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DRONE_ID, droneName);
        jsonObject.put(X_COORD, x);
        jsonObject.put(Y_COORD, y);
        return jsonObject.toString();
    }

}
