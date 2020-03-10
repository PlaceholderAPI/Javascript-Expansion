var player = BukkitPlayer;

var mat;
var data = '0';
var amt = 1;
var name;
var lore;

var hasitemv = 'no';
var matchlores = 0;

function hasitem() {

  if ( args.length === 1 ) {
    mat = args[0].replace("mat: ", "");
  } else if ( args.length === 2 ) {
    mat = args[0].replace("mat: ", "");
    data = args[1].replace("data: ", "");
  } else if ( args.length === 3 ) {
    mat = args[0].replace("mat: ", "");
    data = args[1].replace("data: ", "");
    amt = args[2].replace("amt: ", "");
  } else if ( args.length === 4 ) {
    mat = args[0].replace("mat: ", "");
    data = args[1].replace("data: ", "");
    amt = args[2].replace("amt: ", "");
    name = args[3].replace("name: ", "");
  } else if ( args.length === 5 ) {
    mat = args[0].replace("mat: ", "");
    data = args[1].replace("data: ", "");
    amt = args[2].replace("amt: ", "");
    name = args[3].replace("name: ", "");
    lore = args[4].replace("lore: ", "").split("|");
  }

  var invItems = player.getInventory().getContents();

  for ( s = 0; s < invItems.length; s++ ) {
    if ( invItems[s] !== null ) {
      if ( lore !== undefined && invItems[s].getItemMeta().hasLore() === false ) {
        hasitemv = 'no';
      } else if ( lore !== undefined ) {
        if ( invItems[s].getType().toString() === mat || invItems[s].getTypeId() === parseInt(mat) ) {
          if ( invItems[s].getData().toString().match(/\d+/)[0] === data ) {
            if ( invItems[s].getAmount() >= parseInt(amt) ) {
              if ( invItems[s].getItemMeta().getDisplayName() === name ) {
                for ( l = 0; l < lore.length; l++ ) {
                  if ( invItems[s].getItemMeta().getLore()[l] === lore[l] ) {
                    matchlores++;
                  }
                }
              }
            }
          }
        }
        if ( matchlores === lore.length ) {
          hasitemv = 'yes';
        }
      } else if ( name !== undefined ) {
        if ( invItems[s].getType().toString() === mat || invItems[s].getTypeId() === parseInt(mat) ) {
          if ( invItems[s].getData().toString().match(/\d+/)[0] === data ) {
            if ( invItems[s].getAmount() >= parseInt(amt) ) {
              if ( invItems[s].getItemMeta().getDisplayName() === name ) {
                hasitemv = 'yes';
              }
            }
          }
        }
      } else if ( mat === undefined ) {
        return '&cInvalid syntax, Please use this syntax:\n &7%' + 'javascript_hasitem_mat: [MATERIAL/ID],data: [DATA],amt: [AMOUNT],name: [DISPLAYNAME],lore: [LORE]' + '%';
      } else {
        if ( invItems[s].getType().toString() === mat || invItems[s].getTypeId() === parseInt(mat) ) {
          if ( invItems[s].getData().toString().match(/\d+/)[0] === data ) {
            if ( invItems[s].getAmount() >= parseInt(amt) ) {
              hasitemv = 'yes';
            }
          }
        }
      }
    }
  }
  return hasitemv;
}

hasitem();