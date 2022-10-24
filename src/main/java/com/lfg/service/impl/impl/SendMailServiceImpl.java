package com.lfg.service.impl.impl;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.lfg.entity.MailRequest;
import com.lfg.service.impl.SendMailService;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class SendMailServiceImpl implements SendMailService {

    //注入邮件工具类
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${spring.mail.username}")
    private String sendMailer;

    private static final Logger logger = LoggerFactory.getLogger(SendMailServiceImpl.class);

    public void checkMail(MailRequest mailRequest) {
        Assert.notNull(mailRequest,"邮件请求不能为空");
        Assert.notNull(mailRequest.getSendTo(), "邮件收件人不能为空");
        Assert.notNull(mailRequest.getSubject(), "邮件主题不能为空");
        Assert.notNull(mailRequest.getText(), "邮件收件人不能为空");
    }

    @Override
    public void sendSimpleMail(MailRequest mailRequest ,HttpSession session) {
        SimpleMailMessage message = new SimpleMailMessage();
        checkMail(mailRequest);
        //邮件发件人
        message.setFrom(sendMailer);
        //邮件收件人 1或多个
        message.setTo(mailRequest.getSendTo().split(","));
        //邮件主题
        message.setSubject(mailRequest.getSubject());
        //邮件内容
        Random random = new Random();
        String i = String.valueOf((int)(Math.random()*9000)+1000);

        //验证码保存到session
//        session.setAttribute(mailRequest.getSendTo(),i);

        //验证码保存到redis
        redisTemplate.opsForValue().set(mailRequest.getSendTo(),i,5L,TimeUnit.MINUTES);

        message.setText(i);
        System.out.println(i);
        //邮件发送时间
        message.setSentDate(new Date());

        javaMailSender.send(message);
//        logger.info("发送邮件成功:{}->{}",sendMailer,mailRequest.getSendTo());
    }



    @Override
    public void sendHtmlMail(MailRequest mailRequest) {
        MimeMessage message = javaMailSender.createMimeMessage();
        checkMail(mailRequest);
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            //邮件发件人
            helper.setFrom(sendMailer);
            //邮件收件人 1或多个
            helper.setTo(mailRequest.getSendTo().split(","));
            //邮件主题
            helper.setSubject(mailRequest.getSubject());
            //邮件内容
            helper.setText(mailRequest.getText(),true);
            //邮件发送时间
            helper.setSentDate(new Date());

            String filePath = mailRequest.getFilePath();
            if (StringUtils.hasText(filePath)) {
                FileSystemResource file = new FileSystemResource(new File(filePath));
                String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
                helper.addAttachment(fileName,file);
            }
            javaMailSender.send(message);
//            logger.info("发送邮件成功:{}->{}",sendMailer,mailRequest.getSendTo());
        } catch (MessagingException e) {
//            logger.error("发送邮件时发生异常！",e);
        }
    }
}

