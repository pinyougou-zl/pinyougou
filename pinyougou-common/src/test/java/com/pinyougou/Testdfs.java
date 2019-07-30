package com.pinyougou;

import com.pinyougou.common.util.FastDFSClient;
import org.junit.Test;

public class Testdfs {

    @Test
    public void uploadfile() throws Exception {
        FastDFSClient fastDFSClient = new FastDFSClient("C:\\Users\\江哥\\IdeaProjects\\mypinyougou\\pinyougou-shop-web\\src\\main\\resources\\fdfs_client.conf");
        String jpg = fastDFSClient.uploadFile("C:\\Users\\江哥\\Desktop\\IO.jpg", "jpg");
        System.out.println(jpg);
    }
}
