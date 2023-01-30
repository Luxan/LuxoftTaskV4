package pl.luxan.luxofttaskv4.service.helper;

import pl.luxan.luxofttaskv4.model.ClientData;
import pl.luxan.luxofttaskv4.service.dto.ClientDataDTO;

public class EntityToDtoMapper {

    public static ClientDataDTO clientDataToClientDataDto(ClientData clientData) {
        return ClientDataDTO.builder()
                .primaryKey(clientData.getPrimaryKey())
                .name(clientData.getName())
                .description(clientData.getDescription())
                .updatedTimestamp(clientData.getUpdatedTimestamp())
                .build();
    }

}
