package pl.luxan.luxofttaskv4.service.helper;

import pl.luxan.luxofttaskv4.model.ClientData;
import pl.luxan.luxofttaskv4.service.dto.ClientDataDTO;

public class DtoToEntityMapper {

    public static ClientData clientDataDtoToClientData(ClientDataDTO clientDTO) {
        return ClientData.builder()
                .primaryKey(clientDTO.getPrimaryKey())
                .name(clientDTO.getName())
                .description(clientDTO.getDescription())
                .updatedTimestamp(clientDTO.getUpdatedTimestamp())
                .build();
    }

}
