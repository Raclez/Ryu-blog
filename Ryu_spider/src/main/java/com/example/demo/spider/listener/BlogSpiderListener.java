package com.example.demo.spider.listener;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.SpiderListener;

public class BlogSpiderListener implements SpiderListener {
    @Override
    public void onSuccess(Request request) {

    }


    @Override
    public void onError(Request request, Exception e) {
        SpiderListener.super.onError(request, e);
    }
}
