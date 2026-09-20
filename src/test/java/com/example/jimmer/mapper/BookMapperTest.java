package com.example.jimmer.mapper;

import com.example.jimmer.dto.BookDto;
import com.example.jimmer.entity.Book;
import com.example.jimmer.entity.BookDraft;
import org.babyfish.jimmer.UnloadedException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookMapperTest {

    private final BookMapper mapper = BookMapper.INSTANCE;

    @Test
    void shouldConvertEntityToPlainClass() {
        Book entity = sampleEntity();

        BookDto dto = mapper.toDto(entity);

        assertEquals(entity.id(), dto.getId());
        assertEquals(entity.name(), dto.getTitle());
        assertEquals(entity.price(), dto.getPrice());
    }

    @Test
    void shouldConvertPlainClassToEntity() {
        BookDto dto = sampleDto();

        Book entity = mapper.toEntity(dto);

        assertEquals(dto.getId(), entity.id());
        assertEquals(dto.getTitle(), entity.name());
        assertEquals(dto.getPrice(), entity.price());
    }

    @Test
    void shouldConvertPlainClassToEntityWithExplicitMappings() {
        BookDto dto = sampleDto();

        Book entity = mapper.toEntityExplicit(dto);

        assertEquals(dto.getId(), entity.id());
        assertEquals(dto.getTitle(), entity.name());
        assertEquals(dto.getPrice(), entity.price());
        assertEquals(mapper.toEntity(dto), entity);
    }

    @Test
    void shouldKeepOriginalEntityUnchanged() {
        Book original = sampleEntity();
        BookDto dto = mapper.toDto(original);
        dto.setTitle("Updated Book Title");

        Book converted = mapper.toEntity(dto);

        assertEquals("Getting Started with Jimmer", original.name());
        assertEquals("Updated Book Title", converted.name());
        assertNotSame(original, converted);
    }

    @Test
    void shouldSupportRoundTrip() {
        Book original = sampleEntity();

        assertEquals(original, mapper.toEntity(mapper.toDto(original)));
    }

    @Test
    void shouldConvertListsInBothDirections() {
        List<Book> entities = List.of(sampleEntity());

        List<BookDto> dtos = mapper.toDtoList(entities);

        assertEquals(1, dtos.size());
        assertEquals("Getting Started with Jimmer", dtos.get(0).getTitle());
        assertEquals(entities, mapper.toEntityList(dtos));
    }

    @Test
    void shouldReturnNullForNullInputs() {
        assertNull(mapper.toDto(null));
        assertNull(mapper.toEntity(null));
        assertNull(mapper.toEntityExplicit(null));
        assertNull(mapper.toDtoList(null));
        assertNull(mapper.toEntityList(null));
    }

    @Test
    void shouldConvertEmptyLists() {
        assertTrue(mapper.toDtoList(List.of()).isEmpty());
        assertTrue(mapper.toEntityList(List.of()).isEmpty());
    }

    @Test
    void shouldRejectReadingUnloadedEntityProperties() {
        // Unloaded is not the same as null: all properties used by this mapping must be loaded.
        Book partial = BookDraft.$.produce(draft -> draft.setId(1L));

        assertThrows(UnloadedException.class, () -> mapper.toDto(partial));
    }

    private static Book sampleEntity() {
        return BookDraft.$.produce(draft -> draft
                .setId(1L)
                .setName("Getting Started with Jimmer")
                .setPrice(new BigDecimal("59.90")));
    }

    private static BookDto sampleDto() {
        BookDto dto = new BookDto();
        dto.setId(2L);
        dto.setTitle("Getting Started with MapStruct");
        dto.setPrice(new BigDecimal("39.90"));
        return dto;
    }
}
