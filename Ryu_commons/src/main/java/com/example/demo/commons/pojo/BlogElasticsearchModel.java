package com.example.demo.commons.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;
import java.util.List;

/**
 * ESBlogIndex
 */
@Document(indexName = "blogspider", type = "docs", shards = 1, replicas = 0)
@Data
public class BlogElasticsearchModel {
    @Id
    private String id;

    private String title;

    private String summary;

    private String content;

    private String isPublish;

    private String link;


}
