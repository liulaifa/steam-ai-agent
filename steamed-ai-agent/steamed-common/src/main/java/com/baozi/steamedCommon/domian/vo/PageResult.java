package com.baozi.steamedCommon.domian.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Tag(name = "分页结果")
@Builder
public class PageResult<T> {
    @Schema(description = "总记录数")
    private Long total;
    @Schema(description = "当前页")
    private Integer page;
    @Schema(description = "每页记录数")
    private Integer pageSize;
    @Schema(description = "总页数")
    private Long pages;
    @Schema(description = "数据列表")
    private List<T> list;
}
