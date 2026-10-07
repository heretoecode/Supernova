# Providers and data-sources audit — 7 October 2026

## Scope
Catalogue metadata, streaming availability, integrations and put.io/data-source boundaries.

## Current sources / roles
- Local Nova/media-provider database: authoritative library membership, file identity and playback state.
- Native/local file inspection: technical media facts.
- TMDb: Preview catalogue enrichment and the underlying metadata gateway.
- Streaming availability: country/provider-specific offers exposed only for allowed streaming/free/ad-supported types; rental/purchase is deliberately excluded.
- Trakt: existing integration plus Featured/trending/popular use where configured.
- OpenSubtitles: subtitle discovery/download integration.
- put.io: code contains account/read/sync/reconciliation/library-bridge components, while original-quality media playback is associated with the WebDAV source. Production OAuth configuration remains an external dependency and must not be invented.
- Network protocols: SMB, WebDAV, SFTP, FTP/FTPS and DLNA/UPnP discovery/browsing have distinct transport roles.

## Findings
- Source responsibilities are currently spread across library, streaming, network and integration code. A written source-of-truth matrix is needed.
- TMDb metadata and streaming availability share infrastructure but are different product domains; failures should remain isolated.
- Country-specific provider state is correct in principle and should stay separate per region.
- put.io native-account features and WebDAV file transport must remain architecturally distinct. This is especially important for future Downloads.
- External-service success must never be required to browse/play already indexed local media.
- Credentials/tokens must never enter diagnostics, GitHub documentation or mock data.

## Recommendations
1. Create a source matrix: capability → authoritative source → fallback → cache → credentials → offline behaviour.
2. Keep transport (WebDAV/SMB/etc.), catalogue metadata, availability and account integrations separate.
3. Add request coalescing/rate/failure telemetry by provider without sensitive URLs/tokens.
4. Define explicit offline behaviour for every external source.
5. For future put.io downloads, implement a generic WebDAV download engine first; put.io can be an initially supported/tested source rather than a hard-coded transfer path.
6. Do not add Google Drive/OneDrive/Dropbox until there is an actual supported connector/transport; “Coming soon” is presentation only.
