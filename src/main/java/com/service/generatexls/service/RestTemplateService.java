package com.service.generatexls.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.service.generatexls.dto.Event;
import com.service.generatexls.dto.EventType;
import com.service.generatexls.dto.User;
import lombok.val;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class RestTemplateService {
    //  static String q = "?end=2020-05-04T00:00:00Z&begin=2020-01-01T00:00:00Z";


    @Autowired
    private RestTemplate restTemplate;

    public List<Event> getJson() throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "tmWdXwVzGNy94jwMUXYtApadJUFChYuknEUxrkzsyqUBpfKksDNTpRbh7u22EEJx7pE4t4ThjKf");

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        ResponseEntity<List<Event>> response = restTemplate.exchange(
                "https://dev.rtuitlab.ru/api/event/docsGen?end=2020-05-04T00:00:00Z&begin=2020-01-01T00:00:00Z",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Event>>() {
                });
        HashMap<String, HashMap<Date, String>> data = new HashMap<>();
        for (val event : response.getBody()
        ) {
            for (val shift : event.getShifts()
            ) {
                for (val place : shift.getPlaces()
                ) {
                    for (val participants : place.getParticipants()
                    ) {
                        data.putIfAbsent(participants.getUser().getFullName(), new HashMap<Date, String>());
                        val dateAndShift = data.get(participants.getUser().getFullName());
                        dateAndShift.put(shift.getBeginTime(), participants.getEventRole().getTitle());


                    }

                }

            }

        }


        Set<String> setUser = new HashSet<>();
        for (val event : response.getBody()
        ) {
            for (val shift : event.getShifts()
            ) {
                for (val place : shift.getPlaces()
                ) {
                    for (val participants : place.getParticipants()
                    ) {
                        setUser.add(participants.getUser().getFullName());


                    }

                }

            }
        }


        Set<String> setEventType = new TreeSet<String>();
        for (val event : response.getBody()
        ) {
            setEventType.add(event.getEventType().getTitle());
        }

        Set<Date> setDate = new TreeSet<Date>();


        for (val event : response.getBody()
        ) {
            for (val shift : event.getShifts()
            ) {
                setDate.add(shift.getBeginTime());
            }

        }

        Iterator<String> itrUser = setUser.iterator();
        Iterator<Date> itrDate = setDate.iterator();


        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet1 = workbook.createSheet("Сводка");
        XSSFFont font = workbook.createFont();
        XSSFCellStyle style = workbook.createCellStyle();







        int rowNumName = 0;
        int colNumName = 0;
        int colNumDate = 2;
        int rowMumEventRole = 0;
        int colNumEventRole = 2;





        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd");

        font.setFontName("Arial");
        style.setFont(font);
        XSSFRow rowFullName = sheet1.createRow(rowNumName++);

        XSSFCell cellFullName = rowFullName.createCell(colNumName++);
        cellFullName.setCellValue("имя");
        cellFullName.setCellStyle(style);
        sheet1.autoSizeColumn(0);

        cellFullName = rowFullName.createCell(colNumName);
        cellFullName.setCellStyle(style);
        cellFullName.setCellValue("фамилия");
        sheet1.autoSizeColumn(1);
        colNumName = 0;
        while (itrUser.hasNext()) {
            String fullName = itrUser.next();
            String[] splited = fullName.split(" ");
            String surname = splited[0];
            String name = splited[1];
            rowFullName = sheet1.createRow(rowNumName++);
            cellFullName = rowFullName.createCell(colNumName++);
            cellFullName.setCellValue(surname);
            cellFullName.setCellStyle(style);
            sheet1.autoSizeColumn(0);

            cellFullName = rowFullName.createCell(colNumName);
            cellFullName.setCellStyle(style);
            cellFullName.setCellValue(name);
            sheet1.autoSizeColumn(1);
            colNumName = 0;
            HashMap<Date, String> dateAndEventRole = new HashMap<Date, String>();
            dateAndEventRole = data.get(fullName);
            while (itrDate.hasNext()) {
                Date date  = itrDate.next();
                XSSFRow rowDate = sheet1.getRow(0);
                val formatDate = dateFormat.format(date);
                XSSFCell cellDate = rowDate.createCell(colNumDate++);
                cellDate.setCellValue(formatDate);
                cellDate.setCellStyle(style);
                sheet1.autoSizeColumn(0);

                val eventRole = dateAndEventRole.get(date);
                XSSFRow rowEventRole = sheet1.getRow(rowNumName);
                XSSFCell cellEventRole = rowEventRole.createCell(colNumEventRole);

                cellEventRole.setCellValue(eventRole);

            }


        }



      /*  while (itrDate.hasNext()) {
            date = itrDate.next();
            row = sheet1.getRow(0);
            val a = dateFormat.format(date);
            cell = row.createCell(i++);
            cell.setCellValue(a);
            cell.setCellStyle(style);
            sheet1.autoSizeColumn(0);


        }*/


        try (FileOutputStream outputStream = new FileOutputStream("сводка.xlsx")) {
            workbook.write(outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*HashMap<EventType, List<Date>> data2 = new HashMap<>();
        for (val event : response.getBody()
        ) {
            for (val shift : event.getShifts()
            ) {

                data2.putIfAbsent(event.getEventType(), new List<Date>() );
                val a = data2.get(event.getEventType());



            }
        }
*/


        return response.getBody();

    }
}
