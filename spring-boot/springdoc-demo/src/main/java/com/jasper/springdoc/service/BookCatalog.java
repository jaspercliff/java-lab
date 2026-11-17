package com.jasper.springdoc.service;

import com.jasper.springdoc.enums.BookStatus;
import com.jasper.springdoc.model.BookCreateRequest;
import com.jasper.springdoc.model.BookResponse;
import com.jasper.springdoc.model.PageResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 纯内存假数据，只为让 demo 的每个接口都能真实调用。
 * 本模块不涉及任何建表 / 种子脚本，因此无需 Flyway。
 */
@Component
public class BookCatalog {

    private final AtomicLong sequence = new AtomicLong(1001);
    private final Map<Long, BookResponse> store = new ConcurrentHashMap<>();

    public BookResponse sample(long id) {
        return store.computeIfAbsent(id, key -> {
            BookResponse book = new BookResponse();
            book.setId(key);
            book.setTitle("Springdoc in Action");
            book.setStatus(BookStatus.PUBLISHED);
            book.setPrice(new BigDecimal("88.50"));
            book.setCreatedAt(OffsetDateTime.now());
            book.setCoverUrl("https://cdn.example.com/covers/" + key + ".png");
            book.setTags(List.of("java", "openapi"));
            book.setInternalNote("仅 Admin 视图可见");
            return book;
        });
    }

    public PageResult<BookResponse> page(int page, int size) {
        List<BookResponse> all = List.of(sample(1001), sample(1002), sample(1003));
        return new PageResult<>(all, all.size(), page, size);
    }

    public BookResponse create(BookCreateRequest request) {
        BookResponse book = sample(sequence.getAndIncrement());
        book.setTitle(request.title());
        book.setPrice(request.price());
        if (request.status() != null) {
            book.setStatus(request.status());
        }
        book.setTags(request.tags() == null ? List.of() : request.tags());
        return book;
    }

    public void delete(long id) {
        store.remove(id);
    }
}
