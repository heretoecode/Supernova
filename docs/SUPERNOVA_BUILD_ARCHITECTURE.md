# Supernova build architecture

## Canonical repository

`heretoecode/Supernova` is the canonical application repository. The default development branch is `main`.

A safety snapshot of the complete pre-cleanup Preview 4.1.5 state is retained at `archive/pre-github-cleanup-2026-09-24`.

## Reproducible dependency assembly

Supernova intentionally remains a multi-module Android build rather than copying upstream source into this repository.

The active GitHub Actions workflows:

1. initialise the upstream NOVA manifest tooling from `nova-video-player/aos-AVP`;
2. replace the upstream manifest with Supernova's pinned `.github/build/nova-ci.xml`;
3. sync the exact pinned module revisions;
4. overlay the triggering Supernova revision as the `Video` module;
5. apply the reviewed Supernova patches to supporting modules where required;
6. compile, test and package the resulting application.

The personal `heretoecode/aos-AVP` fork is **not** used by this process.

## Supporting modules

The pinned manifest records the exact revisions used for supporting repositories such as MediaLib, FileCoreLibrary and native/prebuilt components. Supernova-specific corrections that must remain reproducible are stored as reviewed patch files under `.github/build/`.

This approach keeps one canonical Supernova repository in control of the complete build while avoiding duplicated copies of large upstream projects.

## Safety rules

- Do not replace the pinned manifest with moving branch names.
- Do not generate a new signing identity when the expected Preview signing key is unavailable.
- Do not remove historical branches until the canonical `main` build has been validated.
- Preserve upstream attribution and licensing.
- Treat historical handovers and QA records as historical evidence; do not rewrite them merely to modernise repository names.
