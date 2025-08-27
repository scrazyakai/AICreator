package com.akai.aicreator.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 4886004727262210680L;
    /**
     * 分页大小
     */
    Integer pageSize = 10;
    /**
     * 当前第几页
     */
    Integer pageNum = 1;
}
