package com.prj.LoneHPManagement.Controller;

import com.prj.LoneHPManagement.Service.MyanmarHolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class HolidayController {

    private final MyanmarHolidayService myanmarHolidayService;

    @Autowired
    public HolidayController(MyanmarHolidayService myanmarHolidayService) {
        this.myanmarHolidayService = myanmarHolidayService;
    }
    @GetMapping("/check/{date}")
    public HolidayCheckResponse checkHoliday(@PathVariable String date) {
        LocalDate parsedDate = LocalDate.parse(date);
        boolean isHoliday = myanmarHolidayService.isHoliday(parsedDate);
        boolean isWeekend = myanmarHolidayService.isWeekend(parsedDate);
        boolean isPublicHoliday = isHoliday && !isWeekend;

        return new HolidayCheckResponse(parsedDate, isHoliday, isPublicHoliday, isWeekend);
    }

    @GetMapping("/check/today")
    public HolidayCheckResponse checkToday() {
        return checkHoliday(LocalDate.now().toString());
    }
    public record HolidayCheckResponse(LocalDate date, boolean isHoliday, boolean isPublicHoliday, boolean isWeekend) {}
}