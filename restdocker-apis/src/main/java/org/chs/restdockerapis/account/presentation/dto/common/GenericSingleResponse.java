package org.chs.restdockerapis.account.presentation.dto.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GenericSingleResponse<T>{
    T data;
}
