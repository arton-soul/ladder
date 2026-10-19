package com.dearyoti.lexiladder.tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GenerateStoreAssets {

    public static void main(String[] args) {
        File outputDir = new File("Doc/store_listing/assets");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        try {
            generateAppIcon(new File(outputDir, "icon_512x512.png"));
            generateFeatureGraphic(new File(outputDir, "feature_graphic_1024x500.png"));
            System.out.println("ALL_ASSETS_GENERATED_SUCCESSFULLY");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void generateAppIcon(File outputFile) throws IOException {
        int size = 512;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 1. Squircle background (480x480 with 16px padding)
        int pad = 18;
        int rectSize = size - (pad * 2);
        int cornerRadius = 110;
        RoundRectangle2D bgShape = new RoundRectangle2D.Float(pad, pad, rectSize, rectSize, cornerRadius, cornerRadius);

        // Gradient: Vibrant Sky Blue to Deep Midnight Royal Blue
        GradientPaint bgGradient = new GradientPaint(
                pad, pad, new Color(0x19, 0x76, 0xD2),
                pad + rectSize, pad + rectSize, new Color(0x0A, 0x2A, 0x5C)
        );
        g.setPaint(bgGradient);
        g.fill(bgShape);

        // Inner glowing border
        g.setColor(new Color(255, 255, 255, 45));
        g.setStroke(new BasicStroke(3.5f));
        g.draw(bgShape);

        // Subtle top light sheen (glossy curve)
        Area sheenArea = new Area(bgShape);
        Ellipse2D sheenEllipse = new Ellipse2D.Float(-60, -180, 632, 400);
        sheenArea.intersect(new Area(sheenEllipse));
        GradientPaint sheenPaint = new GradientPaint(
                0, pad, new Color(255, 255, 255, 38),
                0, 220, new Color(255, 255, 255, 0)
        );
        g.setPaint(sheenPaint);
        g.fill(sheenArea);

        // 2. Ladder Shadow (Soft drop shadow)
        g.setColor(new Color(5, 18, 42, 110));
        float shadowOffset = 10f;
        BasicStroke shadowStroke = new BasicStroke(28f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g.setStroke(shadowStroke);

        float leftX = 175f;
        float rightX = 337f;
        float topY = 125f;
        float botY = 415f;

        // Shadow rails
        g.draw(new Line2D.Float(leftX, topY + shadowOffset, leftX, botY + shadowOffset));
        g.draw(new Line2D.Float(rightX, topY + shadowOffset, rightX, botY + shadowOffset));

        // Shadow rungs
        float[] rungYs = { 180f, 245f, 310f, 375f };
        for (float ry : rungYs) {
            g.draw(new Line2D.Float(leftX, ry + shadowOffset, rightX, ry + shadowOffset));
        }

        // 3. Pristine White Ladder
        g.setColor(Color.WHITE);
        BasicStroke ladderStroke = new BasicStroke(26f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g.setStroke(ladderStroke);

        // Rails
        g.draw(new Line2D.Float(leftX, topY, leftX, botY));
        g.draw(new Line2D.Float(rightX, topY, rightX, botY));

        // Rungs
        for (float ry : rungYs) {
            g.draw(new Line2D.Float(leftX, ry, rightX, ry));
        }

        // 4. Golden Star / Trophy at top of ladder
        drawStar(g, 256f, 98f, 32f, 15f, new Color(0xFF, 0xD5, 0x4F), new Color(0xFF, 0xA0, 0x00));

        g.dispose();
        ImageIO.write(img, "png", outputFile);
        System.out.println("Generated App Icon: " + outputFile.getAbsolutePath() + " (" + size + "x" + size + ")");
    }

    private static void drawStar(Graphics2D g, float cx, float cy, float rOuter, float rInner, Color fillColor, Color strokeColor) {
        Path2D.Float star = new Path2D.Float();
        double angle = -Math.PI / 2.0;
        double step = Math.PI / 5.0;

        for (int i = 0; i < 10; i++) {
            float r = (i % 2 == 0) ? rOuter : rInner;
            float x = cx + (float) (Math.cos(angle) * r);
            float y = cy + (float) (Math.sin(angle) * r);
            if (i == 0) {
                star.moveTo(x, y);
            } else {
                star.lineTo(x, y);
            }
            angle += step;
        }
        star.closePath();

        // Soft star shadow
        g.setColor(new Color(0, 0, 0, 70));
        AffineTransform tx = AffineTransform.getTranslateInstance(0, 4);
        g.fill(star.createTransformedShape(tx));

        g.setColor(fillColor);
        g.fill(star);
        g.setColor(strokeColor);
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(star);
    }

    private static void generateFeatureGraphic(File outputFile) throws IOException {
        int width = 1024;
        int height = 500;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 1. Background gradient (Modern Deep Sapphire Navy)
        GradientPaint bgGradient = new GradientPaint(
                0, 0, new Color(0x0A, 0x16, 0x28),
                width, height, new Color(0x0D, 0x3B, 0x66)
        );
        g.setPaint(bgGradient);
        g.fillRect(0, 0, width, height);

        // Background geometric accents / decorative ladder steps
        g.setColor(new Color(255, 255, 255, 10));
        g.setStroke(new BasicStroke(2f));
        for (int i = 0; i < 8; i++) {
            int y = 60 + (i * 55);
            g.drawLine(500, y, 1000, y - 60);
        }

        // Ambient radial glow behind the mock card on right
        RadialGradientPaint glow = new RadialGradientPaint(
                new Point2D.Float(780, 240), 280,
                new float[]{ 0.0f, 0.6f, 1.0f },
                new Color[]{ new Color(0x19, 0x76, 0xD2, 110), new Color(0x19, 0x76, 0xD2, 35), new Color(0x0A, 0x16, 0x28, 0) }
        );
        g.setPaint(glow);
        g.fill(new Rectangle2D.Float(500, 0, 524, height));

        // 2. Left Side: Brand Typography & Value Props
        // Mini Pill Tag
        int pillX = 64;
        int pillY = 56;
        RoundRectangle2D tagPill = new RoundRectangle2D.Float(pillX, pillY, 240, 32, 16, 16);
        g.setColor(new Color(0x19, 0x76, 0xD2, 80));
        g.fill(tagPill);
        g.setColor(new Color(0x64, 0xB5, 0xF6));
        g.setStroke(new BasicStroke(1.5f));
        g.draw(tagPill);

        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("CEFR LEVEL-BY-LEVEL", pillX + 28, pillY + 21);

        // App Title
        g.setFont(new Font("SansSerif", Font.BOLD, 64));
        // Soft title shadow
        g.setColor(new Color(0, 0, 0, 90));
        g.drawString("LexiLadder", 64 + 3, 160 + 3);
        // Title white
        g.setColor(Color.WHITE);
        g.drawString("LexiLadder", 64, 160);

        // Subtitle
        g.setFont(new Font("SansSerif", Font.PLAIN, 23));
        g.setColor(new Color(0xBB, 0xDE, 0xFB));
        g.drawString("Master English Vocabulary, Step by Step", 66, 202);

        // Tagline description
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g.setColor(new Color(0x90, 0xCA, 0xF9));
        g.drawString("Built-in Spaced Repetition (SM-2) with Level Mastery Tests", 66, 236);

        // Feature Badges (3 horizontal/stacked pills)
        int badgeStartY = 282;
        int badgeHeight = 44;
        int badgeSpacing = 16;

        String[] badges = {
                "⚡  100% Offline-First",
                "🧠  SM-2 Spaced Repetition",
                "🏆  Level Mastery Tests & Badges"
        };

        for (int i = 0; i < badges.length; i++) {
            int by = badgeStartY + (i * (badgeHeight + badgeSpacing));
            RoundRectangle2D badgePill = new RoundRectangle2D.Float(66, by, 370, badgeHeight, 14, 14);

            g.setColor(new Color(255, 255, 255, 18));
            g.fill(badgePill);
            g.setColor(new Color(255, 255, 255, 45));
            g.setStroke(new BasicStroke(1.2f));
            g.draw(badgePill);

            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 15));
            g.drawString(badges[i], 86, by + 28);
        }

        // 3. Right Side: Interactive Vocabulary Card Mockup
        int cardX = 570;
        int cardY = 65;
        int cardW = 390;
        int cardH = 370;
        RoundRectangle2D cardShape = new RoundRectangle2D.Float(cardX, cardY, cardW, cardH, 24, 24);

        // Card Drop Shadow
        for (int s = 1; s <= 5; s++) {
            RoundRectangle2D cardShadow = new RoundRectangle2D.Float(cardX + (s * 2), cardY + (s * 4), cardW, cardH, 24, 24);
            g.setColor(new Color(0, 0, 0, 18));
            g.fill(cardShadow);
        }

        // Card Body (Clean pure surface with subtle gradient)
        GradientPaint cardPaint = new GradientPaint(
                cardX, cardY, new Color(0xFF, 0xFF, 0xFF),
                cardX, cardY + cardH, new Color(0xF4, 0xF7, 0xFA)
        );
        g.setPaint(cardPaint);
        g.fill(cardShape);
        g.setColor(new Color(0xBB, 0xDE, 0xFB));
        g.setStroke(new BasicStroke(2f));
        g.draw(cardShape);

        // Inside Card Content
        // Level pill inside card
        RoundRectangle2D innerPill = new RoundRectangle2D.Float(cardX + 24, cardY + 24, 150, 28, 14, 14);
        g.setColor(new Color(0xE3, 0xF2, 0xFD));
        g.fill(innerPill);
        g.setColor(new Color(0x19, 0x76, 0xD2));
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("LEVEL 1 • UNIT 1", cardX + 42, cardY + 43);

        // Word Text
        g.setColor(new Color(0x11, 0x18, 0x27));
        g.setFont(new Font("SansSerif", Font.BOLD, 36));
        g.drawString("Achieve", cardX + 24, cardY + 98);

        // Phonetic & POS
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g.setColor(new Color(0x19, 0x76, 0xD2));
        g.drawString("/əˈtʃiːv/ • verb", cardX + 24, cardY + 128);

        // Divider
        g.setColor(new Color(0xE5, 0xE7, 0xEB));
        g.drawLine(cardX + 24, cardY + 146, cardX + cardW - 24, cardY + 146);

        // English Definition
        g.setColor(new Color(0x37, 0x41, 0x51));
        g.setFont(new Font("SansSerif", Font.PLAIN, 15));
        g.drawString("To successfully complete something", cardX + 24, cardY + 175);
        g.drawString("with effort or skill.", cardX + 24, cardY + 198);

        // Indonesian Translation in highlighted box
        RoundRectangle2D transBox = new RoundRectangle2D.Float(cardX + 24, cardY + 218, cardW - 48, 48, 12, 12);
        g.setColor(new Color(0xEE, 0xF6, 0xFF));
        g.fill(transBox);
        g.setColor(new Color(0x0D, 0x47, 0xA1));
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("🇮🇩  Arti: Mencapai / Meraih", cardX + 40, cardY + 248);

        // Rating buttons row (Again, Hard, Good, Easy)
        int btnW = 75;
        int btnH = 36;
        int btnGap = 9;
        int btnStartX = cardX + 24;
        int btnY = cardY + 292;

        String[] ratings = { "Again", "Hard", "Good", "Easy" };
        Color[] ratingColors = {
                new Color(0xEF, 0x53, 0x50), // Red
                new Color(0xFF, 0x98, 0x00), // Orange
                new Color(0x4C, 0xAF, 0x50), // Green
                new Color(0x21, 0x96, 0xF3)  // Blue
        };

        for (int i = 0; i < ratings.length; i++) {
            int bx = btnStartX + (i * (btnW + btnGap));
            RoundRectangle2D btnShape = new RoundRectangle2D.Float(bx, btnY, btnW, btnH, 8, 8);
            g.setColor(new Color(ratingColors[i].getRed(), ratingColors[i].getGreen(), ratingColors[i].getBlue(), 26));
            g.fill(btnShape);
            g.setColor(ratingColors[i]);
            g.setStroke(new BasicStroke(1.2f));
            g.draw(btnShape);

            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            int strW = g.getFontMetrics().stringWidth(ratings[i]);
            g.drawString(ratings[i], bx + ((btnW - strW) / 2), btnY + 22);
        }

        // Floating Mastered 🏆 Ribbon on Top-Right of Card
        RoundRectangle2D ribbon = new RoundRectangle2D.Float(cardX + cardW - 130, cardY - 14, 140, 36, 18, 18);
        g.setColor(new Color(0xFE, 0xF3, 0xC7));
        g.fill(ribbon);
        g.setColor(new Color(0xF5, 0x9E, 0x0B));
        g.setStroke(new BasicStroke(1.5f));
        g.draw(ribbon);

        g.setColor(new Color(0xB4, 0x53, 0x09));
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("Mastered 🏆", cardX + cardW - 110, cardY + 10);

        g.dispose();
        ImageIO.write(img, "png", outputFile);
        System.out.println("Generated Feature Graphic: " + outputFile.getAbsolutePath() + " (" + width + "x" + height + ")");
    }
}
