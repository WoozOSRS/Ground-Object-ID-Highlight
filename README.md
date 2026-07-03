\# Ground Object ID Highlighter



A RuneLite plugin that automatically highlights every matching ground or world object with a configured object ID.



Unlike RuneLite's built-in Object Markers plugin, this plugin does \*\*not\*\* save highlights to a specific tile. Instead, it highlights \*\*every object with a matching ID\*\* wherever it appears in the current scene.



This makes it useful for dynamically spawned boss mechanics and temporary encounter objects.



\## Features



\- Highlight objects by ID

\- Supports multiple object IDs

\- Per-object color customization

\- Adjustable opacity

\- Tile fill, tile outline, object fill, and object outline rendering modes

\- Adjustable border width

\- Optional object ID labels

\- Optional region restrictions

\- Optional pulsing highlights

\- Automatically removes highlights when objects despawn or the instance changes



\## Example Uses



\- Theatre of Blood

&#x20;   - Xarpus Exhumes

\- Colosseum/Maggot King

&#x20;   - Poison splats

\- Custom PvM mechanics

\- Temporary encounter objects



\## Configuration



\### Object IDs



Comma-separated list of object IDs.



Example:



33423,33424



\### ID Colors



Assign specific colors to specific IDs.



Example:



33423:#00FFFF,33424:#FF0000



\### Region Restriction



Optionally limit highlighting to one or more map regions.



Example:



13123



Leave blank to highlight matching IDs everywhere.



\## Installation



Available through the RuneLite Plugin Hub after approval.



\## License



BSD 2-Clause License

