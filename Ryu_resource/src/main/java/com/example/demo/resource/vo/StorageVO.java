package com.example.demo.resource.vo;

import com.example.demo.base.vo.BaseVO;
import lombok.Data;

/**
 * CommentVO
 *
 * @author Ryu
 * @create: 2021年1月11日16:15:52
 */
@Data
public class StorageVO extends BaseVO<StorageVO> {

    /**
     * 管理员UID
     */
    private String adminUid;

    /**
     * 存储大小
     */
    private long storagesize;
}
