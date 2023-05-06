package com.example.demo.base.vo;

import com.example.demo.base.validator.annotion.IdValid;
import com.example.demo.base.validator.group.Delete;
import com.example.demo.base.validator.group.Update;
import lombok.Data;

/**
 * BaseVO   view object 表现层 基类对象
 *
 * @author Ryu
 * @create:
 */
@Data
public class BaseVO<T> extends PageInfo<T> {

    /**
     * 唯一UID
     */
    @IdValid(groups = {Update.class, Delete.class})
    private String uid;

    private Integer status;
}
