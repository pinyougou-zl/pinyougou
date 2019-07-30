package com.pinyougou.upload.controller;

import com.entity.Result;
import com.pinyougou.common.util.FastDFSClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadController {
    @RequestMapping("/uploadFile")
    //支持跨域，只有这个两个的跨域请求上传图片才可以被允许
    @CrossOrigin(origins = {"http://localhost:9102","http://localhost:9101"}
    ,allowCredentials = "true")
    public Result upload(MultipartFile file) {
        System.out.println(file);
        try {
            //加载配置文件
            FastDFSClient fastDFSClient= new FastDFSClient("classpath:config/fastdfs_client.conf");
            //获取源文件的字节数组
            byte[] bytes = file.getBytes();
            //获取扩展名
            String originalFilename = file.getOriginalFilename();
            String exName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            //调用api，上传图片
            String path = fastDFSClient.uploadFile(bytes, exName);
            String realpath = "http://192.168.25.133/"+path;
            return new Result(true,realpath);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }

}
