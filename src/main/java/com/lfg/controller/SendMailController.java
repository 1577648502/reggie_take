package com.lfg.controller;

import com.lfg.common.R;
import com.lfg.entity.MailRequest;
import com.lfg.service.impl.SendMailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * 获取邮箱验证码
 */
@Api(value = "/send-mail", tags = {"获取邮箱验证码"})
@RestController
@RequestMapping("/send-mail")
public class SendMailController {
    @Autowired
    private SendMailService sendMailService;
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "MailRequest", name = "mailRequest", value = "", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "HttpSession", name = "session", value = "", required = true)
    })
    @ApiOperation(value = "按邮箱发送验证码", notes = "按邮箱发送验证码", httpMethod = "POST")
    /**
     * 按邮箱发送验证码
     */
    @PostMapping("/simple")
    public R<String> SendSimpleMessage(@RequestBody MailRequest mailRequest, HttpSession session) {
        sendMailService.sendSimpleMail(mailRequest,session);
        return R.success("验证码获取成功");
    }

    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "MailRequest", name = "mailRequest", value = "", required = true)
    })
    @ApiOperation(value = "获取html格式", notes = "获取html格式", httpMethod = "POST")
    @PostMapping("/html")
    public void SendHtmlMessage(@RequestBody MailRequest mailRequest) { sendMailService.sendHtmlMail(mailRequest);}
}
