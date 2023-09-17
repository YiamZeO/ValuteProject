package com.valute.valute_app.controllers;

import com.valute.valute_app.services.ValuteCursesService;
import com.valute.valute_app.specsDTOs.ValuteCursesSpecDto;
import com.valute.valute_app.xmlDTOs.ValuteCurseListXml;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/delete_by_spec")
    public void deleteBySpec(@RequestBody ValuteCursesSpecDto valuteCursesSpecDto) {
        valuteCursesService.deleteBySpec(valuteCursesSpecDto);
    }

    @GetMapping("/download_valute_curses")
    public void downloadValuteCurses(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        valuteCursesService.downloadValuteCurses(startDate, endDate);
    }
}
