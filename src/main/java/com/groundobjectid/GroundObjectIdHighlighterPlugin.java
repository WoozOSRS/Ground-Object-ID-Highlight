package com.groundobjectid;

import com.google.inject.Provides;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.GroundObject;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GroundObjectDespawned;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
        name = "Ground Object ID Highlighter",
        description = "Highlights matching object IDs anywhere they appear in the current scene",
        tags = {"ground", "object", "id", "highlight", "overlay"}
)
public class GroundObjectIdHighlighterPlugin extends Plugin
{
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private GroundObjectIdHighlighterOverlay overlay;

    @Inject
    private GroundObjectIdHighlighterConfig config;

    private final Set<GameObject> highlightedGameObjects = new HashSet<>();
    private final Set<GroundObject> highlightedGroundObjects = new HashSet<>();

    @Override
    protected void startUp()
    {
        clearHighlightedObjects();
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        clearHighlightedObjects();
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        GameObject object = event.getGameObject();

        if (config.highlightGameObjects() && overlay.shouldHighlight(object.getId()))
        {
            highlightedGameObjects.add(object);
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event)
    {
        highlightedGameObjects.remove(event.getGameObject());
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event)
    {
        GroundObject object = event.getGroundObject();

        if (config.highlightGroundObjects() && overlay.shouldHighlight(object.getId()))
        {
            highlightedGroundObjects.add(object);
        }
    }

    @Subscribe
    public void onGroundObjectDespawned(GroundObjectDespawned event)
    {
        highlightedGroundObjects.remove(event.getGroundObject());
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() == GameState.LOADING
                || event.getGameState() == GameState.LOGIN_SCREEN
                || event.getGameState() == GameState.CONNECTION_LOST
                || event.getGameState() == GameState.HOPPING)
        {
            clearHighlightedObjects();
        }
    }

    private void clearHighlightedObjects()
    {
        highlightedGameObjects.clear();
        highlightedGroundObjects.clear();
    }

    public Set<GameObject> getHighlightedGameObjects()
    {
        return highlightedGameObjects;
    }

    public Set<GroundObject> getHighlightedGroundObjects()
    {
        return highlightedGroundObjects;
    }

    @Provides
    GroundObjectIdHighlighterConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(GroundObjectIdHighlighterConfig.class);
    }
}