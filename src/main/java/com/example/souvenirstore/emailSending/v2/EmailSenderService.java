package com.example.souvenirstore.emailSending.v2;

import com.example.souvenirstore.entity.Order;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;

@Service
@AllArgsConstructor
public class EmailSenderService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendMail(Order order) throws MessagingException {
        Context context = new Context();
        context.setVariable("order", order);
        context.setVariable("orderItems", order.getOrderItems());

        String process = templateEngine.process("orderReceipt", context);
        javax.mail.internet.MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setSubject("Order receipt " + order.getOrderNumber());
        helper.setText(process, true);
        helper.setTo(order.getUser().getEmailAddress());
        emailSender.send(mimeMessage);
    }

}
