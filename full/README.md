### Limitations

These limitation were also present in PHP-CRUD-API.

  - Primary keys should either be auto-increment (from 1 to 2^53) or UUID
  - Composite primary or foreign keys are not supported
  - Complex filters (with both "and" & "or") are not supported
  - Complex writes (transactions) are not supported
  - Complex queries calling functions (like "concat" or "sum") are not supported
  - MySQL storage engine must be either InnoDB or XtraDB
  - Only MySQL, PostgreSQL and SQLServer support spatial/GIS functionality

### Features

These features match features in PHP-CRUD-API.

  - [x] Supports POST variables as input (x-www-form-urlencoded)
  - [x] Supports a JSON object as input
  - [x] Supports a JSON array as input (batch insert)
  - [ ] Supports file upload from web forms (multipart/form-data)
  - [ ] Optional condensed JSON: only first row contains field names
  - [ ] Sanitize and validate input using callbacks
  - [ ] Permission system for databases, tables, columns and records
  - [ ] Multi-tenant database layouts are supported
  - [x] Multi-domain CORS support for cross-domain requests
  - [x] Combined requests with support for multiple table names
  - [x] Search support on multiple criteria
  - [x] Pagination, seeking, sorting and column selection
  - [x] Relation detection and filtering on foreign keys (hasMany)
  - [x] Foreign keys are turned into objects on demand (belongsTo)
  - [x] Atomic increment support via PATCH (for counters)
  - [x] Binary fields supported with base64 encoding
  - [x] Spatial/GIS fields and filters supported with WKT
  - [ ] Unstructured data support through JSON/JSONB/XML
  - [ ] Generate API documentation using Swagger tools
  - [ ] Authentication via JWT token or username/password

### Extra Features

These features are new and where not included in PHP-CRUD-API.

  - [x] Support for output in JSON or XML
  - [x] Does not reflect on every request (better performance)
  - [x] Support for input and output of database structure and records
  - [x] Support for all major database systems (thanks to jOOQ)
  - [x] Support for boolean and binary data in all database engines
  - [x] Support for relational data on read operation (not only on list)

## Features

### Multi-domain CORS support

By default all cross-origin requests are allowed. Use the key "rest.cors.allowed-origins" and
set it to one or multiple hosts in a comma separated list (e.g. "http://localhost:8080,http://localhost:9090").

## Extra Features

### Support for output in JSON or XML

By sending the "Accept: application/json" header you are specifying you want to receive JSON.
By sending the "Accept: application/xml" header you are specifying you want to receive XML. 
Alternatively you may also end your URL in ".json" or ".xml" to specify the output format. 

### Input and output of database structure and records

These are the supported types:

character types:
- varchar(length)
- char(length)
- longvarchar(length)
- clob(length)

unicode types:
- nvarchar(length)
- nchar(length)
- longnvarchar(length)
- nclob(length)

boolean types:
- boolean
- bit

integer types:
- tinyint
- smallint
- integer
- bigint

floating point types:
- double
- float
- real

decimal types:
- numeric(precision,scale)
- decimal(precision,scale)

date/time types:
- date
- time
- timestamp

binary types:
- binary(length)
- varbinary(length)
- longvarbinary(length)
- blob(length)

other types:
- other /* for JDBC unknown types */
- record /* for JDBC STRUCT type */
- result /* emulates REF CURSOR types and similar constructs */
- uuid /* non-jdbc type, limited support */
- geometry /* non-jdbc type, extension with limited support */
- xml /* non-jdbc type, extension with limited support */
- json /* non-jdbc type, extension with limited support */

The length parameter is always optional and not recommended on binary types and large objects (such as clob and nclob).
