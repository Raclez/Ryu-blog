package com.example.demo.spider.processer;


import com.example.demo.commons.entity.BlogSpider;
import com.example.demo.commons.pojo.BlogElasticsearchModel;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;


@Component
public class LocalDataStorage {
    private String filePath="data.txt" ;

    private String basePath="/Users/ryu/Desktop/blogs/";

    public void saveDataToFile(List<BlogElasticsearchModel> data) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filePath,true));
            for (BlogElasticsearchModel blog : data) {

                StringBuffer stringBuffer = new StringBuffer("Title: " + blog.getTitle() + "\n");
                stringBuffer.append("content: " + blog.getContent() + "\n");
                writer.write(stringBuffer.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            // 适当的异常处理
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    // 异常处理
                }
            }
        }
        }

    public void generateMarkdownFiles(List<BlogElasticsearchModel> data) {
        for (BlogElasticsearchModel blog : data) {
            generateMarkdownFile(blog);
        }
    }
    private void generateMarkdownFile(BlogElasticsearchModel blog) {

        String filePath = basePath + blog.getTitle() + ".md"; // Assuming 'blogs' is a directory to store the files

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath,true))) {
            writer.write("# Title: " + blog.getTitle() + "\n\n");
            writer.write("## Link: " + blog.getLink() + "\n\n");
            writer.write("## Content:\n\n" + blog.getContent() + "\n\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception
        }
    }

}
