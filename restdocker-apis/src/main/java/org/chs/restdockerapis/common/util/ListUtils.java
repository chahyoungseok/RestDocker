package org.chs.restdockerapis.common.util;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListUtils {

    /** List가 null 또는 비어있는 경우 True */
    public boolean isBlank(List<?> list) {
        return null == list || list.isEmpty();
    }

    /** List가 존재 하고, List의 Size가 1이 아니라면 True */
    public boolean existAndNotSizeOne(List<?> list) {
        return false == isBlank(list) && 1 != list.size();
    }
}
