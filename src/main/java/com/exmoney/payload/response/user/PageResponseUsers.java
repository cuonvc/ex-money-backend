package com.exmoney.payload.response.user;

import com.exmoney.payload.common.PageResponse;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
public class PageResponseUsers extends PageResponse<UserResponse> {

}
