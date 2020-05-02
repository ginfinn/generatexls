package com.service.generatexls.controllers;

import com.aspose.cells.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.service.generatexls.dto.Event;
import com.service.generatexls.service.RestTeammateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MainController {
    @Autowired
    RestTeammateService restTeammateService;
    @GetMapping("getJson")
    public List<Event> getJosn() throws JsonProcessingException {
// Instantiating a Workbook object
        Workbook workbook = new Workbook();
        Worksheet worksheet = workbook.getWorksheets().get(0);
// Set Styles
        CellsFactory factory = new CellsFactory();
        Style style = factory.createStyle();
        style.setHorizontalAlignment(TextAlignmentType.CENTER);
        style.getFont().setColor(Color.getBlueViolet());
        style.getFont().setBold(true);

        // Set JsonLayoutOptions
        JsonLayoutOptions options = new JsonLayoutOptions();
        options.setTitleStyle(style);
        options.setArrayAsTable(true);
        return restTeammateService.getJson();
    }


}
