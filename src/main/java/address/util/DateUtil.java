package address.util;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtil {
    // wzór daty
    private static final String DATE_PATTERN = "dd.MM.yyyy";

    // formatter daty
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    // formatowanie daty na string
    public static String format(LocalDate date) {
        if (date == null) {
            return null;
        }

        return DATE_FORMATTER.format(date);
    }

    // formatowanie stringa na datę
    public static LocalDate parse(String dateString) {
        try {
            return DATE_FORMATTER.parse(dateString, LocalDate::from);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // sprawdzenie czy string jest poprawną datą
    public static boolean validDate(String dateString) {
        return DateUtil.parse(dateString) != null;
    }
}
