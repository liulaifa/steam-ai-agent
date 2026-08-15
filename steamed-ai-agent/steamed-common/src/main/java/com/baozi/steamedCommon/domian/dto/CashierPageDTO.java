package com.baozi.steamedCommon.domian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

@Data
@Tag(name = "收银员分页参数")
public class CashierPageDTO {
    @Schema(description = "模糊匹配的关键词")
    private String keyword;
    @Schema(description = "当前页")
    private Integer page = 1;
    @Schema(description = "每页数量")
    private Integer pageSize = 10;
}
