function toHumanReadableDate(timestampMs) {
  var date = new Date(timestampMs);
  var year = date.getFullYear();
  var month = "0" + (date.getMonth() + 1);
  var day = "0" + date.getDate();
  var formattedTime = day.substr(-2) + '.' + month.substr(-2) + "." + year;
  return formattedTime;
}

function toHumanReadableTime(timestampMs) {
  var date = new Date(timestampMs);
  var year = date.getFullYear();
  var month = "0" + (date.getMonth() + 1);
  var day = "0" + date.getDate();
  var hour = "0" + date.getHours();
  var minutes = "0" + date.getMinutes();
  var formattedDate = day.substr(-2) + '.' + month.substr(-2) + "." + year;
  var formattedTime = hour.substr(-2) + ':' + minutes.substr(-2)
  return formattedDate + " " + formattedTime;
}

function toMilliseconds(dateString) {
  var match = /^(\d{4})\.(\d{2})\.(\d{2})/.exec(dateString);
  var day = Number(match[3])
  var month = Number(match[2]) - 1
  var year = Number(match[1])
  return new Date(year, month, day, 0, 0, 0).getTime();
}