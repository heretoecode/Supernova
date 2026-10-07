# Language flag assets

National flag PNGs downloaded from https://flagcdn.com/ at 80px width on 7 October 2026; bundled to avoid font/glyph availability differences on Shield. Flag images represent countries/territories without changing the selected language. Source: Flagpedia/FlagCDN public flag artwork.

Generic-language fallback data is frozen from Unicode CLDR likelySubtags (cldr-json/main/cldr-json/cldr-core/supplemental/likelySubtags.json, CLDR version 48). Unicode data license: https://www.unicode.org/license.txt. Explicit regional tags take precedence; English→US, Portuguese→Brazil, Chinese→China, Spanish→Spain, Arabic→Saudi Arabia are explicit product defaults. Remaining languages use the standard likely-region data, approved during implementation clarification. Unknown/undefined languages are honestly labelled without invented nationality.

The frozen likely-region table includes two- and three-letter language identifiers (7,190 country-bearing entries); bibliographic ISO 639-2 aliases resolve to the same language defaults. It is stored as compact data chunks to avoid oversized Java initializer bytecode. Explicit drawable keep rules preserve all country PNGs in shrunk release APKs.
