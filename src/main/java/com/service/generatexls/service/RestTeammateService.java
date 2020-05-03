package com.service.generatexls.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.service.generatexls.dto.Event;
import com.service.generatexls.dto.Participant;
import com.service.generatexls.dto.Shift;
import com.service.generatexls.dto.User;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

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
        HashMap<User, HashMap<Date, List<Shift>>> data = new HashMap<>();


        for (val event : response.getBody()
        ) {
            for (val shift : event.getShifts()
            ) {
                for (val place : shift.getPlaces()
                ) {
                    for (val participants : place.getParticipants()
                    ) {

                        val dateAndShift = data.putIfAbsent(participants.getUser(), new HashMap<Date, List<Shift>>());
                        dateAndShift.put(shift.getBeginTime(), event.getShifts());
                    }

                }

            }

        }


        return response.getBody();
    }
}
