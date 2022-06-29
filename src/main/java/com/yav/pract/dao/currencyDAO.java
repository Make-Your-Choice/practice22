package com.yav.pract.dao;

import com.yav.pract.models.currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class currencyDAO {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public currencyDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public List<currency> index() {
        return jdbcTemplate.query("SELECT * FROM currency", new BeanPropertyRowMapper<>(currency.class));
    }
    public List<currency> showByDate(Date date) throws ParseException {
        SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormat = frmt.format(date);
        date = frmt.parse(dateFormat);
        return jdbcTemplate.query("SELECT * FROM currency WHERE dateRec=?", new Object[]{date},
                new BeanPropertyRowMapper<>(currency.class));
    }
    public void save(currency curr, Date date) throws ParseException {
        SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormat = frmt.format(date);
        date = frmt.parse(dateFormat);
        jdbcTemplate.update("INSERT INTO currency (cbid, name, numcode, charcode, daterec, value)" +
                        "VALUES(?, ?, ?, ?, ?, ?)",
                curr.getCbId(), curr.getName(), curr.getNumCode(),
                curr.getCharCode(), date, curr.getValue());
    }
    public List<currency> showByPeriod(Date date1, Date date2, String cbId) throws ParseException {
        //date.replaceAll("/", ".");
        SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormat = frmt.format(date1);
        date1 = frmt.parse(dateFormat);
        dateFormat = frmt.format(date2);
        date2 = frmt.parse(dateFormat);
        return jdbcTemplate.query("SELECT * FROM currency WHERE dateRec BETWEEN ? AND ? AND cbid=?",
                new BeanPropertyRowMapper<>(currency.class), date1, date2, cbId);
    }
}
