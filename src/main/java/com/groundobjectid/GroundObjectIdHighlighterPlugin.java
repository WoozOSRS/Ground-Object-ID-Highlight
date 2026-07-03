package com.groundobjectid;

import com.google.inject.Provides;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.GroundObject;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GroundObjectDespawned;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.api.events.MenuEntryAdded;
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
    private static final String CONFIG_GROUP = "groundobjectidhighlighter";
    private static final String CONFIG_KEY_OBJECT_IDS = "objectIds";
    private static final String MENU_ADD_OBJECT_ID = "Add object ID highlight";

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private GroundObjectIdHighlighterOverlay overlay;

    @Inject
    private GroundObjectIdHighlighterConfig config;

    @Inject
    private ConfigManager configManager;

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
    public void onMenuEntryAdded(MenuEntryAdded event)
    {
        if (!client.isKeyPressed(KeyEvent.VK_SHIFT))
        {
            return;
        }

        MenuEntry menuEntry = event.getMenuEntry();

        if (!isObjectMenuAction(menuEntry.getType()))
        {
            return;
        }

        int objectId = menuEntry.getIdentifier();

        client.createMenuEntry(-1)
                .setOption(MENU_ADD_OBJECT_ID)
                .setTarget(event.getTarget())
                .setIdentifier(objectId)
                .setType(MenuAction.RUNELITE)
                .onClick(this::addObjectIdFromMenu);
    }

    private void addObjectIdFromMenu(MenuEntry entry)
    {
        int objectId = entry.getIdentifier();
        Set<Integer> currentIds = overlay.parseConfiguredObjectIds();
        currentIds.add(objectId);

        String newConfigValue = currentIds.stream()
                .sorted()
                .map(String::valueOf)
                .reduce((a, b) -> a + "," + b)
                .orElse("");

        configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY_OBJECT_IDS, newConfigValue);
    }

    private boolean isObjectMenuAction(MenuAction action)
    {
        return action == MenuAction.GAME_OBJECT_FIRST_OPTION
                || action == MenuAction.GAME_OBJECT_SECOND_OPTION
                || action == MenuAction.GAME_OBJECT_THIRD_OPTION
                || action == MenuAction.GAME_OBJECT_FOURTH_OPTION
                || action == MenuAction.GAME_OBJECT_FIFTH_OPTION
                || action == MenuAction.EXAMINE_OBJECT;
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