package dev.drone_tracking.DynamoInteraction;

import java.time.Instant;

import dev.drone_tracking.dto.DroneTrackPositionData;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class DynamoClient {
    private static String ROW_FLIGHT_DATA_TABLE_NAME = "row_flight_data";
    private static final DynamoDbEnhancedClient DYNAMODB_CLIENT = DynamoDbEnhancedClient.builder().build();
    private static final DynamoDbTable<RowFlightData> LAST_VALUE_TABLE = DYNAMODB_CLIENT.table(
            ROW_FLIGHT_DATA_TABLE_NAME,
            TableSchema.fromBean(RowFlightData.class));
    public void putData(DroneTrackPositionData data) {
        RowFlightData rowFlightData = data.getRowFlightData();
        rowFlightData.setTimestamp(Instant.now());
        LAST_VALUE_TABLE.putItem(rowFlightData);
    }
}
