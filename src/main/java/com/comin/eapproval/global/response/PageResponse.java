package com.comin.eapproval.global.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 페이징 조회 결과를 공통 형식으로 제공하는 응답 객체.
 *
 * @param <T> 조회 데이터 타입
 */
@Getter
public class PageResponse<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    private PageResponse(
            List<T> content,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    /**
     * Spring Data의 Page 객체를 공통 페이지 응답으로 변환한다.
     *
     * @param page 조회된 페이지
     * @param <T> 조회 데이터 타입
     * @return 공통 페이지 응답
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
