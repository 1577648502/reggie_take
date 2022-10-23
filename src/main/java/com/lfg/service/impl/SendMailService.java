package com.lfg.service.impl;

import com.lfg.entity.MailRequest;

import javax.servlet.http.HttpSession;

public interface SendMailService {

    /**
     * 简单文本邮件
     *
     * @param mailRequest
     * @return
     */
    void sendSimpleMail(MailRequest mailRequest, HttpSession session);


    /**
     * Html格式邮件,可带附件
     *
     * @param mailRequest
     * @return
     */
    void sendHtmlMail(MailRequest mailRequest);
}
