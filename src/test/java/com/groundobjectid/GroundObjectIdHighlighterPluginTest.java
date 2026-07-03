package com.groundobjectid;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class GroundObjectIdHighlighterPluginTest
{
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(GroundObjectIdHighlighterPlugin.class);
        RuneLite.main(args);
    }
}