package com.example.demo.spider.processer;

import com.example.demo.spider.entity.BlogSpider;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
public class LocalDataStorage {

    public void saveDataToFile(Set<BlogSpider> data) {
        try (FileWriter writer = new FileWriter("data.txt",true)) {
            for (BlogSpider blog : data) {
                // 将数据格式化为字符串并写入文件
                String dataString = "Title: " + blog.getTitle() + "\n";
                writer.write(dataString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
