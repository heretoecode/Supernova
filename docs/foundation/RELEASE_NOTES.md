# Supernova Foundation correction — signed 0.134

The first signed Foundation 0.133 was installed successfully; Shield testing revealed missing approved Home carousel and artwork focus containment. This correction restores those behaviours from verified Preview corrective source f97f6294 while preserving all Foundation work. Corrected signed delivery passed independent verification; physical Shield validation remains pending.

- Home uses a bounded active hero card with equal-height previous/next neighbours, fixed title/logo and metadata positions, and one More Info action. Left/right cycles the existing selection; More Info keeps the existing Details action.
- Row artwork is clipped to the rounded body; artwork, captions and focus highlight scale together. Home rail clearance matches the accepted composition.
- Foundation always selects the approved interface, including restored false legacy UI preferences, without exposing Try New UI or resetting other choices.
- Identity `app.supernova.player`, protected existing PKCS#12 signing, approved icon/banner/splash/Space Black Blend, both double-ring indicators, About/licences, dependency/provider integration and source-verified FFmpeg remain intact.
- Literal actual-build history now records independently verified first Foundation 0.133; the corrected candidate continues as **0.134 / versionCode 134**. All prior 132 entries are preserved exactly.

Other maintenance-lineage omissions are recorded in [the register](PREVIEW_OMISSION_REGISTER.json) and remain deferred. No unrelated feature, onboarding or import work is included. See [correction evidence and physical checks](FOUNDATION_UI_CORRECTION.md).

## First Foundation 0.133 — delivered

Permanent identity and signing; approved branding and animated loading indicators; approved About, credits, technical information and full offline licences; reconstructed 0.1–0.132 history; exact-source four-ABI media libraries. Signed source 11766e59; verified delivery run 37942503242. Physical installation passed according to the user; carousel/focus regressions led to this correction. No broader physical acceptance is claimed.

Signed source cf1211ceea12a6e1ae82e10cbfb247d33d4ad01f; [download APK](https://github.com/heretoecode/Supernova/actions/runs/37961864830/artifacts/11632643927). SHA-256 `8d159d9ffc70d5ffbe335c0caa280fdf742909911581c85d55865d906ff58e29`. See [delivery index](UI_SIGNED_DELIVERY.json).
