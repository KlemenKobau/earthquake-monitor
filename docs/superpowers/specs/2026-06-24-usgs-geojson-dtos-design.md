# USGS GeoJSON Feed DTOs — Design

## Purpose

Model the response shape of the USGS earthquake GeoJSON feed
(`https://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php`) as Java DTOs so the
application can deserialize it via Jackson. Scope is USGS-specific, not the full GeoJSON
spec — geometry is always `Point` for this feed, and properties are the documented USGS
fields, not a generic payload type.

## Decisions

- **USGS-specific, non-generic types** — no generic `Feature<P>`/`FeatureCollection<P>`.
  `EarthquakeFeature` embeds `EarthquakeProperties` directly.
- **Java records + Jackson** — immutable, no Lombok. Field names matching JSON use Jackson's
  native record support; mismatches use `@JsonProperty`.
- **Dedicated `Coordinates` record** with a custom Jackson deserializer converting the raw
  `[lon, lat, depth]` JSON array into named fields (`longitude`, `latitude`, `depth`).
- **All documented USGS properties fields** included (not a subset).
- **Top-level `metadata` object** included.
- All properties fields are nullable in practice (confirmed via a live sample fetch — e.g.
  `felt`/`cdi` are frequently `null`), so boxed types are used throughout, not primitives.

## Types (package `com.kkobau.earthquakemonitor.dto`)

```
EarthquakeFeedResponse
  String type                      // "FeatureCollection"
  Metadata metadata
  List<EarthquakeFeature> features
  double[] bbox                    // nullable, present on some feed variants

Metadata
  Long generated                   // epoch millis
  String url
  String title
  Integer status                   // HTTP-style status code, e.g. 200
  String api
  Integer count

EarthquakeFeature
  String type                      // "Feature"
  EarthquakeProperties properties
  PointGeometry geometry
  String id

PointGeometry
  String type                      // "Point"
  Coordinates coordinates          // custom-deserialized from JSON array

Coordinates                        // not a 1:1 JSON field, built by deserializer
  double longitude
  double latitude
  double depth                     // 0.0 if the source array omits a third element

EarthquakeProperties
  Double mag
  String place
  Long time                        // epoch millis
  Long updated                     // epoch millis
  String tz                        // deprecated by USGS, always null in practice, kept for fidelity
  String url
  String detail
  Integer felt
  Double cdi
  Double mmi
  String alert
  String status
  Integer tsunami
  Integer sig
  String net
  String code
  String ids                       // comma-delimited, e.g. ",us7000sui3,"
  String sources                   // comma-delimited
  String types                     // comma-delimited
  Integer nst
  Double dmin
  Double rms
  Double gap
  String magType
  String type                      // "earthquake", etc.
  String title                     // human-readable summary, e.g. "M 6.6 - ..."
```

## Coordinates deserialization

`PointGeometry.coordinates` is backed by a custom `JsonDeserializer<Coordinates>` registered via
`@JsonDeserialize(using = ...)` on the record component, reading the JSON array
`[longitude, latitude, depth?]` positionally. If the array has only 2 elements, `depth` defaults
to `0.0`.

## Out of scope

- Generic GeoJSON geometry types (LineString, Polygon, etc.) — this feed only emits `Point`.
- HTTP client for fetching the feed (`client/` package) — separate follow-up.
- Persistence/entity mapping — separate follow-up.
