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