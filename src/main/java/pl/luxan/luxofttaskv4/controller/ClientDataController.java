package pl.luxan.luxofttaskv4.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.luxan.luxofttaskv4.controller.exception.InvalidRequestException;
import pl.luxan.luxofttaskv4.controller.exception.ResourceNotFoundException;
import pl.luxan.luxofttaskv4.controller.helper.FileToDTOMapper;
import pl.luxan.luxofttaskv4.controller.response.ClientDataResponse;
import pl.luxan.luxofttaskv4.controller.response.DeleteClientDataResponse;
import pl.luxan.luxofttaskv4.controller.response.UploadClientDataResponse;
import pl.luxan.luxofttaskv4.controller.helper.StringTimestampConverter;
import pl.luxan.luxofttaskv4.service.ClientDataService;
import pl.luxan.luxofttaskv4.service.dto.ClientDataDTO;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.path}")
public class ClientDataController {

    @Value("${api.path}")
    private String apiPath;

    private final ClientDataService clientDataService;

    @Autowired
    ClientDataController(ClientDataService clientDataService) {
        this.clientDataService = clientDataService;
    }

    @PostMapping("uploadCSV")
    public ResponseEntity<UploadClientDataResponse> uploadClientData(
            @RequestParam("csvFile") MultipartFile multipartFile
    ) throws InvalidRequestException, IOException {
        List<ClientDataDTO> clientDataDTOS = FileToDTOMapper.multipartFileToClientDataDto(multipartFile);
        List<String> savedClientDataPrimaryKeys = new ArrayList<>();

        if (!clientDataDTOS.isEmpty()) {
            List<String> primaryKeys = clientDataDTOS.stream().map(ClientDataDTO::getPrimaryKey).toList();
            if (new HashSet<>(primaryKeys).size() != clientDataDTOS.size())
                throw new InvalidRequestException("Detected multiple client data rows with same PRIMARY_KEY");
            List<ClientDataDTO> savedClientDataDTOS = clientDataService.saveClientData(clientDataDTOS);
            savedClientDataPrimaryKeys = savedClientDataDTOS.stream()
                    .map(ClientDataDTO::getPrimaryKey)
                    .collect(Collectors.toList());
        }
        UploadClientDataResponse response = UploadClientDataResponse.builder()
                .totalNumberOfRows(clientDataDTOS.size())
                .primaryKeysUploaded(savedClientDataPrimaryKeys)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("clientData/{primaryKey}")
    public ResponseEntity<ClientDataResponse> getClientData(@PathVariable(value = "primaryKey") String primaryKey)
            throws ResourceNotFoundException {
        Optional<ClientDataDTO> optionalClientDataDTO = clientDataService.getClientDetailsByPrimaryKey(primaryKey);
        ClientDataDTO clientDataDTO = optionalClientDataDTO.orElseThrow(() ->
                new ResourceNotFoundException("Could not find client data with PRIMARY_KEY: " + primaryKey));
        ClientDataResponse response = ClientDataResponse.builder()
                .primaryKey(clientDataDTO.getPrimaryKey())
                .name(clientDataDTO.getName())
                .description(clientDataDTO.getDescription())
                .updatedTimestamp(StringTimestampConverter.timestampToString(clientDataDTO.getUpdatedTimestamp()))
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("clientData/{primaryKey}")
    public ResponseEntity<DeleteClientDataResponse> deleteClientData(@PathVariable(value = "primaryKey") String primaryKey)
            throws ResourceNotFoundException {
        Optional<ClientDataDTO> optionalClientDataDTO = clientDataService.deleteClientData(primaryKey);
        optionalClientDataDTO.orElseThrow(() ->
                new ResourceNotFoundException("Client data with this PRIMARY_KEY does not exist: " + primaryKey));
        DeleteClientDataResponse response = new DeleteClientDataResponse(optionalClientDataDTO.get().getPrimaryKey());
        return ResponseEntity.ok(response);
    }
}
