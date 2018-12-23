package com.pinyougou.manage.controller;

import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

/**
 * @author zp
 */
@RequestMapping("upload")
@RestController
public class UploadController {

    @PostMapping
    public Result upload(MultipartFile file){

        try {
            //获取文件拓展名
            String fileExtName = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf(".") + 1);
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastdfs/tracker.conf");

            String url = fastDFSClient.uploadFile(file.getBytes(),fileExtName);
            return Result.success(url);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.error("上传图片失败");
    }
}
