package com.exmoney.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_scheduler_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TaskSchedulerConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ref_id")
    private Long refId;

    @Column(name = "ref_table")
    private String refTable;

    @Column(name = "task_name")
    private String taskName; //SchedulerTaskName static class

    @Column(name = "time_interval") //MONTHLY, WEEKLY, DAILY, PER_HOUR, PER_MONTH
    private String timeInterval; //ex: DAILY

    @Column(name = "time_value") //based on timeInterval
    private Integer timeValue; //ex: 6 (meaning: 06:00am per day)

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private Long updatedBy;
}
