package org.example.clean4u._core.response;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Data
public class PageResponse<T> {
    List<T> content;
    private int number;
    private int size;
    private int totalPages;
    private Long totalElements;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;
    private Integer previousPageNumber;
    private Integer nextPageNumber;
    private List<PageLink> pageLinks;

    public <E> PageResponse(Page<E> page, Function<E, T> mapper) {
        this.content = page.getContent().stream()
                .map(mapper)
                .toList();
        this.number = page.getNumber();
        this.size = page.getSize();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.first  = page.isFirst();
        this.last = page.isLast();
        this.hasNext = page.hasNext();
        this.hasPrevious = page.hasPrevious();
        this.previousPageNumber = page.hasPrevious() ? page.getNumber() : null;
        this.nextPageNumber = page.hasNext() ? page.getNumber() + 2 : null;
        this.pageLinks = generatePageLinks(page);
    }

    private List<PageLink> generatePageLinks(Page<?> page) {
        List<PageLink> links = new ArrayList<>();

        int currentPage = page.getNumber() + 1;
        int totalPages = page.getTotalPages();
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, currentPage + 2);

        for(int i = startPage; i <= endPage; i++) {
            PageLink link = new PageLink();
            link.setDisplayNumber(i);
            link.setActive(i == currentPage);
            links.add(link);
        }
        return links;
    }

    @Data
    public static class PageLink {
        private int displayNumber;
        private boolean active;
    }
}
