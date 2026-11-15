# Frozen upgrade fixtures

Synthetic media databases, containing no owner's media or credentials. Both 0.153 and 0.154 used schema 63 and
the same prepared helper bytes, so these two compressed fixtures are intentionally identical.

`source-receipts.json` records release source commits, every historical MediaLib preparation operation and its
verified hash, and the resulting VideoOpenHelper/ScraperTables hashes. The pinned upstream helper sources were
prepared in that order; their actual onCreate/upgrade SQL created these databases under native Robolectric SQLite.
Only the temporary generator class name was changed to coexist with the current helper. These are not databases
created by the current schema-64 helper with a changed version number.

Seeds: local file 31, `/storage/old/Film.mkv`, size 123, movie 100/TMDB 123, bookmark 4444, watched and hidden,
user collection 7 with item position 2 and hidden state. `fixture-receipts.json` freezes compressed/database hashes,
uncompressed bytes and schema. HistoricalUpgrade157Test verifies those hashes before opening each fixture and
asserts identities, collection membership, hidden/viewing state, source relinking and retention of the old store.
The test also retains independent settings and an opaque credential file. It does not simulate AndroidKeyStore
decryption or the Android package-manager upgrade itself; those require the signed in-place device matrix.
