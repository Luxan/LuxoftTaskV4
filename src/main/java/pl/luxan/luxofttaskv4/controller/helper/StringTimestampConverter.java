package pl.luxan.luxofttaskv4.controller.helper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class StringTimestampConverter {

    private static final DateTimeFormatter ISO_8601_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'");

    public static Timestamp stringToTimestamp(String timestamp) throws DateTimeParseException {
        LocalDateTime localDateTime = LocalDateTime.from(ISO_8601_FORMATTER.parse(timestamp));
        return Timestamp.valueOf(localDateTime);
    }

    public static String timestampToString(Timestamp timestamp) throws DateTimeParseException {
        return ISO_8601_FORMATTER.format(timestamp.toLocalDateTime());
    }

}
