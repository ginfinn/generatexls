package com.service.generatexls.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.service.generatexls.dto.Event;
import com.service.generatexls.dto.User;
import lombok.val;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class RestTeammateService {
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
        HashMap<User, HashMap<Date, String>> data = new HashMap<>();
        Set<Date> treeSet = new TreeSet<Date>();
        for (val event : response.getBody()
        ) {
            for (val shift : event.getShifts()
            ) {
                treeSet.add(shift.getBeginTime());
            }

        }

        for (val event : response.getBody()
        ) {
            for (val shift : event.getShifts()
            ) {
                for (val place : shift.getPlaces()
                ) {
                    for (val participants : place.getParticipants()
                    ) {
                        data.putIfAbsent(participants.getUser(), new HashMap<Date, String>());
                        val dateAndShift = data.get(participants.getUser());
                        dateAndShift.put(shift.getBeginTime(), participants.getEventRole().getTitle());


                    }

                }

            }

        }
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet1 = workbook.createSheet("Сводка");
        XSSFFont font = workbook.createFont();
        XSSFCellStyle style = workbook.createCellStyle();
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
