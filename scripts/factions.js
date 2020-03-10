/*
A simple placeholder which will return brackets around the faction name if the user 
is in a faction, if they are not it will return nothing

Creating our faction variable which is the user's faction name (if the user is not in a faction it
will return the specified value in the factions config)
*/
var faction = "%factionsuuid_faction_name%";

/*
Creating our hasFaction function which will return the correct value
*/
function hasFaction()
{
  /* 
  If the faction variable returns an empty string, we return an empty string back,
  and if the faction variable isn't empty we return colored brackets around it
  */
  if (faction == '')
  {
    return '';
  }
  return '&8[&f' + faction + '&8]';
  
  /*
  A compacter way of defining if statements
  Checked value ? Boolean true : Boolean false
  return faction == '' ? '' : '&8[&f' + faction + '&8]';
  */
}
/*
Calling the hasFaction function
*/
hasFaction();
