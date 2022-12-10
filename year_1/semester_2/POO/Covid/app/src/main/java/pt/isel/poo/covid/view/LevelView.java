package pt.isel.poo.covid.view;

import java.util.List;

import pt.isel.poo.covid.model.Level;
import pt.isel.poo.covid.model.LevelElement;
import pt.isel.poo.covid.model.Location;

import pt.isel.poo.covid.tile.Tile;
import pt.isel.poo.covid.tile.TilePanel;

public class LevelView {

    private final TilePanel panel;
    private final Level level;

    public LevelView(TilePanel panel, Level level) {
        this.panel = panel;
        this.level = level;
        initialize();
        level.addChangeListener(new Level.ChangeListener() {
            @Override
            public void onChanged(List<Location> changedLocations) {
                for (Location location : changedLocations) {
                    updatePosition(location);
                }
            }
        });
        
    }


    private void initialize() {
        Location tempLoc = new Location(0, 0);

        for(tempLoc.x = 0 ; tempLoc.x < level.arenaWidth ; ++tempLoc.x) {
            for(tempLoc.y = 0 ; tempLoc.y < level.arenaHeight ; ++tempLoc.y) {
                updatePosition(tempLoc);
            }
        }
    }

    private void updatePosition(Location position) {
        final LevelElement element = level.getElementAt(position.x, position.y);
        Tile tile = null;
        if(element!=null) tile = element.tileType(panel.getContext());
        panel.setTile(position.x, position.y,tile);
    }
}
