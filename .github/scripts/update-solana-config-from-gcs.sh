#!/bin/bash
# Script to update config.yaml downloaded from GCS
# This script merges:
#   - Media paths from repo (release-notes-template.yaml)
#   - Release notes from docs/whatsNew/WHATS_NEW_EN.md
#   - Version info from git tag
# Into the config.yaml downloaded from GCS

set -e

# Arguments
VERSION_NAME="${1}"         # e.g., "2.4.10"
VERSION_CODE="${2}"         # e.g., "1388"
CONFIG_FILE="${3}"          # Path to config.yaml (downloaded from GCS)
TEMPLATE_FILE="${4:-stores/solana/release-notes-template.yaml}"  # Media template
WHATS_NEW_FILE="${5:-docs/whatsNew/WHATS_NEW_EN.md}"  # Changelog file

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Updating Solana dApp Store config from GCS"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Version:      ${VERSION_NAME}"
echo "Build:        ${VERSION_CODE}"
echo "Config:       ${CONFIG_FILE}"
echo "Template:     ${TEMPLATE_FILE}"
echo "What's New:   ${WHATS_NEW_FILE}"
echo ""

# Verify required tools
if ! command -v yq &> /dev/null; then
  echo " Error: yq is required but not installed"
  echo "Install: https://github.com/mikefarah/yq"
  exit 1
fi

# Verify files exist
if [[ ! -f "${CONFIG_FILE}" ]]; then
  echo " Error: Config file not found: ${CONFIG_FILE}"
  exit 1
fi

if [[ ! -f "${TEMPLATE_FILE}" ]]; then
  echo "  Warning: Template file not found: ${TEMPLATE_FILE}"
  echo "Will skip media path updates"
  SKIP_MEDIA=true
else
  SKIP_MEDIA=false
fi

if [[ ! -f "${WHATS_NEW_FILE}" ]]; then
  echo "  Warning: What's New file not found: ${WHATS_NEW_FILE}"
  echo "Will use fallback release notes"
  SKIP_WHATS_NEW=true
else
  SKIP_WHATS_NEW=false
fi

# Backup config file
cp "${CONFIG_FILE}" "${CONFIG_FILE}.backup"
echo " Backup created: ${CONFIG_FILE}.backup"
echo ""

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# STEP 1: Update media paths from template
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

if [[ "${SKIP_MEDIA}" == "false" ]]; then
  echo "Step 1: Updating media paths from template..."

  # Extract media paths from template
  APP_ICON=$(yq eval '.media.app_icon' "${TEMPLATE_FILE}")
  RELEASE_ICON=$(yq eval '.media.release_icon' "${TEMPLATE_FILE}")
  BANNER=$(yq eval '.media.banner' "${TEMPLATE_FILE}")

  # Update app icon
  if [[ "${APP_ICON}" != "null" && -n "${APP_ICON}" ]]; then
    yq eval -i ".app.media[0].uri = \"${APP_ICON}\"" "${CONFIG_FILE}"
    echo "   Updated app.media[0].uri = ${APP_ICON}"
  fi

  # Update release icon
  if [[ "${RELEASE_ICON}" != "null" && -n "${RELEASE_ICON}" ]]; then
    yq eval -i ".release.media[0].uri = \"${RELEASE_ICON}\"" "${CONFIG_FILE}"
    echo "   Updated release.media[0].uri = ${RELEASE_ICON}"
  fi

  # Update banner
  if [[ "${BANNER}" != "null" && -n "${BANNER}" ]]; then
    yq eval -i ".release.media[1].uri = \"${BANNER}\"" "${CONFIG_FILE}"
    echo "   Updated release.media[1].uri = ${BANNER}"
  fi

  # Update screenshots
  SCREENSHOT_COUNT=$(yq eval '.media.screenshots | length' "${TEMPLATE_FILE}")
  if [[ "${SCREENSHOT_COUNT}" != "null" && "${SCREENSHOT_COUNT}" -gt 0 ]]; then
    for i in $(seq 0 $((SCREENSHOT_COUNT - 1))); do
      SCREENSHOT=$(yq eval ".media.screenshots[${i}]" "${TEMPLATE_FILE}")
      if [[ "${SCREENSHOT}" != "null" && -n "${SCREENSHOT}" ]]; then
        # Screenshots start at index 2 (after icon and banner)
        RELEASE_INDEX=$((i + 2))
        yq eval -i ".release.media[${RELEASE_INDEX}].uri = \"${SCREENSHOT}\"" "${CONFIG_FILE}"
        echo "   Updated release.media[${RELEASE_INDEX}].uri = ${SCREENSHOT}"
      fi
    done
  fi

  echo " Media paths updated"
  echo ""
else
  echo "  Skipping media path updates (template not found)"
  echo ""
fi

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# STEP 2: Extract release notes from WHATS_NEW_EN.md
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

echo "Step 2: Parsing release notes from ${WHATS_NEW_FILE}..."

extract_changelog_section() {
  local version_name=$1
  local version_code=$2
  local section=$3
  local whats_new_file=$4

  # Search for the version header: ## [VERSION_NAME (VERSION_CODE)] - DATE
  # Then extract the specific section (Added, Changed, Fixed)
  awk -v version="${version_name} (${version_code})" -v section="${section}" '
    BEGIN { in_version=0; in_section=0 }

    # Find the version header (matches ## [version] with optional date suffix)
    /^## \[/ {
      if (index($0, version) > 0) {
        in_version=1
      } else if (in_version) {
        # Hit next version, stop
        exit
      }
      next
    }

    # Check for section headers when in version
    in_version && /^### / {
      if (index($0, section) > 0) {
        in_section=1
      } else {
        in_section=0
      }
      next
    }

    # Print lines in the section, converting format
    in_version && in_section && /^- / {
      sub(/^- /, "* ")
      print
    }
  ' "${whats_new_file}"
}

ADDED_NOTES=""
CHANGED_NOTES=""
FIXED_NOTES=""

if [[ "${SKIP_WHATS_NEW}" == "false" ]]; then
  echo "  Looking for version: ${VERSION_NAME} (${VERSION_CODE})"

  ADDED_NOTES=$(extract_changelog_section "${VERSION_NAME}" "${VERSION_CODE}" "Added" "${WHATS_NEW_FILE}")
  CHANGED_NOTES=$(extract_changelog_section "${VERSION_NAME}" "${VERSION_CODE}" "Changed" "${WHATS_NEW_FILE}")
  FIXED_NOTES=$(extract_changelog_section "${VERSION_NAME}" "${VERSION_CODE}" "Fixed" "${WHATS_NEW_FILE}")
fi

# Fallback to defaults if empty
if [[ -z "${ADDED_NOTES}" || "${ADDED_NOTES}" == "" ]]; then
  ADDED_NOTES="* No new features"
fi

if [[ -z "${CHANGED_NOTES}" || "${CHANGED_NOTES}" == "" ]]; then
  CHANGED_NOTES="* No changes"
fi

if [[ -z "${FIXED_NOTES}" || "${FIXED_NOTES}" == "" ]]; then
  FIXED_NOTES="* No fixes"
fi

echo "  Added:   $(echo "${ADDED_NOTES}" | grep -c '\*' || echo 0) items"
echo "  Changed: $(echo "${CHANGED_NOTES}" | grep -c '\*' || echo 0) items"
echo "  Fixed:   $(echo "${FIXED_NOTES}" | grep -c '\*' || echo 0) items"
echo ""

# Build new_in_version content
NEW_IN_VERSION="Added:
${ADDED_NOTES}
Changed:
${CHANGED_NOTES}
Fixed:
${FIXED_NOTES}"

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# STEP 3: Update version fields and release notes
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

echo "Step 3: Updating version fields..."

# Update version and version_code
yq eval -i ".release.android_details.version = \"${VERSION_NAME}\"" "${CONFIG_FILE}"
yq eval -i ".release.android_details.version_code = ${VERSION_CODE}" "${CONFIG_FILE}"

# Update new_in_version using strenv
export NEW_IN_VERSION
yq eval -i ".release.catalog.en-US.new_in_version = strenv(NEW_IN_VERSION)" "${CONFIG_FILE}"

echo "   version = ${VERSION_NAME}"
echo "   version_code = ${VERSION_CODE}"
echo "   new_in_version = [parsed from release notes]"
echo ""

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# STEP 4: Verify updates
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

echo "Step 4: Verifying updates..."
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Updated fields:"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
yq eval '.release.android_details | pick(["version", "version_code"])' "${CONFIG_FILE}"
echo ""

if [[ "${SKIP_MEDIA}" == "false" ]]; then
  echo "Media paths:"
  yq eval '.app.media[0].uri' "${CONFIG_FILE}"
  yq eval '.release.media[] | .uri' "${CONFIG_FILE}" | head -3
  echo ""
fi

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo " Config update complete!"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "Updated fields:"
echo "  • release.android_details.version"
echo "  • release.android_details.version_code"
echo "  • release.catalog.en-US.new_in_version"
if [[ "${SKIP_MEDIA}" == "false" ]]; then
  echo "  • app.media[*].uri"
  echo "  • release.media[*].uri"
fi
echo ""
echo "Preserved (auto-generated by CLI):"
echo "  • release.address"
echo "  • lastSubmittedVersionOnChain.*"
echo "  • lastUpdatedVersionOnStore.*"
echo ""
echo "Preserved (private data from GCS):"
echo "  • publisher.*"
echo "  • app.address"
echo "  • app.urls"
echo "  • solana_mobile_dapp_publisher_portal.testing_instructions"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
