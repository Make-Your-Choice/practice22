package com.yav.pract.controllers;

import com.yav.pract.dao.currencyDAO;
import com.yav.pract.models.currency;
import com.yav.pract.parsers.currencyParser;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/currency")
public class currencyController {
    private currencyDAO currencyDao;
    private currencyParser parser;
    private SimpleDateFormat frmt;
    private String dateFormat;
    @Autowired
    public currencyController(currencyDAO currencyDao, currencyParser parser) throws ParseException, IOException, ParserConfigurationException, SAXException {
        this.currencyDao = currencyDao;
        this.parser = parser;
        Date dateCurrent = new Date();
        frmt = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat = frmt.format(dateCurrent);
        dateCurrent = frmt.parse(dateFormat);
        if(this.currencyDao.showByDate(dateCurrent).isEmpty()) {
            parser.parseByDate(this.currencyDao, dateCurrent);
        }
    }
    /*@GetMapping("/hhh")
    public String index(Model model) {
        model.addAttribute("currency", currencyDao.index());
        return "currency/hhh";
    }*/
    @GetMapping("/all")
    public String showByDate(@RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy")
                             Date date, Model model) throws IOException, SAXException, ParserConfigurationException, ParseException {
        dateFormat = frmt.format(date);
        date = frmt.parse(dateFormat);
        String[] parts = dateFormat.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);
        boolean flagDay = false;
        boolean flagMonth = false;
        if(year % 4 == 0 && year % 100 != 0 || year % 4 == 0 && year % 100 == 0 && year % 400 == 0) {
            if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                if(day < 1 || day > 31) { flagDay = true; }
            } else if(month == 4 || month == 6 || month == 9 || month == 11) {
                if(day < 1 || day > 30) { flagDay = true; }
            } else if(month == 2) {
                if(day < 1 || day > 29) { flagDay = true; }
            } else { flagMonth = true; }
        } else {
            if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                if(day < 1 || day > 31) { flagDay = true; }
            } else if(month == 4 || month == 6 || month == 9 || month == 11) {
                if(day < 1 || day > 30) { flagDay = true; }
            } else if(month == 2) {
                if(day < 1 || day > 28) { flagDay = true; }
            } else { flagMonth = true; }
        }
        if(flagDay && flagMonth) {
            model.addAttribute("errMessage", "Date parameter is incorrect: invalid day and invalid month");
            return "currency/error";
        }
        if(flagDay) {
            model.addAttribute("errMessage", "Date parameter is incorrect: invalid day");
            return "currency/error";
        }
        if(flagMonth) {
            model.addAttribute("errMessage", "Date parameter is incorrect: invalid month");
            return "currency/error";
        }
        Date dateCurrent = new Date();
        dateFormat = frmt.format(dateCurrent);
        dateCurrent = frmt.parse(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 2);
        calendar.set(calendar.DAY_OF_MONTH, 1);
        Date dateMax = calendar.getTime();
        dateFormat = frmt.format(dateMax);
        dateMax = frmt.parse(dateFormat);
        if(date.after(dateCurrent)) {
            if(date.before(dateMax)) {
                date = dateCurrent;
            }
            else {
                model.addAttribute("errMessage", "No data found");
                return "currency/error";
            }
        }
        if(currencyDao.showByDate(date).isEmpty()) {
            parser.parseByDate(currencyDao, date);
            if(currencyDao.showByDate(date).isEmpty()) {
                model.addAttribute("errMessage", "No data found");
                return "currency/error";
            }
        }
        model.addAttribute("currency", currencyDao.showByDate(date));
        return "currency/all";
    }
    @GetMapping("/one")
    public String showByPeriod(@RequestParam("date1") @DateTimeFormat(pattern = "dd.MM.yyyy") Date date1,
                               @RequestParam("date2") @DateTimeFormat(pattern = "dd.MM.yyyy") Date date2,
                               @RequestParam("cbid") String cbid, Model model) throws ParseException, IOException, ParserConfigurationException, SAXException {
        dateFormat = frmt.format(date1);
        date1 = frmt.parse(dateFormat);
        dateFormat = frmt.format(date2);
        date2 = frmt.parse(dateFormat);
        Date dateCurrent = new Date();
        dateFormat = frmt.format(dateCurrent);
        dateCurrent = frmt.parse(dateFormat);
        if(date1.after(dateCurrent) && date2.after(dateCurrent) || date1.after(dateCurrent)) {
            model.addAttribute("errMessage", "No data found");
            return "currency/error";
        }
        if(date1.before(dateCurrent) && date2.after(dateCurrent)) {
            date2 = dateCurrent;
        }
        List<currency> currList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        currency curr;
        while(date1.compareTo(date2)<=0) {
            curr = currencyDao.showByDateCbId(date1, cbid);
            if(curr == null) {
                parser.parseByDate(currencyDao, date1);
                curr = currencyDao.showByDateCbId(date1, cbid);
                if(curr == null) {
                    model.addAttribute("errMessage", "No data found");
                    return "currency/error";
                }
            }
            currList.add(curr);
            calendar.setTime(date1);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            date1 = calendar.getTime();
        }
        model.addAttribute("currency", currList);
        return "currency/one";
    }
    @GetMapping("/single")
    public String showByDateOne(@RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") Date date,
                               @RequestParam("cbid") String cbid, Model model) throws ParseException, IOException, ParserConfigurationException, SAXException {
        dateFormat = frmt.format(date);
        date = frmt.parse(dateFormat);
        Date dateCurrent = new Date();
        dateFormat = frmt.format(dateCurrent);
        dateCurrent = frmt.parse(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 2);
        calendar.set(calendar.DAY_OF_MONTH, 1);
        Date dateMax = calendar.getTime();
        dateFormat = frmt.format(dateMax);
        dateMax = frmt.parse(dateFormat);
        if(date.after(dateCurrent)) {
            if(date.before(dateMax)) {
                date = dateCurrent;
            }
            else {
                model.addAttribute("errMessage", "No data found");
                return "currency/error";
            }
        }
        currency curr = currencyDao.showByDateCbId(date, cbid);
        if(curr == null) {
            parser.parseByDate(currencyDao, date);
            curr = currencyDao.showByDateCbId(date, cbid);
            if(curr == null) {
                model.addAttribute("errMessage", "No data found");
                return "currency/error";
            }
        }
        model.addAttribute("currency", curr);
        return "currency/single";
    }
    /*public String index() {
        return "currency/hhh";
    }*/
}
