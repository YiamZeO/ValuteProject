package com.valute.valute_app.controllers;

import com.valute.valute_app.entities.ValuteType;
import com.valute.valute_app.services.ValuteTypesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/valute_types")
public class ValuteTypesController {
    private final ValuteTypesService valuteTypesService;

    @Autowired
    public ValuteTypesController(ValuteTypesService valuteTypesService) {
        this.valuteTypesService = valuteTypesService;
    }

    @GetMapping("/download_valute_types")
    public void downloadValuteTypes() {
        valuteTypesService.downloadValuteTypes();
    }

    @GetMapping("/delete_all_valute_types")
    public void deleteAllValuteTypes() {
        valuteTypesService.deleteAll();
    }

    @GetMapping("/get_valute_types")
    public List<ValuteType> getValuteTypes() {
        return valuteTypesService.getValuteTypes();
    }

    @GetMapping("/get_valute_types_xml")
    public ResponseEntity<byte[]> getValuteTypesXml() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "Valute_types_table.xlsx");
        return ResponseEntity.ok().headers(headers).
                body(valuteTypesService.getValuteTypesXml());
    }
}
