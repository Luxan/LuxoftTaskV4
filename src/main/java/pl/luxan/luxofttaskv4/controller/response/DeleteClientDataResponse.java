package pl.luxan.luxofttaskv4.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class DeleteClientDataResponse {

    private String primaryKey;

}
