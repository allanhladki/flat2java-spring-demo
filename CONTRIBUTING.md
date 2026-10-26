# Contributing to flat2java-spring-demo

Bug reports, questions and pull requests are welcome. This page describes how to build the project
and what a pull request needs to be merged.

This repository is a demonstration of [flat2java](https://github.com/allanhladki/flat2java). Report
problems with the library itself (mapping rules, record format, exception messages) in the
[library repository](https://github.com/allanhladki/flat2java/issues/new/choose).

## Reporting a bug

[Open an issue](https://github.com/allanhladki/flat2java-spring-demo/issues/new/choose) with:

- the demo version or commit, and the Java version;
- how the application runs: dev mode, packaged JVM, container or native;
- the request, for example a `curl` command, with any personal data replaced;
- the response body or the relevant log lines.

> [!IMPORTANT]
> Do not report security vulnerabilities in public issues. See [SECURITY.md](SECURITY.md).

## Development setup

| Tool | Version |
|---|---|
| JDK | 21 |
| Maven | Not required: use the Maven Wrapper (`./mvnw`, or `mvnw.cmd` on Windows) |
| Docker or Podman | Optional, to build the container image |

```bash
git clone https://github.com/allanhladki/flat2java-spring-demo.git
cd flat2java-spring-demo
./mvnw verify
```

| Command | Purpose |
|---|---|
| `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev` | Starts the application with the dev profile |
| `./mvnw verify` | Runs the tests, checks formatting, writes the JaCoCo report to `target/site/jacoco` |
| `./mvnw spotless:apply` | Formats the code; `verify` fails on unformatted code |
| `./mvnw test -Dtest=RecordEndpointsTest` | Runs one test class |
| `./mvnw package && docker build -t flat2java-spring-demo:jvm .` | Builds the JVM image, as CI does |

## Pull requests

- Keep each pull request to one change, and describe the behaviour before and after.
- Add a test for every bug fix and every new endpoint. Tests use `@SpringBootTest` and REST Assured.
- Code, comments and documentation are written in English.
- New or changed endpoints and configuration need a README update and an entry in
  [CHANGELOG.md](CHANGELOG.md).
- Never log record contents or field values: flat files often contain personal data.
- Pull requests are squash-merged: the pull request title becomes the commit on `main`, so write
  it as a [Conventional Commit](https://www.conventionalcommits.org/), for example
  `feat(api): add an endpoint that reads a file with many records`. Use `!` for breaking changes
  (`feat!: ...`).
- `main` is protected: changes go through a pull request, with the `verify` and
  `container image` checks passing and the branch up to date.
- [CODEOWNERS](.github/CODEOWNERS) requests a review from the maintainer on every pull request.
  Approval is not required to merge.

## Releasing

Maintainers only. The demo version follows the flat2java version it demonstrates. A release is a
pull request with the `release` label: merging it creates the tag and the GitHub release. The
demo is not published to Maven Central.

1. Open a pull request that:
   - sets the release version, dropping `-SNAPSHOT`:

     ```bash
     ./mvnw versions:set -DnewVersion=0.1.0 -DgenerateBackupPoms=false
     ```

   - dates the version's CHANGELOG section: `## [0.1.0] - 2026-10-26`;
   - has the **`release`** label.
2. Merge it. The [Release workflow](.github/workflows/release.yml) then:
   1. checks that the merged pull request has the `release` label, that the version is not a
      `-SNAPSHOT`, that the tag `v0.1.0` does not exist yet, and that the CHANGELOG section is
      dated;
   2. creates the tag `v0.1.0` on the merge commit, signed with the release key;
   3. creates the GitHub release with the CHANGELOG section as notes.
3. Open a pull request that sets the next `-SNAPSHOT` version and adds an empty
   `## [Unreleased]` section to the CHANGELOG. Do not label it.

> [!IMPORTANT]
> The `release` label is the only switch. A merged pull request without it never releases, even
> when the version has no `-SNAPSHOT`.

The workflow uses the `release` environment, with the secrets `GPG_PRIVATE_KEY` (armored release
key) and `GPG_PASSPHRASE`. The key must be registered on GitHub so that the tag is shown as
verified.

If the workflow fails, fix the cause and use **Re-run failed jobs** on the run in the Actions tab.
An existing tag or release is kept. When the fix needs a code change, merge it in a new pull
request with the `release` label; the version stays the same.

## License

By contributing, you agree that your contributions are licensed under the [MIT License](LICENSE).
