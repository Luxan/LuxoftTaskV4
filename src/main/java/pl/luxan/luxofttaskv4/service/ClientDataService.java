package pl.luxan.luxofttaskv4.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.luxan.luxofttaskv4.model.ClientData;
import pl.luxan.luxofttaskv4.repository.ClientDataRepository;
import pl.luxan.luxofttaskv4.service.dto.ClientDataDTO;
import pl.luxan.luxofttaskv4.service.helper.DtoToEntityMapper;
import pl.luxan.luxofttaskv4.service.helper.EntityToDtoMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientDataService {

    private final ClientDataRepository clientDataRepository;

    @Autowired
    ClientDataService(ClientDataRepository clientDataRepository) {
        this.clientDataRepository = clientDataRepository;
    }

    @Transactional(readOnly = true)
    public Optional<ClientDataDTO> getClientDetailsByPrimaryKey(String clientId) {
        Optional<ClientData> optionalClientData = clientDataRepository.findByPrimaryKey(clientId);
        if (optionalClientData.isPresent()) {
            ClientData clientdata = optionalClientData.get();
            return Optional.of(EntityToDtoMapper.clientDataToClientDataDto(clientdata));
        } else return Optional.empty();
    }

    @Transactional
    public List<ClientDataDTO> saveClientData(List<ClientDataDTO> clientDataDtos) {
        return clientDataDtos.stream()
                .map(DtoToEntityMapper::clientDataDtoToClientData)
                .map(clientDataRepository::save)
                .map(EntityToDtoMapper::clientDataToClientDataDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<ClientDataDTO> deleteClientData(String primaryKey) {
        Optional<ClientData> foundClientData = clientDataRepository.findByPrimaryKey(primaryKey);
        if (foundClientData.isPresent()) {
            ClientData clientdata = foundClientData.get();
            clientDataRepository.delete(clientdata);
            return Optional.of(EntityToDtoMapper.clientDataToClientDataDto(clientdata));
        } else return Optional.empty();
    }
}
