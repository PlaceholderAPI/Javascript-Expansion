function time() {
  // year, month, day, hour, minute, second
  // months start at 0 and end at 11
  // January is 0. December is 11
  var startDate = new Date(2018, 0, 1);
  var endDate = new Date();
  var difference = endDate - startDate;

  var months = Math.floor(difference / (1000 * 60 * 60 * 24 * 31));
  var days = Math.floor(difference % (1000 * 60 * 60 * 24 * 31) / (1000 * 60 * 60 * 24));
  var hours   = Math.floor(difference % (1000 * 60 * 60 * 24) / (1000 * 60 * 60));
  var minutes = Math.floor(difference % (1000 * 60 * 60) / (1000 * 60));
  var seconds = Math.floor(difference % (1000 * 60) / (1000));

  // XXmo XXd XXh XXm XXs (changeable)
  return months + "mo "+ days + "d " + hours + "h " + minutes + "m " + seconds + "s";
}