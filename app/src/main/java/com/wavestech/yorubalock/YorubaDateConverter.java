package com.wavestech.yorubalock;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class YorubaDateConverter {

    String[] numbers = {
        "Kìíní",
        "Kejì",
        "Kęta",
        "Kęrin",
        "Kaàrún",
        "Kefà",
        "Keje",
        "Kejo",
        "Keèmí",
        "Keèwá",
        "Kokànlá",
        "Kejìla",
        "Ketàlá",
        "Kerìnlá",
        "Keèdogún",
        "Keèrìndinlogún",
        "Ketàdínlógún",
        "Kejìdínlógún",
        "Kandínlógún",
        "Ogún",
        "Kanlelógún",
        "Kejilelógún",
        "Ketalelógún",
        "Kerinlelógún",
        "Keedógbòn",
        "Kerindinlógbòn",
        "Ketadinlógbòn",
        "Ketadinlógbòn",
        "Kejidinlógbòn",
        "Kandinlógbòn",
        "Ogbòn",
        "Kanlelógbòn"
    };

    public String getDateString() {
        LocalDate currentDate = LocalDate.now();

        // get the day number (1-31)
        int day = currentDate.getDayOfMonth();

        // get the month number (1-12)
        int month = currentDate.getMonthValue();

        // get the year number
        int year = currentDate.getYear();

        return getDayText(day) + ", " + getMonthText(month) + ", " + getyearText(year);
    }
    private String getDayText(int day) {
        String text = "Ọjọ ";

        return text + numbers[day - 1];
    }

    private String getMonthText(int month) {
        String text = "Osù ";

        return text + numbers[month - 1];
    }

    private String getyearText(int year) {
        String text = "Odún ";
        return text + year;
    }

}
