package com.atguigu.gmall.manage;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class GmallManageWebApplicationTests {


    //删除
    public void delete() throws IOException, MyException {
        String path = GmallManageWebApplicationTests.class.getResource("/tracker.conf").getPath();
        ClientGlobal.init(path);
        // 链接tracker
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getTrackerServer();

        // 获得storage
        StorageClient storageClient = new StorageClient(trackerServer,null);
        storageClient.delete_file("group1","M00/00/00/rBE8715iAYCAZmlJAAYYbdgZ0ug065.png");
    }

    //新增
    public void contextLoads() throws Exception {

        String path = GmallManageWebApplicationTests.class.getResource("/tracker.conf").getPath();
        ClientGlobal.init(path);
        // 链接tracker
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getTrackerServer();

        // 获得storage
        StorageClient storageClient = new StorageClient(trackerServer,null);

        // 上传文件
        String[] urls = storageClient.upload_file("D:\\2.png", "png", null);
        // 解析返回的图片的路径url信息
        String urlPath = "http://47.116.101.239";
        for (String url : urls) {
            urlPath = urlPath +"/"+url;
            System.out.println(url);
        }
        System.out.println(urlPath+"========================================");

    }

}
