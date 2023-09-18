package com.valute.valute_app.controllers;

import com.valute.valute_app.services.ValuteCursesService;
import com.valute.valute_app.specsDTOs.ValuteCursesSpecDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/get_valute_curses_xml")
    public ResponseEntity<byte[]> getValuteCursesXml(@RequestBody ValuteCursesSpecDto valuteCursesSpecDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "Valute_curses_table.xlsx");
        return ResponseEntity.ok().headers(headers).
                body(valuteCursesService.getValuteCursesBySpecXml(valuteCursesSpecDto));
    }

    @GetMapping("/get_valute_curses")
    public List<Map<String, Object>> getValuteCurses(@RequestBody ValuteCursesSpecDto valuteCursesSpecDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "Valute_curses_table.xlsx");
        return valuteCursesService.getValuteCursesBySpec(valuteCursesSpecDto);
    }
}
