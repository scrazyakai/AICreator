package com.akai.aicreator.model.request;

import lombok.Data;

@Data
public class BanRequest {
    private Long userId;
    /**
     * 被封禁时间(s)
     */
    private Long time;
}
