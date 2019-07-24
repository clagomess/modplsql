# mod-plsql

Lightweight based Java server to run Oracle OWA (Oracle Web Agent)

### Download
- https://github.com/clagomess/modplsql/releases/latest

### Example

OWA Oracle procedure:
```sql
CREATE OR REPLACE PROCEDURE HELLO_WORLD (
    NUM_ENTRIES NUMBER,
    NAME_ARRAY OWA.VC_ARR,
    VALUE_ARRAY OWA.VC_ARR,
    RESERVED OWA.VC_ARR
) IS
BEGIN
    HTP.PRINT('Hello World');
END;
```

config.json:
```json
{
  "dbUrl": "jdbc:oracle:thin:@localhost:1521:ORCLCDB",
  "dbUser": "DBBACKUP",
  "dbPass": "010203",
  "indexPage": "DBBACKUP.HELLO_WORLD",
  "params": [
    {"key": "foo", "value": "bar"}
  ]
}
```

- Run: `java -jar mod-plsql.jar`
- See: `http://localhost:8000`