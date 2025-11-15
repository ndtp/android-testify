#!/usr/bin/env bash
set -euo pipefail

# Usage: ./update_version.sh <old_version> <new_version>
# Example: ./update_version.sh 3.2.2 4.0.0

OLD_VERSION="$1"
NEW_VERSION="$2"

echo "Updating Testify version: $OLD_VERSION → $NEW_VERSION"
echo ""

# 1. Update plugin version and dependency references
echo "Updating Markdown files..."

find . -type f -name "*.md" ! -path "*/docs/node_modules/*" | while read -r file; do
  updated=false

  # Update plugin block: id("dev.testify") version "x.y.z"
  if grep -q 'id("dev.testify")' "$file"; then
    sed -i.bak -E \
      "s/(id\\(\"dev\\.testify\"\\) version \")${OLD_VERSION}(\" apply false)/\\1${NEW_VERSION}\\2/g" \
      "$file"
    updated=true
  fi

  # Update dependency lines: androidTestImplementation "dev.testify:<artifact>:x.y.z"
  if grep -q 'dev.testify:' "$file"; then
    sed -i.bak -E \
      "s/(dev\\.testify:[^\":]+:)${OLD_VERSION}/\\1${NEW_VERSION}/g" \
      "$file"
    updated=true
  fi

  if [ "$updated" = true ]; then
    rm -f "${file}.bak"
    echo "  Updated $file"
  else
    rm -f "${file}.bak" 2>/dev/null || true
  fi
done

# 2. Update CHANGELOG.md files
echo ""
echo "Updating CHANGELOG entries..."

find . -type f -name "CHANGELOG.md" ! -path "*/docs/node_modules/*" | while read -r changelog; do
  if ! grep -q "## ${NEW_VERSION}" "$changelog"; then
    tmpfile=$(mktemp)
    {
      echo "## ${NEW_VERSION}"
      echo ""
      echo "- TODO: Add changes here"
      echo ""
      echo "---"
      echo ""
      cat "$changelog"
    } > "$tmpfile"
    mv "$tmpfile" "$changelog"
    echo "  Added entry to $changelog"
  else
    echo "  Skipped $changelog (entry already exists)"
  fi
done

echo ""
echo "✅ All done!"
