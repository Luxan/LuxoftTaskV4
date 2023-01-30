package pl.luxan.luxofttaskv4.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Builder
@Data
@AllArgsConstructor
public class ClientDataResponse {

    private String primaryKey;
    private String name;
    private String description;
    private String updatedTimestamp;

}
