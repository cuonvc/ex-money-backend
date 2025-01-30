package com.exmoney.payload.mapper;

import com.exmoney.entity.TaskSchedulerConfig;
import com.exmoney.payload.response.scheduler.SchedulerResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface SchedulerMapper {

    SchedulerResponse toResponse(TaskSchedulerConfig entity);
}
