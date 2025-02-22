package dev.drone_tracking.DynamoInteraction;

import java.time.Instant;

import software.amazon.awssdk.enhanced.dynamodb.extensions.annotations.DynamoDbAutoGeneratedTimestampAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class RowFlightData {
    private String droneId;
    private Instant timestamp;
    private Double x;
    private Double y;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("drone_id")
    public String getDroneId() {
        return droneId;
    }
     public void setDroneId(String droneId) {
        this.droneId = droneId;
     }

     @DynamoDbSortKey
     @DynamoDbAutoGeneratedTimestampAttribute
     public Instant getTimestamp() {
        return timestamp;
     }

     public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
     }

     public Double getX() {
        return x;
     }

     public void setX(Double x ) {
        this.x = x;
     }

     public Double getY() {
        return y;
     }

     public void setY(Double y ) {
        this.y = y;
     }


}
