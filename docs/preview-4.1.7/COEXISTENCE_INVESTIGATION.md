# Upstream Nova coexistence — investigation, not a confirmed diagnosis

The candidate preserves `org.courville.nova.markpreview` and the established
signing certificate. No identity migration has been attempted.

The source manifest still declares `android:sharedUserId="archos.openmediacenter"`
and `android:sharedUserMaxSdkVersion="32"`. Several activities also retain
`archos.task.video` task affinity. The build preparation script isolates the
three inherited content-provider authorities but intentionally does not remove
the shared-user identity.

Android documents that applications sharing a user ID require identical signing
certificate sets, that removing an existing shared-user ID is not a supported
migration, and that sharedUserMaxSdkVersion affects new installations rather
than migrating an existing shared UID:
https://developer.android.com/guide/topics/manifest/manifest-element

This is a plausible installation-conflict mechanism, NOT proof of the reported
Shield failure. We do not yet have the actual upstream APK's merged manifest and
certificate or the Shield installer failure code/log. Shared task affinity alone
is not evidence of an installation failure.

The source-validation workflow now audits the generated NoamazonDebug merged
manifest, preserving a JSON report of package, label, banner, shared-user ID,
provider authorities and task affinities. It fails unexpected identity/provider
changes and records the inherited shared-user ID as a warning. It makes no
manifest changes and neither installs nor uninstalls an application. CI run
36240788783 successfully audited the real merged NoamazonDebug manifest:
package `org.courville.nova.markpreview`, label `Supernova`, version
`6.4.63-mark.4.1.7-preview` / `6040083`, banner `@drawable/preview_custom_banner`,
all eight provider authorities under the Preview package namespace, inherited
shared-user ID and task affinity as described above. No unexpected identity
changes were detected. This is not a physical coexistence test.

Remaining evidence: obtain the exact official APK and installer error, compare
its identifiers/certificate, and inspect installed package/shared-user state on
the Shield without uninstalling either application. Any proposed shared-user
migration requires a separately reviewed upgrade/data-preservation plan and
user approval; do not casually remove the manifest declaration.
