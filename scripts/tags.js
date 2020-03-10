/*
A simple placeholder which formats DeluxeTags to better suit a chat format,
meaning it returns a space if the user has a tag equipped

Creating our tags variable which will return either a tag if the user has it equipped
or a blank string if they do not
*/
var tags = "%deluxetags_tag%";

/*
Creating our hasTags function which will return the correct value
*/
function hasTag()
{
  /*
  If our tags variable which we created above returns an empty string,
  we return an empty string back as the user does not have a tag equipped
  and if our variable isn't empty, we return a space after it to fix spacing
  in the chat format
  */
  if (tags == '')
  {
    return '';
  }
  return tags + ' ';
  
  /*
  A compacter way of defining if statements
  Checked value ? Boolean true : Boolean false
  return tags == '' ? '' : tags + ' ';
  */
}
/*
Calling the hasTag function
*/
hasTag();
    
