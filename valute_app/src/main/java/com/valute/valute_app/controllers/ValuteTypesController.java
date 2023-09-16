package com.valute.valute_app.controllers;

import com.valute.valute_app.services.ValuteTypesServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/valute_types")
public class ValuteTypesController {
    private final ValuteTypesServices valuteTypesServices;

    @Autowired
    public ValuteTypesController(ValuteTypesServices valuteTypesServices) {
        this.valuteTypesServices = valuteTypesServices;
    }

    @GetMapping("/download_valute_types")
    public void downloadValuteTypes() {
        valuteTypesServices.downloadValuteTypes();
    }

    @GetMapping("/delete_all_valute_types")
    public void deleteAllValuteTypes() {
        valuteTypesServices.deleteAll();
    }

    @GetMapping("/get_valute_types_xml")
    public ResponseEntity<byte[]> getValuteTypesXml() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "Valute_types_table.xlsx");
        return ResponseEntity.ok().headers(headers).
                body(valuteTypesServices.getValuteTypesXml());
    }
}
