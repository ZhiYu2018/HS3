package com.czx.h3outbound.ofs.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileSegment {
    private int num;
    private String name;
    private String sha;
}
