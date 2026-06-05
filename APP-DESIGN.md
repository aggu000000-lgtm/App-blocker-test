# App UI & Design Specifications

To deliver a SwiftUI-level/Material Expressive visual quality and feel, the application uses a clean, minimalist light-theme layout, high-contrast typography, physics-based springs, and a polished 3D liquid glass overlay.

---

## 1. Color Palette

We use a high-end, clean light mode palette combined with vibrant organic gradients:
- **Background Base**: Pure pristine white (`0xFFFFFFFF`)
- **Charcoal Text (Primary)**: Deep charcoal-obsidian for maximum readability (`0xFF121214`)
- **Slate Text (Secondary)**: Soft slate gray for details (`0xFF6B7280`)
- **Organic 5-Color Gradient**:
  - Green: `0xFF2ECC71`
  - Yellow: `0xFFF1C40F`
  - Red: `0xFFE74C3C`
  - Pink: `0xFFFD79A8`
  - Magenta: `0xFFD946EF`
- **Glass Base**: High-translucency frosted white layer (`Color.White.copy(alpha = 0.05f)`)
- **Glass Border Light**: Semitransparent white bevel highlight (`Color.White.copy(alpha = 0.8f)`)
- **Glass Border Dark**: Subtle shadow border boundary (`Color.Black.copy(alpha = 0.12f)`)
- **Accent Cyan**: Sleek slate-cyan indicator (`0xFF00ADB5`)
- **Accent Purple**: Sleek violet highlight (`0xFF8B5CF6`)

---

## 2. Typography

We bundle the modern geometric sans-serif typeface **Outfit** to drive high-contrast, premium text styling:
- **Heading Styles**: Extra-bold and bold weights (`FontWeight.ExtraBold`, `FontWeight.Bold`) with tight letter spacing.
- **Body & Controls**: Medium and normal weights (`FontWeight.Medium`, `FontWeight.Normal`) for clean legibility.
- **Offline Reliability**: Loaded directly from local resources (`res/font/outfit_*.ttf`), ensuring fast offline drawing without depending on external Web APIs.

---

## 3. Irregular Gradient Canvas

The base background mimics paint blobs drifting behind a frosted window:
1. **White Canvas**: A solid white background base.
2. **Morphing Irregular Shapes**: Two dynamically morphing paths drawn on a Canvas. The vertices are computed in real time using modulated sine and cosine wave offsets to produce organic, non-circular shapes:
   $$r(\theta) = R_{\text{base}} + \sin(2\theta + \text{progress}) \cdot A_1 + \cos(3\theta - \text{progress} \cdot 0.8) \cdot A_2$$
3. **Fluid Blur**: The canvas is blurred with a high-radius `65.dp` Gaussian blur to diffuse the intense primary colors into a soft, drifting pastel glow.

---

## 4. Polished 3D Liquid Glassmorphism

The cards represent physical floating glass panes with tactile and specular highlights:
1. **3D Bevel Refraction**: A thin 1.dp border drawn with a linear gradient from top-left (bright refraction highlight) to bottom-right (subtle shadow fade-out).
2. **Specular Shine Reflection**: An animated diagonal specular band glides across the card surface, driven by an infinite progress transition. This simulates light reflecting off a polished glass surface:
   $$\text{Sweep Position} = w \cdot \text{progress} \quad (\text{from } -1.5 \text{ to } 2.5)$$
3. **Inner Rim Glow**: A thin white inner stroke adds a subtle highlighting layer just inside the border.
4. **Soft Drop Shadow**: A low-opacity spot shadow (`ambientColor = 0.05f`, `spotColor = 0.08f` black) creates a high 3D floating elevation look.
5. **Tactile Spring Physics**: Click interactions use bouncy spring scaling (`0.92f` on press) coupled with mechanical haptic vibrations to complete the physical feel.
