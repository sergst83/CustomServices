package ru.bia.process.handler;

import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class PickUpCargoHandlerTest {

    @Test
    void test() throws ParseException {
        String dateString = "2019-09-20T12:21:10.905+0000";
        Date date = new StdDateFormat().parse(dateString);
    }
}