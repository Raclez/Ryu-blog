package com.example.demo.commons.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ESMessage implements Serializable {
    private String operation; // 操作类型字段，可以是 "add" 或 "delete"
    private List<BlogElasticsearchModel> data; // 包含要添加或删除的数据

}
