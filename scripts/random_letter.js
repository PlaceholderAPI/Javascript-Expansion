function randomLetter() {
  var start = "A".charCodeAt(0);
  var random = Math.random() * 26;

  return String.fromCharCode(start + Math.floor(random));
}
randomLetter();