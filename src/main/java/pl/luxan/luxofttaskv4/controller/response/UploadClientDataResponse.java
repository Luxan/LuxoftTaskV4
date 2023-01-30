package pl.luxan.luxofttaskv4.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class UploadClientDataResponse {

    private Integer totalNumberOfRows;
    private List<String> primaryKeysUploaded;

}
