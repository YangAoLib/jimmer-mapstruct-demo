package com.example.jimmer.mapper;

import com.example.jimmer.dto.BookDto;
import com.example.jimmer.entity.Book;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct generates the implementation; no handwritten field copying or entity implementations are needed.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BookMapper {

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mapping(source = "name", target = "title")
    BookDto toDto(Book entity);

    @InheritInverseConfiguration(name = "toDto")
    Book toEntity(BookDto dto);

    /**
     * Explicitly map all fields without relying on inverse mapping configuration.
     * Use a qualifier name to avoid ambiguity with toEntity when mapping collection elements.
     */
    @Named("toEntityExplicit")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "name")
    @Mapping(source = "price", target = "price")
    Book toEntityExplicit(BookDto dto);

    List<BookDto> toDtoList(List<Book> entities);

    List<Book> toEntityList(List<BookDto> dtos);
}
