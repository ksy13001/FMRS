package com.ksy.fmrs.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.format.SignStyle;

public class FlexibleLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText().trim();
        try{
            // fmplayer : 2000-07-21T00:00:00 형식
            return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalDate();
        } catch (DateTimeParseException e) {
            // 2000-8-14 같이 0 안붙이고 1글자로 입력 올때 처리
            try {
                // year는 4자리 고정, month/day는 1~2자리 허용
                return new DateTimeFormatterBuilder()
                        .appendValue(ChronoField.YEAR, 4)
                        .appendLiteral('-')
                        .appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, SignStyle.NOT_NEGATIVE)
                        .appendLiteral('-')
                        .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE)
                        .toFormatter()
                        .parse(text, LocalDate::from);
            } catch (DateTimeParseException e1) {
                throw ctxt.weirdStringException(text, LocalDate.class, "날짜 형식이 잘못되었습니다");
            }
        }
    }
}
