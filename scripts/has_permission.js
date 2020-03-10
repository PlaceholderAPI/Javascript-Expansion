/*
A simple placeholder which checks if the user has a permission

First we create our permission variables which will give a boolean output whether or not
the user has the specified permission
*/
var haspermission = "%player_has_permission_permission.test%";

/*
Here we create a function called hasPermission (can be named whatever you'd like) 
which will do all the checks
*/
function hasPermission() {

  
/*
If the haspermission variable which we created above returns yes (true boolean)
the placeholder will return the bellow if statements value
*/
  if (haspermission === "yes") {
    return "&aYou have the Test permission!";
  }

/*
If the haspermission variable doesn't return the above state (true boolean), 
the user does not have the checked permission, so we return the bellow else statements value
*/
  else {
    return "&cYou don't have the Test permission!";
  }
}
/*
Here we call the hasPermission function so it runs
*/
hasPermission();
