package com.exmoney.worker;

import com.exmoney.entity.TaskSchedulerConfig;
import com.exmoney.repository.TaskSchedulerConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.exmoney.util.Constant.ScheduleTimeIntervalType.*;
import static com.exmoney.util.Constant.Status.ACTIVE;
import static com.exmoney.util.Utils.getNow;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpenseSchedulerResetWorker {

    private final TaskSchedulerConfigRepository taskSchedulerConfigRepository;

    @Scheduled(cron = "0 0 0 * * *")
    //reset task status về ACTIVE vào mỗi ngày, mỗi tuần hoặc mỗi tháng
    public void run() {

        LocalDateTime now = getNow();

        List<TaskSchedulerConfig> tasks = taskSchedulerConfigRepository.findAllByExecuted();
        tasks.forEach(task -> {

            String intervalType = task.getTimeInterval();
            int timePoint = task.getTimeValue();

            switch (intervalType) {
                case DAILY -> {
                    log.info("\n==================> Trigger Expense scheduler reset DAILY");
                    resetToActive(task, now);
                }
                case WEEKLY -> {
                    if (now.getDayOfWeek().getValue() == timePoint) {
                        log.info("\n==================> Trigger Expense scheduler reset WEEKLY");
                        resetToActive(task, now);
                    }
                }
                case MONTHLY -> {
                    if (now.getDayOfMonth() == timePoint) {
                        log.info("\n==================> Trigger Expense scheduler reset MONTHLY");
                        resetToActive(task, now);
                    }
                }
            }
        });

        taskSchedulerConfigRepository.saveAll(tasks);
    }

    private void resetToActive(TaskSchedulerConfig config, LocalDateTime now) {
        config.setStatus(ACTIVE);
        config.setUpdatedAt(now);
        config.setUpdatedBy(0L);
    }
}
