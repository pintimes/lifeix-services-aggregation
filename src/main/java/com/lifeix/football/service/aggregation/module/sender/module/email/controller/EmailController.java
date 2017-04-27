package com.lifeix.football.service.aggregation.module.sender.module.email.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lifeix.football.service.aggregation.module.sender.model.Email;
import com.lifeix.football.service.aggregation.module.sender.module.email.service.EmailService;

@RestController
@RequestMapping(value = "/emails")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public void sendEmails(@RequestBody(required = true) List<Email> emails) {
	emailService.sendEmails(emails);
    }

}
