package com.valute.valutedownloadservice.controllers;

import com.valute.valutedownloadservice.services.ValuteTypesService;
import com.valute.xmldtos.DTOs.ValuteTypeXml;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<ValuteTypeXml> downloadValuteTypes() {
        return valuteTypesService.downloadValuteTypes();
    }
}
