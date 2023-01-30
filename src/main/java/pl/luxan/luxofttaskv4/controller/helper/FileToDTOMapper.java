package pl.luxan.luxofttaskv4.controller.helper;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;
import pl.luxan.luxofttaskv4.controller.exception.InvalidRequestException;
import pl.luxan.luxofttaskv4.service.dto.ClientDataDTO;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileToDTOMapper {

    public static List<ClientDataDTO> multipartFileToClientDataDto(MultipartFile file) throws IOException, InvalidRequestException {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<ClientDataDTO> result = new ArrayList<>();
            Optional<ClientDataDTO> firstRow = readFirstRow(reader);
            firstRow.ifPresent(result::add);
            result.addAll(readRest(reader));
            return result;
        } catch (CsvException | IllegalStateException e) {
            throw new InvalidRequestException(e);
        }
    }

    private static Optional<ClientDataDTO> readFirstRow(CSVReader reader) throws CsvValidationException, IOException {
        String[] row = reader.readNext();
        if (row == null)
            return Optional.empty();
        return switch (row.length) {
            case 0 -> Optional.empty();
            case 1, 2, 3 -> Optional.ofNullable(buildClientDataDto(row));
            case 4 -> isHeader(row) ? Optional.empty() : Optional.ofNullable(buildClientDataDto(row));
            default -> throw new IllegalStateException("Unexpected value: " + row.length);
        };
    }

    private static boolean isHeader(String[] row) {
        return row[0].equals("PRIMARY_KEY") &&
                row[1].equals("NAME") &&
                row[2].equals("DESCRIPTION") &&
                row[3].equals("UPDATED_TIMESTAMP");
    }

    @NotNull
    private static List<ClientDataDTO> readRest(CSVReader reader) throws IOException, CsvException {
        return reader.readAll().stream()
                .filter(row -> row.length > 0)
                .map(FileToDTOMapper::buildClientDataDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Nullable
    private static ClientDataDTO buildClientDataDto(String[] rows) {
        ClientDataDTO.ClientDataDTOBuilder builder = ClientDataDTO.builder().primaryKey(rows[0]);
        setName(rows, builder);
        setDescription(rows, builder);
        return setTimestampAndBuild(rows, builder);
    }

    private static void setName(String[] row, ClientDataDTO.ClientDataDTOBuilder builder) {
        if (row.length > 1)
            builder.name(row[1]);
    }

    private static void setDescription(String[] row, ClientDataDTO.ClientDataDTOBuilder builder) {
        if (row.length > 2)
            builder.description(row[2]);
    }

    @Nullable
    private static ClientDataDTO setTimestampAndBuild(String[] row, ClientDataDTO.ClientDataDTOBuilder builder) {
        if (row.length > 3)
            try {
                builder.updatedTimestamp(StringTimestampConverter.stringToTimestamp(row[3]));
            } catch (DateTimeParseException e) {
                return null;
            }
        return builder.build();
    }

}
