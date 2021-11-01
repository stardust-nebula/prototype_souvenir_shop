package com.example.souvenirstore.emailSending;

import com.example.souvenirstore.entity.Order;
import com.example.souvenirstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;

import java.util.Optional;

@Controller
public class SimpleEmailController {
    @Autowired
    public JavaMailSender mailSender;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private OrderService orderService;

    @ResponseBody
    @RequestMapping("/sendSimpleEmail")
    public String sendSimpleEmail()  throws MessagingException {

        Optional<Order> order = orderService.getOrderById(1);
        emailSenderService.sendMail(order.get());
        return "Email Sent!";
    }
}
