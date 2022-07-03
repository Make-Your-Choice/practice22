package com.yav.pract.dao;

import com.yav.pract.models.currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class currencyDAO {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public currencyDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    /*public List<currency> index() {
        return jdbcTemplate.query("SELECT * FROM currency", new BeanPropertyRowMapper<>(currency.class));
    }
    public List<currency> showByDate(Date date) throws ParseException {
        SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormat = frmt.format(date);
        date = frmt.parse(dateFormat);
        return jdbcTemplate.query("SELECT * FROM currency WHERE dateRec=?", new Object[]{date},
                new BeanPropertyRowMapper<>(currency.class));
    }
    public currency showByDateCbId(Date date, String cbId) throws ParseException {
        SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormat = frmt.format(date);
        date = frmt.parse(dateFormat);
        return jdbcTemplate.query("SELECT * FROM currency WHERE dateRec=? AND cbid=?", new Object[]{date, cbId},
                new BeanPropertyRowMapper<>(currency.class)).stream().findAny().orElse(null);
    }
    public void save(currency curr, Date date) throws ParseException {
        SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormat = frmt.format(date);
        date = frmt.parse(dateFormat);
        jdbcTemplate.update("INSERT INTO currency (cbid, name, numcode, charcode, daterec, value)" +
                        "VALUES(?, ?, ?, ?, ?, ?)",
                curr.getCbId(), curr.getName(), curr.getNumCode(),
                curr.getCharCode(), date, curr.getValue());
    }*/
    /*public void updateCurrencyVal(currency curr) {
        int pKey = jdbcTemplate.queryForObject("SELECT ida FROM archive WHERE cbid=? OR cbidp=?",
                new Object[]{curr.getCbId(), curr.getCbId()}, int.class);
        jdbcTemplate.update("INSERT INTO currencyval (ida, value, daterec)" +
                        "VALUES(?, ?, ?)",
                pKey, curr.getValue(), curr.getDateRec());
    }*/
    public void updateArchive(currency curr) {
        jdbcTemplate.update("INSERT INTO archive (cbid, cbidp, name, numcode, charcode, nominal)" +
                        "VALUES(?, ?, ?, ?, ?, ?)",
                curr.getCbId(), curr.getCbIdP(), curr.getName(), curr.getNumCode(),
                curr.getCharCode(), curr.getNominal());
    }
    public void updateCurrencyVal(currency curr) {
        int pKey = jdbcTemplate.queryForObject("SELECT ida FROM archive WHERE cbid=?",
                new Object[]{curr.getCbId()}, int.class);
        jdbcTemplate.update("INSERT INTO currencyval (ida, value, daterec)" +
                        "VALUES(?, ?, ?)",
                pKey, curr.getValue(), curr.getDateRec());
    }
    public currency searchByDateCbIdCurrencyVal(Date date, String cbId) throws ParseException {
        SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormat = frmt.format(date);
        date = frmt.parse(dateFormat);
        int pKey = jdbcTemplate.queryForObject("SELECT ida FROM archive WHERE cbid=?", new Object[]{cbId}, int.class);
        return jdbcTemplate.query("SELECT * FROM currencyval WHERE daterec=? AND ida=?", new Object[]{date, pKey},
                new BeanPropertyRowMapper<>(currency.class)).stream().findAny().orElse(null);
    }
    public currency searchByCbIdArchive(String cbId) {
        return jdbcTemplate.query("SELECT * FROM archive WHERE cbid=?", new Object[]{cbId},
                new BeanPropertyRowMapper<>(currency.class)).stream().findAny().orElse(null);
    }
    /*public currency searchByDateCbIdPCurrencyVal(Date date, String cbIdP) throws ParseException {
        SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormat = frmt.format(date);
        date = frmt.parse(dateFormat);
        int pKey = jdbcTemplate.queryForObject("SELECT ida FROM archive WHERE cbidp=?", new Object[]{cbIdP}, int.class);
        return jdbcTemplate.query("SELECT * FROM currencyval WHERE daterec=? AND ida=?", new Object[]{date, pKey},
                new BeanPropertyRowMapper<>(currency.class)).stream().findAny().orElse(null);
    }*/
    public List<currency> showAllByDate(Date date) throws ParseException {
        SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormat = frmt.format(date);
        date = frmt.parse(dateFormat);
        return jdbcTemplate.query("SELECT a.*, c.value, c.daterec FROM archive a" +
                        " JOIN currencyval c ON a.ida=c.ida AND c.daterec=?", new Object[]{date},
                new BeanPropertyRowMapper<>(currency.class));
    }
    public List<currency> showOneByDateCbId(Date date1, Date date2, String cbId) throws ParseException {
        SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormat1 = frmt.format(date1);
        date1 = frmt.parse(dateFormat1);
        String dateFormat2 = frmt.format(date2);
        date2 = frmt.parse(dateFormat2);
        return jdbcTemplate.query("SELECT a.*, c.value, c.daterec FROM archive a" +
                        " JOIN currencyval c ON a.ida=c.ida AND a.cbid=? AND c.daterec BETWEEN ? AND ? ORDER BY c.daterec",
                new Object[]{cbId, date1, date2}, new BeanPropertyRowMapper<>(currency.class));
    }
    public currency showSingleByDateCbId(Date date, String cbId) throws ParseException {
        SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormat = frmt.format(date);
        date = frmt.parse(dateFormat);
        return jdbcTemplate.query("SELECT a.*, c.value, c.daterec FROM archive a" +
                        " JOIN currencyval c ON a.ida=c.ida AND a.cbid=? AND c.daterec=?", new Object[]{cbId, date},
                new BeanPropertyRowMapper<>(currency.class)).stream().findAny().orElse(null);
    }
    public int checkArchive() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM archive", new Object[]{}, int.class);
    }
    /*public List<currency> showByPeriod(Date date1, Date date2, String cbId) throws ParseException {
        //date.replaceAll("/", ".");
        SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormat = frmt.format(date1);
        date1 = frmt.parse(dateFormat);
        dateFormat = frmt.format(date2);
        date2 = frmt.parse(dateFormat);
        return jdbcTemplate.query("SELECT * FROM currency WHERE dateRec BETWEEN ? AND ? AND cbid=?",
                new BeanPropertyRowMapper<>(currency.class), date1, date2, cbId);
    }*/
}
