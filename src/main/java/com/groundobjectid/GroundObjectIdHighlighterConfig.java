package com.groundobjectid;

import java.awt.Color;
import net.runelite.client.config.*;

@ConfigGroup("groundobjectidhighlighter")
public interface GroundObjectIdHighlighterConfig extends Config
{
    enum RenderMode
    {
        TILE_FILL,
        TILE_OUTLINE,
        OBJECT_FILL,
        OBJECT_OUTLINE,
        OBJECT_FILL_AND_OUTLINE
    }

    @ConfigItem(
            keyName = "objectIds",
            name = "Object IDs",
            description = "Comma-separated object IDs to highlight. Example: 33423,33424",
            position = 0
    )
    default String objectIds()
    {
        return "";
    }

    @ConfigItem(
            keyName = "idColors",
            name = "ID Colors",
            description = "Optional per-ID colors. Example: 33423:#00FFFF,33424:#FF0000",
            position = 1
    )
    default String idColors()
    {
        return "";
    }

    @Alpha
    @ConfigItem(
            keyName = "defaultColor",
            name = "Default Color",
            description = "Default highlight color",
            position = 2
    )
    default Color defaultColor()
    {
        return new Color(0, 255, 255, 120);
    }

    @Range(min = 0, max = 255)
    @ConfigItem(
            keyName = "opacity",
            name = "Opacity",
            description = "Overlay opacity from 0 to 255",
            position = 3
    )
    default int opacity()
    {
        return 120;
    }

    @ConfigItem(
            keyName = "renderMode",
            name = "Render Mode",
            description = "How matching objects should be highlighted",
            position = 4
    )
    default RenderMode renderMode()
    {
        return RenderMode.TILE_FILL;
    }

    @Range(min = 1, max = 10)
    @ConfigItem(
            keyName = "borderWidth",
            name = "Border Width",
            description = "Width of outline/border rendering",
            position = 5
    )
    default int borderWidth()
    {
        return 2;
    }

    @ConfigItem(
            keyName = "regionIds",
            name = "Only in Regions",
            description = "Optional comma-separated region IDs. Leave blank to highlight everywhere.",
            position = 6
    )
    default String regionIds()
    {
        return "";
    }

    @ConfigItem(
            keyName = "pulse",
            name = "Pulse Highlight",
            description = "Makes the overlay pulse visually",
            position = 7
    )
    default boolean pulse()
    {
        return false;
    }

    @ConfigItem(
            keyName = "showIdText",
            name = "Show ID Text",
            description = "Shows the matching object ID above the object",
            position = 8
    )
    default boolean showIdText()
    {
        return true;
    }

    @ConfigItem(
            keyName = "highlightGameObjects",
            name = "Highlight GameObjects",
            description = "Highlights matching GameObject IDs",
            position = 9
    )
    default boolean highlightGameObjects()
    {
        return true;
    }

    @ConfigItem(
            keyName = "highlightGroundObjects",
            name = "Highlight GroundObjects",
            description = "Highlights matching GroundObject IDs",
            position = 10
    )
    default boolean highlightGroundObjects()
    {
        return true;
    }
}