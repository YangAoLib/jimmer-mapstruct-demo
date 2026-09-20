package com.example.jimmer;

import com.example.jimmer.dto.BookDto;
import com.example.jimmer.entity.Book;
import com.example.jimmer.entity.BookDraft;
import com.example.jimmer.mapper.BookMapper;

import java.math.BigDecimal;

public class DemoApplication {

    public static void main(String[] args) {
        Book entity = BookDraft.$.produce(draft -> draft
                .setId(1L)
                .setName("Getting Started with Jimmer")
                .setPrice(new BigDecimal("59.90")));

        BookDto dto = BookMapper.INSTANCE.toDto(entity);
        System.out.printf("Entity to plain class: id=%d, title=%s, price=%s%n",
                dto.getId(), dto.getTitle(), dto.getPrice());

        dto.setTitle("Getting Started with MapStruct");
        Book converted = BookMapper.INSTANCE.toEntity(dto);
        System.out.printf("Plain class to Entity: id=%d, name=%s, price=%s%n",
                converted.id(), converted.name(), converted.price());
        System.out.printf("Original immutable Entity remains unchanged: name=%s%n", entity.name());
    }
}
