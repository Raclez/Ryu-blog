package com.example.demo.spider.processer;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.SpiderListener;

public class CustomSpiderListener implements SpiderListener {
    int maxBlogs; // 停止条件：最大博客数
    private int blogCount; // 已爬取的博客数

    public CustomSpiderListener(int maxBlogs) {
        this.maxBlogs = maxBlogs;
        this.blogCount = 0;
    }
    @Override
    public void onSuccess(Request request) {
        blogCount++;
        if (blogCount >= maxBlogs) {

        }
    }

    @Override
    public void onError(Request request) {
        SpiderListener.super.onError(request);
    }

    @Override
    public void onError(Request request, Exception e) {
        SpiderListener.super.onError(request, e);
    }
}
