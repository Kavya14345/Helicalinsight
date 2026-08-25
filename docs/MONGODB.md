# MongoDB Connectivity

Helical Insight exposes MongoDB through the existing NoSQL data-source flow. The
MongoDB connection test uses the MongoDB Java Sync Driver and runs a `ping`
command against the configured database. The existing Drill-backed NoSQL loader
continues to create the middleware storage used by metadata and query execution.

## Configuration

1. Make MongoDB reachable from the Helical Insight server. The default connection
   is `mongodb://localhost:27017`.
2. Open **Administration -> Data Sources** and choose **MongoDB** under **No SQL &
   Big Data**.
3. Enter the MongoDB host, port, database name, and collection. The default port
   is `27017`.
4. For an authenticated deployment, enter the MongoDB username and password and,
   when required, set `authSource` to the database that stores the user.
5. Test the connection, save it, and use the saved data source when creating
   metadata.

The connection form stores the generated MongoDB URL in `jdbcUrl`. It also
accepts a complete `mongodb://` or `mongodb+srv://` URL, so replica sets and
cloud-hosted MongoDB deployments can be configured without changing application
code.

## Implementation

- `MongoDbConnectionProvider` builds a MongoDB client from either the generated
  host/port fields or a complete URI and validates it with `ping`.
- `MongoDrillLoader` adapts Helical Insight's existing NoSQL form fields to that
  provider for connection testing.
- The existing `com.helicalinsight.nosql.mongo` driver registration and Drill
  storage lifecycle remain unchanged for compatibility with existing data sources.