package com.akai.aicreator.model.request;

import lombok.Data;

@Data
public class PagesQueryRequest {
    private Integer cur = 1;
    private Integer size = 10;
}
