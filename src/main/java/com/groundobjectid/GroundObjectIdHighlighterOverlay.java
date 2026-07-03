package com.groundobjectid;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class GroundObjectIdHighlighterOverlay extends Overlay
{
    private final Client client;
    private final GroundObjectIdHighlighterPlugin plugin;
    private final GroundObjectIdHighlighterConfig config;

    @Inject
    public GroundObjectIdHighlighterOverlay(
            Client client,
            GroundObjectIdHighlighterPlugin plugin,
            GroundObjectIdHighlighterConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        for (GameObject object : plugin.getHighlightedGameObjects())
        {
            renderTileObject(graphics, object, object.getId());
        }

        for (GroundObject object : plugin.getHighlightedGroundObjects())
        {
            renderTileObject(graphics, object, object.getId());
        }

        return null;
    }

    private void renderTileObject(Graphics2D graphics, TileObject object, int id)
    {
        LocalPoint localPoint = object.getLocalLocation();

        if (localPoint == null)
        {
            return;
        }

        Polygon tilePolygon = Perspective.getCanvasTilePoly(client, localPoint);

        if (tilePolygon == null)
        {
            return;
        }

        Color color = getColorForId(id);
        Stroke oldStroke = graphics.getStroke();
        graphics.setStroke(new BasicStroke(config.borderWidth()));

        GroundObjectIdHighlighterConfig.RenderMode mode = config.renderMode();

        if (mode == GroundObjectIdHighlighterConfig.RenderMode.TILE_FILL
                || mode == GroundObjectIdHighlighterConfig.RenderMode.OBJECT_FILL)
        {
            graphics.setColor(color);
            graphics.fill(tilePolygon);
        }
        else if (mode == GroundObjectIdHighlighterConfig.RenderMode.TILE_OUTLINE
                || mode == GroundObjectIdHighlighterConfig.RenderMode.OBJECT_OUTLINE)
        {
            graphics.setColor(color);
            graphics.draw(tilePolygon);
        }
        else if (mode == GroundObjectIdHighlighterConfig.RenderMode.OBJECT_FILL_AND_OUTLINE)
        {
            graphics.setColor(color);
            graphics.fill(tilePolygon);

            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 255));
            graphics.draw(tilePolygon);
        }

        graphics.setStroke(oldStroke);

        if (config.showIdText())
        {
            Point textLocation = object.getCanvasTextLocation(graphics, "ID: " + id, 0);

            if (textLocation != null)
            {
                OverlayUtil.renderTextLocation(graphics, textLocation, "ID: " + id, color);
            }
        }
    }

    public boolean shouldHighlight(int objectId)
    {
        return parseIds(config.objectIds()).contains(objectId) && isInAllowedRegion();
    }

    private boolean isInAllowedRegion()
    {
        Set<Integer> allowedRegions = parseIds(config.regionIds());

        if (allowedRegions.isEmpty())
        {
            return true;
        }

        int[] currentRegions = client.getMapRegions();

        for (int region : currentRegions)
        {
            if (allowedRegions.contains(region))
            {
                return true;
            }
        }

        return false;
    }

    private Color getColorForId(int id)
    {
        Color baseColor = parseColorMap().getOrDefault(id, config.defaultColor());
        int alpha = config.pulse() ? getPulseAlpha() : config.opacity();

        return new Color(
                baseColor.getRed(),
                baseColor.getGreen(),
                baseColor.getBlue(),
                Math.max(0, Math.min(255, alpha))
        );
    }

    private int getPulseAlpha()
    {
        double wave = (Math.sin(System.currentTimeMillis() / 250.0) + 1.0) / 2.0;
        return 60 + (int) (wave * config.opacity());
    }

    private Map<Integer, Color> parseColorMap()
    {
        Map<Integer, Color> colorMap = new HashMap<>();
        String raw = config.idColors();

        if (raw == null || raw.trim().isEmpty())
        {
            return colorMap;
        }

        String[] entries = raw.split(",");

        for (String entry : entries)
        {
            String[] parts = entry.trim().split(":");

            if (parts.length != 2)
            {
                continue;
            }

            try
            {
                int id = Integer.parseInt(parts[0].trim());
                Color color = Color.decode(parts[1].trim());
                colorMap.put(id, color);
            }
            catch (Exception ignored)
            {
            }
        }

        return colorMap;
    }

    private static Set<Integer> parseIds(String rawIds)
    {
        if (rawIds == null || rawIds.trim().isEmpty())
        {
            return Collections.emptySet();
        }

        try
        {
            return Arrays.stream(rawIds.split(","))
                    .map(String::trim)
                    .filter(value -> !value.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
        }
        catch (NumberFormatException ex)
        {
            return Collections.emptySet();
        }
    }
}