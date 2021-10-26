package com.example.souvenirstore.emailSending;

import com.example.souvenirstore.emailSending.v2.EmailSenderService;
import com.example.souvenirstore.entity.Order;
import com.example.souvenirstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.util.Optional;

import static com.example.souvenirstore.emailSending.MyConstants.FRIEND_EMAIL;

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
//
//        emailSenderService.sendMail();
        return "Email Sent!";






//        SimpleMailMessage message = new SimpleMailMessage();
//
//        message.setTo(MyConstants.FRIEND_EMAIL);
//        message.setSubject("Test Simple Email");
//        message.setText("Hello, Im testing Simple Email");
//
//        this.mailSender.send(message);
//
//        return "Email Sent!";
    }
}
