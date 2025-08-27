package com.akai.aicreator.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AppDeployRequest implements Serializable {

    /**
     * 应用 id
     */
    private Long appId;

    @Serial
    private static final long serialVersionUID = 1L;
}
