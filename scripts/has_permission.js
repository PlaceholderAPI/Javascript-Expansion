// create a variable and name it whatever you want like this
// and use the placeholder you want, i'm using this one
var haspermission = "%player_has_permission_permission.test%";

// create a function with the name you want
function permission() {

// if the haspermission variable that we created before returns yes (true boolean)
// the js placeholder will return what we set in the return down
  if (haspermission === "yes") {
    return "&aYou have the Test permission!";
  }

// if the haspermission variable wasnt true it will return what we set down
  else {
    return "&cYou don't have the Test permission!";
  }
}
// by this we are calling the function to run
permission();
