package com.example.demo.search.repository;
;
import com.example.demo.commons.pojo.BlogElasticsearchModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * BlogRepository操作类
 * 在ElasticsearchRepository中我们可以使用Not Add Like Or Between等关键词自动创建查询语句
 *
 * @author
 * @date 2021年1月18日19:09:20
 */
public interface BlogRepository extends ElasticsearchRepository<BlogElasticsearchModel, String> {
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title^0.75\", \"summary^0.75\", \"content^0.1\"]}}")
    Page<BlogElasticsearchModel> findByKey(String key, Pageable pageable);
}
