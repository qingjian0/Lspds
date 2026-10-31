# Vendor baseline

This directory pins the YunSeek source baseline as a Git submodule.

- Repository: https://github.com/ZW-SYS/Deekseep-Custom
- Commit: c315b82451d97e9668e2452992f1a0a2fd4a784a
- Version: YunSeek 1.8.1
- License: GPL-3.0

Initialize with:

```bash
git submodule update --init --recursive
```

The submodule is a source reference, not yet a compiled Lspds integration. Lspds keeps its own
applicationId, package metadata, build identity, and target scope. Do not edit the upstream
submodule in place; put integration changes in Lspds-owned adapter/source files or submit
upstream changes separately.

Before copying or modifying any file, preserve its copyright header and add its original path,
repository, commit, and license to the file-level provenance inventory.
