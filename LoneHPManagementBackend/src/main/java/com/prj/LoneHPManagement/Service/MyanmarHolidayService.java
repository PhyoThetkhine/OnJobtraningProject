package com.prj.LoneHPManagement.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MyanmarHolidayService {
    private final WebClient webClient;
    private final Map<Integer, Set<LocalDate>> holidayCache = new ConcurrentHashMap<>();

    @Value("${calendarific.api.key}")
    private String apiKey;

    public MyanmarHolidayService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://calendarific.com/api/v2/holidays").build();
    }
    public boolean isHoliday(LocalDate date) {
        int year = date.getYear();
        Set<LocalDate> holidays = holidayCache.computeIfAbsent(year, this::fetchHolidays);
        return holidays.contains(date);
    }

    public boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek().getValue() > 5; // Saturday or Sunday
    }


    public Set<LocalDate> fetchHolidays(int year) {
        try {
            CalendarificHolidayResponse apiResponse = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("api_key", apiKey)
                            .queryParam("country", "MM") // Myanmar country code
                            .queryParam("year", year)
                            .build())
                    .retrieve()
                    .bodyToMono(CalendarificHolidayResponse.class)
                    .block();

            return extractDatesFromResponse(apiResponse);
        } catch (Exception e) {
            return getDefaultHolidays(year);
        }
    }

    private Set<LocalDate> extractDatesFromResponse(CalendarificHolidayResponse apiResponse) {
        Set<LocalDate> dates = new HashSet<>();
        if (apiResponse != null && apiResponse.getResponse() != null && apiResponse.getResponse().getHolidays() != null) {
            for (CalendarificHolidayResponse.Holiday holiday : apiResponse.getResponse().getHolidays()) {
                String dateStr = holiday.getDate().getIso(); // Example: "2024-01-01"
                LocalDate date = LocalDate.parse(dateStr);
                dates.add(date);
            }
        }
        return dates;
    }

    private Set<LocalDate> getDefaultHolidays(int year) {
        // Common Myanmar public holidays
        return Set.of(
                LocalDate.of(year, 1, 4),   // Independence Day
                LocalDate.of(year, 2, 12),  // Union Day
                LocalDate.of(year, 3, 2),   // Peasants' Day
                LocalDate.of(year, 3, 27),  // Armed Forces Day
                LocalDate.of(year, 4, 13),  // Thingyan Festival (approximate)
                LocalDate.of(year, 4, 14),
                LocalDate.of(year, 4, 15),
                LocalDate.of(year, 4, 16),
                LocalDate.of(year, 12, 25)  // Christmas
        );
    }

    // DTO classes for Calendarific response
    public static class CalendarificHolidayResponse {
        private Response response;

        public Response getResponse() {
            return response;
        }

        public void setResponse(Response response) {
            this.response = response;
        }

        public static class Response {
            private List<Holiday> holidays;

            public List<Holiday> getHolidays() {
                return holidays;
            }

            public void setHolidays(List<Holiday> holidays) {
                this.holidays = holidays;
            }
        }

        public static class Holiday {
            private String name;
            private Date date;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Date getDate() {
                return date;
            }

            public void setDate(Date date) {
                this.date = date;
            }
        }

        public static class Date {
            private String iso; // The holiday date in ISO format (e.g., "2024-01-01")

            public String getIso() {
                return iso;
            }

            public void setIso(String iso) {
                this.iso = iso;
            }
        }
    }
}
