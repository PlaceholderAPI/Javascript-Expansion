var player = BukkitPlayer;
var material = '%player_item_in_hand%';

function displayname() {
  if ( material !== 'AIR' ) {
    var has = player.getInventory().getItemInHand().getItemMeta().hasDisplayName();
    var name = player.getInventory().getItemInHand().getItemMeta().getDisplayName();
  }

  if ( material === 'AIR' ) {
    // return 'AIR' when you aren't holding an item (You can change it to whatever you want)
    return 'AIR';
  } else if ( has ) {
    return name;
  } else {
    // returns the material name (%player_item_in_hand%) when it an item doesn't has a display name
    // You can change it to whatever you want by replacing material with 'WHAT YOU WANT'
    return material;
  }
}
displayname();