package com.example.demo.search.repository;
;
import com.example.demo.commons.pojo.ESBlogIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * BlogRepository操作类
 * 在ElasticsearchRepository中我们可以使用Not Add Like Or Between等关键词自动创建查询语句
 *
 * @author
 * @date 2021年1月18日19:09:20
 */
public interface BlogRepository extends ElasticsearchRepository<ESBlogIndex, String> {
}
