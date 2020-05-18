package com.atguigu.gmall.manage.util;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * GmallUploadUtil
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-06
 * @Description:
 */
public class GmallUploadUtil {
    public static String upLoadImg(MultipartFile multipartFile) {
        String path = GmallUploadUtil.class.getResource("/tracker.conf").getPath();
        String urlPath = "http://47.116.101.239";
        try {
            ClientGlobal.init(path);
            // 链接tracker
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getTrackerServer();
            // 获得storage
            StorageClient storageClient = new StorageClient(trackerServer, null);
            String originalFilename = multipartFile.getOriginalFilename();
            int lastIndex = originalFilename.lastIndexOf(".");
            // 上传文件
            String[] urls = storageClient.upload_file(multipartFile.getBytes(), originalFilename.substring(lastIndex+1), null);
            // 解析返回的图片的路径url信息
            for (String url : urls) {
                urlPath = urlPath + "/" + url;
                System.out.println(url);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return urlPath;
    }

    public static void main(String[] args) {
        System.out.println("aaabbb".substring(2));
    }
}

