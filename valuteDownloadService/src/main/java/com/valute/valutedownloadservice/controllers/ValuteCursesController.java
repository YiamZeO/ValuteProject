package com.valute.valutedownloadservice.controllers;

import com.valute.valutedownloadservice.services.ValuteCursesService;
import com.valute.xmldtos.DTOs.ValuteCurseListXml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/valute_curses")
public class ValuteCursesController {
    private final ValuteCursesService valuteCursesService;

    @Autowired
    public ValuteCursesController(ValuteCursesService valuteCursesService) {
        this.valuteCursesService = valuteCursesService;
    }

    @GetMapping("/download_valute_curses")
    public List<ValuteCurseListXml> downloadValuteCurses(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return valuteCursesService.downloadValuteCurses(startDate, endDate);
    }
}
