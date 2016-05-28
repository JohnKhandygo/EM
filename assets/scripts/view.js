function wrapWithTag(tag, idValue, classValue, contentArray) {
  var classArg = classValue == "" ? "" : "class=\"" + classValue + "\""
  var idArg = idValue == "" ? "" : "id=\"" + idValue + "\""
  var openTag = "<" + tag + " " + classArg + " " + idArg + ">"
  var content = ""
  for(var i = 0; i < contentArray.length; ++i) {
    content += contentArray[i]
  }
  var closeTag = "</" + tag + ">"
  return openTag + content + closeTag
}

function buildAwardView(award) {
  return wrapWithTag("div", "", "award-item row cells2",
        [
          wrapWithTag("div", "", "cell padding20",
            [wrapWithTag("div", "", "item-info align-left", [toHumanReadableDate(award.when)])]),
          wrapWithTag("div", "", "cell padding20",
            [wrapWithTag("div", "", "item-info align-right", [award.amount])]),
          wrapWithTag("div", "", "",
            [wrapWithTag("div", "", "item-type align-right", ["award"])])
        ])
}

function buildOutOfOfficeView(outOfOffice) {
  return wrapWithTag("div", outOfOffice.id, "out-of-office-item row cells5",
      [
        wrapWithTag("div", "", "cell colspan2 padding20",
          [wrapWithTag("div", "", "item-info align-left",
            [
              toHumanReadableTime(outOfOffice.when),
              " <br/> ",
              toHumanReadableTime(outOfOffice.when + outOfOffice.duration)
            ])]),
        wrapWithTag("div", "", "cell colspan3 padding20",
          [wrapWithTag("div", "", "item-info align-left", [outOfOffice.reason])]),
        wrapWithTag("div", "", "",
          [wrapWithTag("div", "", "item-type align-right",
            [
              wrapWithTag("span", "", "item-type v-align-bottom", ["out of office"]),
              wrapWithTag("button", "", "button danger", ["Cancel"])
            ])])
      ])
}

function buildVocationView(vocation) {
  return wrapWithTag("div", vocation.id, "vocation-item row cells5",
      [
        wrapWithTag("div", "", "cell colspan2 padding20",
          [wrapWithTag("div", "", "item-info align-left",
            [
              "since",
              " <br/> ",
              toHumanReadableDate(vocation.when)
            ])]),
        wrapWithTag("div", "", "cell colspan3 padding20",
          [wrapWithTag("div", "", "item-info align-right",
          [
            "till",
            " <br/> ",
            toHumanReadableDate(vocation.when + vocation.duration)
          ])]),
        wrapWithTag("div", "", "",
          [wrapWithTag("div", "", "item-type align-right", ["vocation"])])
      ])
}

function buildManagerView(manager) {
  return wrapWithTag("div", "", "manager-item row",
    [
      wrapWithTag("div", "", "cell padding20",
        [wrapWithTag("div", "", "item-info align-left", [manager.name])]),
      wrapWithTag("div", "", "",
        [wrapWithTag("div", "", "item-type align-right", ["manager"])])
    ])
}

function buildPaymasterView(paymaster) {
  return wrapWithTag("div", "", "paymaster-item row",
    [
      wrapWithTag("div", "", "cell padding20",
        [wrapWithTag("div", "", "item-info align-left", [paymaster.name])]),
      wrapWithTag("div", "", "",
        [wrapWithTag("div", "", "item-type align-right", ["paymaster"])])
    ])
}

function buildTeammateView(teammate) {
  return wrapWithTag("div", "", "teammate-item row",
    [
      wrapWithTag("div", "", "cell padding20",
        [wrapWithTag("div", "", "item-info align-left", [teammate.name])]),
      wrapWithTag("div", "", "",
        [wrapWithTag("div", "", "item-type align-right", ["teammate"])])
    ])
}

function buildPatronagedEmployeeView(patronaged) {
  return wrapWithTag("div", patronaged.id, "patronaged-item row",
    [
      wrapWithTag("div", "", "cell padding20",
        [wrapWithTag("div", "", "item-info align-left", [patronaged.name])]),
      wrapWithTag("div", "", "",
        [
          wrapWithTag("div", "", "award-form login-form padding20",
            [
              wrapWithTag("form", "", "",
                [
                  wrapWithTag("h4", "", "text-light", ["Award form"]),
                  wrapWithTag("hr", "", "thin", [""]),
                  wrapWithTag("br", "", "", [""]),
                  "<div class=\"align-left half-size\">",
                  "<div class=\"input-control text full-size\" data-role=\"input\">",
                  "<label class=\"text-light\" for=\"award-amount\">Award amount:</label>",
                  "<input type=\"text\" name=\"award-amount\">",
                  "<button class=\"button helper-button clear\" type=\"button\">",
                  wrapWithTag("span", "", "fa fa-times", [""]),
                  "</button>",
                  "</div>",
                  "</div>",
                  "<br>",
                  "<br>",
                  "<br>",
                  "<br>",
                  "<div class=\"align-right half-size\">",
                  "<div class=\"input-control text full-size\" data-role=\"datepicker\">",
                  "<label class=\"text-light\" for=\"award-date\">Award date:</label>",
                  "<input type=\"text\" name=\"award-date\">",
                  wrapWithTag("button", "", "button",
                    [wrapWithTag("span", "", "fa fa-calendar", [])]),
                  "</div>",
                  "</div>"
                ])
            ]),
          wrapWithTag("div", "", "item-type align-right",
           [
             wrapWithTag("span", "", "item-type v-align-bottom", ["patronaged"]),
             wrapWithTag("button", "", "award-button button success", ["Award"])
           ])
        ])
    ])
}

function buildAwardsInboxView(award) {
  return wrapWithTag("div", award.id, "award-inbox-item row cells2",
    [
      wrapWithTag("div", "", "cell padding20",
        [wrapWithTag("div", "", "item-info align-left", [toHumanReadableDate(award.when)])]),
      wrapWithTag("div", "", "cell padding20",
        [wrapWithTag("div", "", "item-info align-right", [award.amount])]),

      wrapWithTag("div", "", "",
        [
          wrapWithTag("div", "", "item-type align-right",
            [
              wrapWithTag("span", "", "item-type v-align-bottom", ["award"]),
              wrapWithTag("button", "", "approve-button button success", ["Approve"]),
              wrapWithTag("button", "", "reject-button button danger", ["Reject"])
            ])
        ])
    ])
}

function buildAwardsOutboxView(award) {
  return wrapWithTag("div", award.id, "award-outbox-item row cells2",
    [
      wrapWithTag("div", "", "cell padding20",
        [wrapWithTag("div", "", "item-info align-left", [toHumanReadableDate(award.when)])]),
      wrapWithTag("div", "", "cell padding20",
        [wrapWithTag("div", "", "item-info align-right", [award.amount])]),

      wrapWithTag("div", "", "",
        [
          wrapWithTag("div", "", "item-type align-right",
            [
              wrapWithTag("span", "", "item-type v-align-bottom", ["award"]),
              wrapWithTag("button", "", "cancel-button button danger", ["Cancel"])
            ])
        ])
    ])
}

function buildVocationInboxView(vocation) {
  return wrapWithTag("div", vocation.id, "vocation-inbox-item row cells2",
    [
      wrapWithTag("div", "", "cell padding20",
        [wrapWithTag("div", "", "item-info align-left",
          [
            "since",
            " <br/> ",
            toHumanReadableDate(vocation.when),
          ])]),
      wrapWithTag("div", "", "cell padding20",
        [wrapWithTag("div", "", "item-info align-right",
          [
            "till",
            "<br/>",
            toHumanReadableDate(vocation.when + vocation.duration)
          ])]),

      wrapWithTag("div", "", "",
        [
          wrapWithTag("div", "", "item-type align-right",
            [
              wrapWithTag("span", "", "item-type v-align-bottom", ["vocation"]),
              wrapWithTag("button", "", "approve-button button success", ["Approve"]),
              wrapWithTag("button", "", "reject-button button danger", ["Reject"])
            ])
        ])
    ])
}

function buildVocationOutboxView(vocation) {
  return wrapWithTag("div", vocation.id, "vocation-outbox-item row cells2",
    [
      wrapWithTag("div", "", "cell padding20",
        [wrapWithTag("div", "", "item-info align-left",
          [
            "since",
            "<br/>",
            toHumanReadableDate(vocation.when)
          ])]),
      wrapWithTag("div", "", "cell padding20",
        [wrapWithTag("div", "", "item-info align-right",
          [
            "till",
            "<br/>",
            toHumanReadableDate(vocation.when + vocation.duration)
          ])]),

      wrapWithTag("div", "", "",
        [
          wrapWithTag("div", "", "item-type align-right",
            [
              wrapWithTag("span", "", "item-type v-align-bottom", ["vocation"]),
              wrapWithTag("button", "", "cancel-button button danger", ["Cancel"])
            ])
        ])
    ])
}

function buildNewsItemView(d) {
  return wrapWithTag("div", "", "news-item row",
    [
      wrapWithTag("div", "", "cell padding20", [
        wrapWithTag("h3", "", "", [d.title]),
        wrapWithTag("p", "", "", [d.text])
      ]),

      wrapWithTag("div", "", "",
        [
          wrapWithTag("div", "", "item-type align-right",
          [
            wrapWithTag("span", "", "item-type v-align-bottom", [toHumanReadableDate(d.origin)]),
          ])
        ])
    ])
}