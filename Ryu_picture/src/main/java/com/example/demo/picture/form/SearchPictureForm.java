package com.example.demo.picture.form;

import com.example.demo.base.vo.FileVO;
import lombok.Data;

@Data
public class SearchPictureForm extends FileVO {
    private String searchKey;
    private Integer count;
}
