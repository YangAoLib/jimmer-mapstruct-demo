# Jimmer + MapStruct Starter Project

A Maven project that runs without Spring Boot or a database, demonstrating bidirectional mapping between plain Java classes and Jimmer entities.

## Technology Versions

- Java 17+ (compilation target: 17; verified on JDK 21)
- Maven 3.9+
- Jimmer 0.12.2
- MapStruct 1.6.3
- JUnit 5.11.4

## Running the Project

Run the following commands in the project directory:

```bash
mvn clean verify
mvn compile exec:java
```

Example output:

```text
Entity to plain class: id=1, title=Getting Started with Jimmer, price=59.90
Plain class to Entity: id=1, name=Getting Started with MapStruct, price=59.90
Original immutable Entity remains unchanged: name=Getting Started with Jimmer
```

If non-ASCII characters appear garbled in a Windows terminal, configure the Maven JVM output encoding for the current terminal session. For example, in PowerShell:

```powershell
$env:MAVEN_OPTS = "$env:MAVEN_OPTS -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8"
mvn compile exec:java
```

## Project Structure

```text
src/main/java/com/example/jimmer/
├── DemoApplication.java       # Runnable bidirectional mapping example
├── dto/BookDto.java            # Plain JavaBean with getters and setters
├── entity/Book.java            # Jimmer entity interface with id()/name()/price()
└── mapper/BookMapper.java      # MapStruct mapper interface
src/test/java/com/example/jimmer/mapper/
└── BookMapperTest.java         # Tests for bidirectional mapping, collections, null, immutability, and more
```

## Mapping Methods

```java
// Jimmer entity to plain Java class
BookDto dto = BookMapper.INSTANCE.toDto(entity);

// Plain Java class to a new Jimmer entity
Book entity = BookMapper.INSTANCE.toEntity(dto);

// Collection mapping
List<BookDto> dtos = BookMapper.INSTANCE.toDtoList(entities);
List<Book> entities = BookMapper.INSTANCE.toEntityList(dtos);
```

`Book.name` and `BookDto.title` intentionally use different names to demonstrate field mapping:

```java
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mapping(source = "name", target = "title")
    BookDto toDto(Book entity);

    @InheritInverseConfiguration(name = "toDto")
    Book toEntity(BookDto dto);
}
```

The identically named `id` and `price` properties are mapped automatically; the inverse configuration reuses the forward mapping. Adding an unmapped target property causes compilation to fail, preventing fields from being missed.

### Explicit Inverse Mapping

In addition to `toEntity`, which reuses the forward configuration, a separate method with explicit mappings is provided:

```java
@Named("toEntityExplicit")
@Mapping(source = "id", target = "id")
@Mapping(source = "title", target = "name")
@Mapping(source = "price", target = "price")
Book toEntityExplicit(BookDto dto);
```

Call `BookMapper.INSTANCE.toEntityExplicit(dto)` to use it. This method specifies each field mapping explicitly, while MapStruct still generates the builder-based conversion code rather than requiring a handwritten implementation. `@Named` prevents ambiguity in collection mapping when two methods have identical parameter and return types; the existing `toEntityList` continues to use the original `toEntity` method.

## Key Integration Details

The `annotationProcessorPaths` section in `pom.xml` configures all of the following:

1. `jimmer-apt`: generates `BookDraft`, `BookDraft.Builder`, table metadata, and related code.
2. `jimmer-mapstruct-apt`: the official MapStruct SPI integration, recognizing Jimmer's non-JavaBean getters and immutable entity builders.
3. `mapstruct-processor`: generates `BookMapperImpl`.

No handwritten entity implementations, custom property naming SPI, or field-by-field copying code are needed. This standalone Java project also explicitly includes Jackson to provide the annotations and runtime support required by Jimmer-generated code.

Generated code is located in `target/generated-sources/annotations/`; do not edit or commit it manually. The core logic of the generated inverse mapping is:

```java
BookDraft.Builder book = new BookDraft.Builder();
book.name(dto.getTitle());
book.id(dto.getId());
book.price(dto.getPrice());
return book.build();
```

## Notes

- Jimmer entities are immutable interfaces. Modifying a DTO and mapping it again creates a new entity without changing the original.
- In this example, both `name` and `price` are non-nullable properties; callers should supply non-null values. The Jimmer builder skips null assignments to these properties, potentially producing an entity with unloaded properties rather than immediately throwing a validation exception. This project does not introduce an additional business validation framework.
- An unloaded Jimmer property is not the same as `null`. This mapping reads all three properties directly; database queries must load `id`, `name`, and `price`, or mapping will throw `UnloadedException`. Tests cover this behavior.
- A `null` input returns `null`; an empty collection maps to an empty collection.
- This project demonstrates object mapping only and does not include database connections, CRUD operations, or web endpoints.

## IntelliJ IDEA

Open `pom.xml` in this directory and import it as a Maven project. Select JDK 17 or later as the project SDK. Run `mvn compile` first to generate the source code. If the editor still reports that `BookDraft` does not exist, reload the Maven project and confirm that annotation processing is enabled.

## Test Coverage

`mvn clean verify` runs 9 tests covering entity-to-plain-class mapping, plain-class-to-entity mapping, explicit field mapping to an entity, original entity immutability, round-trip consistency, bidirectional collection mapping, null inputs, empty collections, and exceptions for unloaded properties.
