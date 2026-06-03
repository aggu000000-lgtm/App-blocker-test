> [!IMPORTANT]
> **Developer Sync Note**: To maintain continuity across multiple chat sessions, you MUST update this document after every design change, color swap, asset addition, or animation physics adjustment.

# App UI & Design Specifications

To match a SwiftUI level of visual quality and feel, this application relies on clean layouts, high contrast typography, physical spring animations, and a rich frosted-glass aesthetic.

## 1. Color Palette

We use a high-end, immersive dark mode palette:
- **Background Base**: Deep obsidian purple (`0xFF090514`)
- **Neon Cyan (Accent 1)**: Bright, glowing electric cyan (`0xFF00E5FF`)
- **Neon Purple (Accent 2)**: Vibrant violet (`0xFFD500F9`)
- **Warm Coral (Accent 3)**: Soft peach-pink (`0xFFFF8A80`)
- **Glass Base**: High-translucency frosted white (`Color.White.copy(alpha = 0.08f)`)
- **Glass Border Light**: Semitransparent highlight (`Color.White.copy(alpha = 0.18f)`)
- **Glass Border Dark**: Faded boundary (`Color.White.copy(alpha = 0.02f)`)
- **Text Primary**: High-contrast pure white (`0xFFFFFFFF`)
- **Text Secondary**: Soft lavender gray (`0xFFB39DDB`)

## 2. Liquid Glass Overlay & Aurora Effect

The design mimics a floating physical pane over a dynamic fluid background:
1. **Aurora Background**: An infinite transition drawing three large, colored radial gradient blobs on a canvas. The blobs change size and float across the screen using organic sinus-offset movements.
2. **Dynamic Blur**: The background is covered with `Modifier.blur(40.dp)` to diffuse the intense primary colors into a soft, glowing, lava-lamp fluid movement.
3. **Glass Border Highlight**: Containers use a dual-gradient thin border (1.dp) from top-left (light highlight) to bottom-right (hidden edge) to mimic physical glass refraction.
4. **Content Layer**: Cards use `GlassmorphicCard` with an inner shadow and 16.dp corner radius to house elements sharp, bright, and legible.

## 3. Physical Motion Choreography

All transitions must feel instantaneous but organic:
- **Button Clicks**: Standard scale down on press (`0.92f`) and a spring bounce on release using a standard spring spec:
  ```kotlin
  spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessMedium
  )
  ```
- **List Scrolling**: App toggles bounce slightly when flipped.
- **Lock Screen Interception**: Slides up or fades in with a quick `tween(300, easing = FastOutSlowInEasing)`.
