
var min = 1;
var max = 25;

function randomInteger() {
  if (args.length == 2) {
    min = args[0];
    max = args[1];
  }

  var random = Math.random() * (max - min);
  random += min;

  return Math.floor(random);
}

randomInteger();