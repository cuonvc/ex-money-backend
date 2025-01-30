package com.exmoney.payload.response.scheduler;

import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SchedulerResponse {

    private Long id;

    private Long refId;

    private String refTable;

    private String taskName;

    private String timeInterval;

    private Integer timeValue;

    private String status;

    private LocalDateTime createdAt;

    private Long createdBy;

    private LocalDateTime updatedAt;

    private Long updatedBy;

    private Object data;
}
