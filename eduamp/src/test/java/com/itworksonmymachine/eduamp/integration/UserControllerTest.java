package com.itworksonmymachine.eduamp.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(
    value = {"/users"},
    produces = MediaType.APPLICATION_JSON_VALUE
)
@Validated
public class UserControllerTest {

}
