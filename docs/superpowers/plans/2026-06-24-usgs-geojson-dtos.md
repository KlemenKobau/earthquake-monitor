# USGS GeoJSON Feed DTOs Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add Java record DTOs in `com.kkobau.earthquakemonitor.dto` that deserialize the USGS earthquake GeoJSON feed response via Jackson.

**Architecture:** Five immutable records mirror the USGS feed JSON shape one-to-one, except `Coordinates`, which is built by a custom `ValueDeserializer` from the raw `[lon, lat, depth]` array. No Spring context is required for these tests — plain `tools.jackson.databind.ObjectMapper` deserializing fixed JSON fixture strings is sufficient and keeps tests fast.

**Tech Stack:** Java 25 records, Jackson 3 (`tools.jackson:jackson-databind`, transitively provided by `spring-boot-starter-jackson` via Spring Boot 4.1's `spring-boot-starter-webmvc`), JUnit 5. Note: Spring Boot 4.1 ships **Jackson 3**, which relocated most classes from `com.fasterxml.jackson.databind`/`.core` to `tools.jackson.databind`/`.core` (e.g. `ObjectMapper`, `JsonNode`, `JsonParser`, `DeserializationContext`) and renamed `JsonDeserializer` to `ValueDeserializer`. Only `jackson-annotations` (e.g. `@JsonProperty`) remains under `com.fasterxml.jackson.annotation`. `@JsonDeserialize` itself moved to `tools.jackson.databind.annotation`.

Reference spec: `docs/superpowers/specs/2026-06-24-usgs-geojson-dtos-design.md`

---

### Task 1: `Metadata` record

**Files:**
- Create: `src/main/java/com/kkobau/earthquakemonitor/dto/Metadata.java`
- Test: `src/test/java/com/kkobau/earthquakemonitor/dto/MetadataTest.java`

- [ ] **Step 1: Write the failing test**

```java
package com.kkobau.earthquakemonitor.dto;

import static org.assertj.core.api.Assertions.assertThat;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class MetadataTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesFromUsgsJson() throws Exception {
        String json = """
                {
                  "generated": 1782289885000,
                  "url": "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/significant_month.geojson",
                  "title": "USGS Significant Earthquakes, Past Month",
                  "status": 200,
                  "api": "2.4.0",
                  "count": 16
                }
                """;

        Metadata metadata = objectMapper.readValue(json, Metadata.class);

        assertThat(metadata.generated()).isEqualTo(1782289885000L);
        assertThat(metadata.url()).isEqualTo("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/significant_month.geojson");
        assertThat(metadata.title()).isEqualTo("USGS Significant Earthquakes, Past Month");
        assertThat(metadata.status()).isEqualTo(200);
        assertThat(metadata.api()).isEqualTo("2.4.0");
        assertThat(metadata.count()).isEqualTo(16);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw test -Dtest=MetadataTest`
Expected: FAIL to compile — `Metadata` does not exist.

- [ ] **Step 3: Write the record**

```java
package com.kkobau.earthquakemonitor.dto;

public record Metadata(
        Long generated,
        String url,
        String title,
        Integer status,
        String api,
        Integer count
) {
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw test -Dtest=MetadataTest`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/kkobau/earthquakemonitor/dto/Metadata.java src/test/java/com/kkobau/earthquakemonitor/dto/MetadataTest.java
git commit -m "feat: add Metadata DTO for USGS feed envelope"
```

---

### Task 2: `Coordinates` record + custom deserializer

**Files:**
- Create: `src/main/java/com/kkobau/earthquakemonitor/dto/Coordinates.java`
- Create: `src/main/java/com/kkobau/earthquakemonitor/dto/CoordinatesDeserializer.java`
- Test: `src/test/java/com/kkobau/earthquakemonitor/dto/CoordinatesTest.java`

- [ ] **Step 1: Write the failing test**

```java
package com.kkobau.earthquakemonitor.dto;

import static org.assertj.core.api.Assertions.assertThat;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class CoordinatesTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesLongitudeLatitudeDepthFromArray() throws Exception {
        Coordinates coordinates = objectMapper.readValue("[160.5652,52.7943,10.0]", Coordinates.class);

        assertThat(coordinates.longitude()).isEqualTo(160.5652);
        assertThat(coordinates.latitude()).isEqualTo(52.7943);
        assertThat(coordinates.depth()).isEqualTo(10.0);
    }

    @Test
    void defaultsDepthToZeroWhenArrayHasOnlyTwoElements() throws Exception {
        Coordinates coordinates = objectMapper.readValue("[160.5652,52.7943]", Coordinates.class);

        assertThat(coordinates.longitude()).isEqualTo(160.5652);
        assertThat(coordinates.latitude()).isEqualTo(52.7943);
        assertThat(coordinates.depth()).isEqualTo(0.0);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw test -Dtest=CoordinatesTest`
Expected: FAIL to compile — `Coordinates` does not exist.

- [ ] **Step 3: Write the record and deserializer**

```java
package com.kkobau.earthquakemonitor.dto;

import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = CoordinatesDeserializer.class)
public record Coordinates(double longitude, double latitude, double depth) {
}
```

```java
package com.kkobau.earthquakemonitor.dto;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

public class CoordinatesDeserializer extends ValueDeserializer<Coordinates> {

    @Override
    public Coordinates deserialize(JsonParser jsonParser, DeserializationContext context) throws JacksonException {
        JsonNode arrayNode = jsonParser.readValueAsTree();
        double longitude = arrayNode.get(0).asDouble();
        double latitude = arrayNode.get(1).asDouble();
        double depth = arrayNode.size() > 2 ? arrayNode.get(2).asDouble() : 0.0;
        return new Coordinates(longitude, latitude, depth);
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw test -Dtest=CoordinatesTest`
Expected: PASS (both test methods)

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/kkobau/earthquakemonitor/dto/Coordinates.java src/main/java/com/kkobau/earthquakemonitor/dto/CoordinatesDeserializer.java src/test/java/com/kkobau/earthquakemonitor/dto/CoordinatesTest.java
git commit -m "feat: add Coordinates DTO with array-to-record deserializer"
```

---

### Task 3: `PointGeometry` record

**Files:**
- Create: `src/main/java/com/kkobau/earthquakemonitor/dto/PointGeometry.java`
- Test: `src/test/java/com/kkobau/earthquakemonitor/dto/PointGeometryTest.java`

- [ ] **Step 1: Write the failing test**

```java
package com.kkobau.earthquakemonitor.dto;

import static org.assertj.core.api.Assertions.assertThat;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class PointGeometryTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesFromUsgsJson() throws Exception {
        String json = """
                {
                  "type": "Point",
                  "coordinates": [160.5652, 52.7943, 10.0]
                }
                """;

        PointGeometry geometry = objectMapper.readValue(json, PointGeometry.class);

        assertThat(geometry.type()).isEqualTo("Point");
        assertThat(geometry.coordinates().longitude()).isEqualTo(160.5652);
        assertThat(geometry.coordinates().latitude()).isEqualTo(52.7943);
        assertThat(geometry.coordinates().depth()).isEqualTo(10.0);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw test -Dtest=PointGeometryTest`
Expected: FAIL to compile — `PointGeometry` does not exist.

- [ ] **Step 3: Write the record**

```java
package com.kkobau.earthquakemonitor.dto;

public record PointGeometry(String type, Coordinates coordinates) {
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw test -Dtest=PointGeometryTest`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/kkobau/earthquakemonitor/dto/PointGeometry.java src/test/java/com/kkobau/earthquakemonitor/dto/PointGeometryTest.java
git commit -m "feat: add PointGeometry DTO"
```

---

### Task 4: `EarthquakeProperties` record

**Files:**
- Create: `src/main/java/com/kkobau/earthquakemonitor/dto/EarthquakeProperties.java`
- Test: `src/test/java/com/kkobau/earthquakemonitor/dto/EarthquakePropertiesTest.java`

- [ ] **Step 1: Write the failing test**

```java
package com.kkobau.earthquakemonitor.dto;

import static org.assertj.core.api.Assertions.assertThat;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class EarthquakePropertiesTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesFromUsgsJson() throws Exception {
        String json = """
                {
                  "mag": 6.6,
                  "place": "133 km ESE of Petropavlovsk-Kamchatsky, Russia",
                  "time": 1781851951597,
                  "updated": 1781940624773,
                  "tz": null,
                  "url": "https://earthquake.usgs.gov/earthquakes/eventpage/us7000sui3",
                  "detail": "https://earthquake.usgs.gov/earthquakes/feed/v1.0/detail/us7000sui3.geojson",
                  "felt": 1,
                  "cdi": 2,
                  "mmi": 5.354,
                  "alert": "green",
                  "status": "reviewed",
                  "tsunami": 0,
                  "sig": 670,
                  "net": "us",
                  "code": "7000sui3",
                  "ids": ",us7000sui3,",
                  "sources": ",us,",
                  "types": ",dyfi,ground-failure,losspager,moment-tensor,origin,phase-data,shakemap,",
                  "nst": 188,
                  "dmin": 1.762,
                  "rms": 0.58,
                  "gap": 31,
                  "magType": "mww",
                  "type": "earthquake",
                  "title": "M 6.6 - 133 km ESE of Petropavlovsk-Kamchatsky, Russia"
                }
                """;

        EarthquakeProperties properties = objectMapper.readValue(json, EarthquakeProperties.class);

        assertThat(properties.mag()).isEqualTo(6.6);
        assertThat(properties.place()).isEqualTo("133 km ESE of Petropavlovsk-Kamchatsky, Russia");
        assertThat(properties.time()).isEqualTo(1781851951597L);
        assertThat(properties.updated()).isEqualTo(1781940624773L);
        assertThat(properties.tz()).isNull();
        assertThat(properties.url()).isEqualTo("https://earthquake.usgs.gov/earthquakes/eventpage/us7000sui3");
        assertThat(properties.detail()).isEqualTo("https://earthquake.usgs.gov/earthquakes/feed/v1.0/detail/us7000sui3.geojson");
        assertThat(properties.felt()).isEqualTo(1);
        assertThat(properties.cdi()).isEqualTo(2);
        assertThat(properties.mmi()).isEqualTo(5.354);
        assertThat(properties.alert()).isEqualTo("green");
        assertThat(properties.status()).isEqualTo("reviewed");
        assertThat(properties.tsunami()).isEqualTo(0);
        assertThat(properties.sig()).isEqualTo(670);
        assertThat(properties.net()).isEqualTo("us");
        assertThat(properties.code()).isEqualTo("7000sui3");
        assertThat(properties.ids()).isEqualTo(",us7000sui3,");
        assertThat(properties.sources()).isEqualTo(",us,");
        assertThat(properties.types()).isEqualTo(",dyfi,ground-failure,losspager,moment-tensor,origin,phase-data,shakemap,");
        assertThat(properties.nst()).isEqualTo(188);
        assertThat(properties.dmin()).isEqualTo(1.762);
        assertThat(properties.rms()).isEqualTo(0.58);
        assertThat(properties.gap()).isEqualTo(31);
        assertThat(properties.magType()).isEqualTo("mww");
        assertThat(properties.type()).isEqualTo("earthquake");
        assertThat(properties.title()).isEqualTo("M 6.6 - 133 km ESE of Petropavlovsk-Kamchatsky, Russia");
    }

    @Test
    void allowsNullFeltAndCdi() throws Exception {
        String json = """
                {
                  "mag": 6.6,
                  "place": "central Mid-Atlantic Ridge",
                  "time": 1781722618159,
                  "updated": 1781888494516,
                  "felt": null,
                  "cdi": null,
                  "mmi": 0,
                  "tsunami": 0,
                  "sig": 670,
                  "magType": "mww",
                  "type": "earthquake",
                  "title": "M 6.6 - central Mid-Atlantic Ridge"
                }
                """;

        EarthquakeProperties properties = objectMapper.readValue(json, EarthquakeProperties.class);

        assertThat(properties.felt()).isNull();
        assertThat(properties.cdi()).isNull();
        assertThat(properties.mmi()).isEqualTo(0);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw test -Dtest=EarthquakePropertiesTest`
Expected: FAIL to compile — `EarthquakeProperties` does not exist.

- [ ] **Step 3: Write the record**

```java
package com.kkobau.earthquakemonitor.dto;

public record EarthquakeProperties(
        Double mag,
        String place,
        Long time,
        Long updated,
        String tz,
        String url,
        String detail,
        Integer felt,
        Double cdi,
        Double mmi,
        String alert,
        String status,
        Integer tsunami,
        Integer sig,
        String net,
        String code,
        String ids,
        String sources,
        String types,
        Integer nst,
        Double dmin,
        Double rms,
        Double gap,
        String magType,
        String type,
        String title
) {
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw test -Dtest=EarthquakePropertiesTest`
Expected: PASS (both test methods)

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/kkobau/earthquakemonitor/dto/EarthquakeProperties.java src/test/java/com/kkobau/earthquakemonitor/dto/EarthquakePropertiesTest.java
git commit -m "feat: add EarthquakeProperties DTO"
```

---

### Task 5: `EarthquakeFeature` record

**Files:**
- Create: `src/main/java/com/kkobau/earthquakemonitor/dto/EarthquakeFeature.java`
- Test: `src/test/java/com/kkobau/earthquakemonitor/dto/EarthquakeFeatureTest.java`

- [ ] **Step 1: Write the failing test**

```java
package com.kkobau.earthquakemonitor.dto;

import static org.assertj.core.api.Assertions.assertThat;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class EarthquakeFeatureTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesFromUsgsJson() throws Exception {
        String json = """
                {
                  "type": "Feature",
                  "properties": {
                    "mag": 6.6,
                    "place": "133 km ESE of Petropavlovsk-Kamchatsky, Russia",
                    "time": 1781851951597,
                    "magType": "mww",
                    "type": "earthquake",
                    "title": "M 6.6 - 133 km ESE of Petropavlovsk-Kamchatsky, Russia"
                  },
                  "geometry": {
                    "type": "Point",
                    "coordinates": [160.5652, 52.7943, 10.0]
                  },
                  "id": "us7000sui3"
                }
                """;

        EarthquakeFeature feature = objectMapper.readValue(json, EarthquakeFeature.class);

        assertThat(feature.type()).isEqualTo("Feature");
        assertThat(feature.id()).isEqualTo("us7000sui3");
        assertThat(feature.properties().mag()).isEqualTo(6.6);
        assertThat(feature.properties().title()).isEqualTo("M 6.6 - 133 km ESE of Petropavlovsk-Kamchatsky, Russia");
        assertThat(feature.geometry().type()).isEqualTo("Point");
        assertThat(feature.geometry().coordinates().longitude()).isEqualTo(160.5652);
        assertThat(feature.geometry().coordinates().latitude()).isEqualTo(52.7943);
        assertThat(feature.geometry().coordinates().depth()).isEqualTo(10.0);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw test -Dtest=EarthquakeFeatureTest`
Expected: FAIL to compile — `EarthquakeFeature` does not exist.

- [ ] **Step 3: Write the record**

```java
package com.kkobau.earthquakemonitor.dto;

public record EarthquakeFeature(
        String type,
        EarthquakeProperties properties,
        PointGeometry geometry,
        String id
) {
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw test -Dtest=EarthquakeFeatureTest`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/kkobau/earthquakemonitor/dto/EarthquakeFeature.java src/test/java/com/kkobau/earthquakemonitor/dto/EarthquakeFeatureTest.java
git commit -m "feat: add EarthquakeFeature DTO"
```

---

### Task 6: `EarthquakeFeedResponse` record (top-level)

**Files:**
- Create: `src/main/java/com/kkobau/earthquakemonitor/dto/EarthquakeFeedResponse.java`
- Test: `src/test/java/com/kkobau/earthquakemonitor/dto/EarthquakeFeedResponseTest.java`

- [ ] **Step 1: Write the failing test**

```java
package com.kkobau.earthquakemonitor.dto;

import static org.assertj.core.api.Assertions.assertThat;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class EarthquakeFeedResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesFullFeedResponse() throws Exception {
        String json = """
                {
                  "type": "FeatureCollection",
                  "metadata": {
                    "generated": 1782289885000,
                    "url": "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/significant_month.geojson",
                    "title": "USGS Significant Earthquakes, Past Month",
                    "status": 200,
                    "api": "2.4.0",
                    "count": 1
                  },
                  "features": [
                    {
                      "type": "Feature",
                      "properties": {
                        "mag": 6.6,
                        "place": "133 km ESE of Petropavlovsk-Kamchatsky, Russia",
                        "time": 1781851951597,
                        "magType": "mww",
                        "type": "earthquake",
                        "title": "M 6.6 - 133 km ESE of Petropavlovsk-Kamchatsky, Russia"
                      },
                      "geometry": {
                        "type": "Point",
                        "coordinates": [160.5652, 52.7943, 10.0]
                      },
                      "id": "us7000sui3"
                    }
                  ],
                  "bbox": [160.5652, 52.7943, 10.0, 160.5652, 52.7943, 10.0]
                }
                """;

        EarthquakeFeedResponse response = objectMapper.readValue(json, EarthquakeFeedResponse.class);

        assertThat(response.type()).isEqualTo("FeatureCollection");
        assertThat(response.metadata().count()).isEqualTo(1);
        assertThat(response.features()).hasSize(1);
        assertThat(response.features().get(0).id()).isEqualTo("us7000sui3");
        assertThat(response.bbox()).containsExactly(160.5652, 52.7943, 10.0, 160.5652, 52.7943, 10.0);
    }

    @Test
    void allowsMissingBbox() throws Exception {
        String json = """
                {
                  "type": "FeatureCollection",
                  "metadata": {
                    "generated": 1782289885000,
                    "url": "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/significant_month.geojson",
                    "title": "USGS Significant Earthquakes, Past Month",
                    "status": 200,
                    "api": "2.4.0",
                    "count": 0
                  },
                  "features": []
                }
                """;

        EarthquakeFeedResponse response = objectMapper.readValue(json, EarthquakeFeedResponse.class);

        assertThat(response.bbox()).isNull();
        assertThat(response.features()).isEmpty();
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw test -Dtest=EarthquakeFeedResponseTest`
Expected: FAIL to compile — `EarthquakeFeedResponse` does not exist.

- [ ] **Step 3: Write the record**

```java
package com.kkobau.earthquakemonitor.dto;

import java.util.List;

public record EarthquakeFeedResponse(
        String type,
        Metadata metadata,
        List<EarthquakeFeature> features,
        double[] bbox
) {
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw test -Dtest=EarthquakeFeedResponseTest`
Expected: PASS (both test methods)

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/kkobau/earthquakemonitor/dto/EarthquakeFeedResponse.java src/test/java/com/kkobau/earthquakemonitor/dto/EarthquakeFeedResponseTest.java
git commit -m "feat: add EarthquakeFeedResponse top-level DTO"
```

---

### Task 7: Full suite run

**Files:** none (verification only)

- [ ] **Step 1: Run the full test suite**

Run: `./mvnw test`
Expected: All tests pass, including the 6 new DTO test classes and the pre-existing `EarthquakemonitorApplicationTests`.

- [ ] **Step 2: Confirm no leftover uncommitted changes**

Run: `git status`
Expected: Clean working tree (everything committed in Tasks 1–6).
