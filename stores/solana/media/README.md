# Solana dApp Store Media Assets

This directory contains media assets for the Solana dApp Store listing.

## Required Files

### App Icon
- **File:** `icon512.png`
- **Size:** 512x512 px
- **Format:** PNG
- **Purpose:** App icon displayed in the store

### Banner
- **File:** `banner.png`
- **Size:** Recommended 1920x1080 px
- **Format:** PNG
- **Purpose:** Featured banner image

### Screenshots
- **Files:** `slide1.2.png`, `slide2.2.png`, `slide3.2.png`, `slide4.2.png`, `slide5.2.png`
- **Size:** Phone screen resolution (e.g., 1080x2400 px)
- **Format:** PNG
- **Purpose:** App screenshots showing features

## Guidelines

- Use high-quality images (avoid compression artifacts)
- Ensure images accurately represent the app
- Follow Solana dApp Store content policies
- Update screenshots when UI changes significantly

## References

All media files are referenced in `stores/solana/release-notes-template.yaml`:

```yaml
app:
  media:
    - purpose: icon
      uri: ./media/icon512.png

release:
  media:
    - purpose: icon
      uri: ./media/icon512.png
    - purpose: banner
      uri: ./media/banner.png
    - purpose: screenshot
      uri: ./media/slide1.2.png
    - purpose: screenshot
      uri: ./media/slide2.2.png
    - purpose: screenshot
      uri: ./media/slide3.2.png
    - purpose: screenshot
      uri: ./media/slide4.2.png
    - purpose: screenshot
      uri: ./media/slide5.2.png
```

## Notes

- Media files are committed to the repository
- These files are used during `npx @solana-mobile/dapp-store-cli create release`
- Update media assets as needed for new releases
