package com.lfg.controller;


import com.lfg.common.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件管理
 */
@Api(value = "/common", tags = {"文件管理"})
@RestController

@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String path;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "file", name = "file", value = "", required = true)
    })
    @ApiOperation(value = "文件上传", notes = "文件上传", httpMethod = "POST")
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));

        //uuid设置用户名，防止重名
        String s = UUID.randomUUID().toString() + substring;
        //创建一个目录对象
        File file1 = new File(path);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        try {
            file.transferTo(new File(path + s));
            return R.success(s);
        } catch (IOException e) {
            e.printStackTrace();
            return R.error("上传失败");
        }

    }

    /**
     * 文件下载
     *
     * @param response
     * @param name
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "HttpServletResponse", name = "response", value = "", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "name", value = "", required = true)
    })
    @ApiOperation(value = "文件下载", notes = "文件下载", httpMethod = "GET")
    @GetMapping("download")
    public void download(HttpServletResponse response, String name) {
        try {
            //输入流通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(path + name));
            //输出流 通过输出流将文件回写到浏览器，展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
