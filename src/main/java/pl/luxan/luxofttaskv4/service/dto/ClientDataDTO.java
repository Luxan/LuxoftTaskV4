package pl.luxan.luxofttaskv4.service.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;
import java.util.Date;

@Data
@Builder
@EqualsAndHashCode
public class ClientDataDTO {

    private String primaryKey;
    private String name;
    private String description;
    private Timestamp updatedTimestamp;

}
