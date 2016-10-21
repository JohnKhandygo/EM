var address = "http://localhost:8080/application";

var session

$(document).ready(function() {
  var appBar = $("div.app-bar")
  var grid = $("div.grid")
  var loginForm = $("div#login-form")

  $("body").css("background-color", "#008080")
  $(appBar).hide()
  $(grid).hide()
  $(loginForm).show()

  $(loginForm).find("button#login-button").click(function(event) {
    event.preventDefault();

    var userLogin = $(loginForm).find("input#user-login").val()
    var userPassword = $(loginForm).find("input#user-password").val()

    $.post({
      url: address + "/api/auth",
      data: {
        login: userLogin,
        password: userPassword
      },
      success: function(d) {
        session = d.session

        $("body").css("background-color", "#ffffff")
        $(appBar).show()
        $(grid).show()
        $(loginForm).hide()

        $(homeSection).click()
      }
    })
  })

  var contentDiv = $("div#content")

  var homeSection = $("li#home")
  var stuffSection = $("li#stuff")
  var proposalsSection = $("li#proposals")
  var newsSection = $("li#news")

  $(homeSection).click(function() {
    $(contentDiv).empty()

    $(stuffSection).removeClass("active")
    $(proposalsSection).removeClass("active")
    $(newsSection).removeClass("active")
    $(homeSection).addClass("active")

    $(contentDiv).append(wrapWithTag("div", "", "row cells2",
      [
        wrapWithTag("div", "awards", "cell padding20", [wrapWithTag("h3", "", "", "PAYMENTS")]),
        wrapWithTag("div", "schedule", "cell padding20", [wrapWithTag("h3", "", "", "SCHEDULE")])
      ]))

    $.ajax({
        url: address + "/api/awards/approved",
        method: "GET",
        contentType: "application/json",
        headers: {
          session_id: session
        },
        success: function(d) {
          for (var i = 0; i < d.length; ++i) {
            $("div#awards").append(buildAwardView(d[i]))
          }
        }
      })

    $.ajax({
        url: address + "/api/out_of_offices/approved",
        method: "GET",
        contentType: "application/json",
        headers: {
          session_id: session
        },
        success: function(d) {
          for (var i = 0; i < d.length; ++i) {
            $("div#schedule").append(buildOutOfOfficeView(d[i]))
            var buttons = $("div#schedule").find("button.button")
            var lastAddedButton = buttons[buttons.length - 1];
            $(lastAddedButton).click(function(event) {
              var parentView = $(this).closest("div.out-of-office-item")
              var id = $(parentView).attr("id")
              $.ajax({
                url: address + "/api/out_of_offices/" + id + "/cancel",
                method: "DELETE",
                contentType: "application/json",
                headers: {
                  session_id: session
                },
                success: function(r) {
                  //alert(JSON.stringify(r));
                  $(parentView).remove();
                }
              })
            })
          }
        }
      })

    $.ajax({
        url: address + "/api/vocations/approved",
        method: "GET",
        contentType: "application/json",
        headers: {
          session_id: session
        },
        success: function(d) {
          for (var i = 0; i < d.length; ++i) {
            $("div#schedule").append(buildVocationView(d[i]))
          }
        }
      })
  })

  $(stuffSection).click(function() {
    $(contentDiv).empty()

    $(homeSection).removeClass("active")
    $(proposalsSection).removeClass("active")
    $(newsSection).removeClass("active")
    $(stuffSection).addClass("active")

    $(contentDiv).append(wrapWithTag("div", "", "row cells2",
      [
        wrapWithTag("div", "team", "cell padding20", [wrapWithTag("h3", "", "", "TEAM")]),
        wrapWithTag("div", "patronaged", "cell padding20",
          [wrapWithTag("h3", "", "", "PATRONAGED")])
      ]))

    $.get({
      url: address + "/api/employees/manager",
      contentType: "application/json",
      headers: {
        session_id: session
      },
      success: function(d) {
        if (d.type == ".employee") {
          //$("div#team").append(wrapWithTag("h4", "", "", "Manager"))
          $("div#team").append(buildManagerView(d))
        }
      }
    })

    $.ajax({
      url: address + "/api/employees/paymaster",
      method: "GET",
      contentType: "application/json",
      headers: {
        session_id: session
      },
      success: function(d) {
        if (d.type == ".employee") {
          //$("div#team").append(wrapWithTag("h4", "", "", "Manager"))
          $("div#team").append(buildPaymasterView(d))
        }
      }
    })

    $.ajax({
      url: address + "/api/employees/teammates",
      method: "GET",
      contentType: "application/json",
      headers: {
        session_id: session
      },
      success: function(d) {
        for(var i = 0; i < d.length; ++i) {
          //$("div#team").append(wrapWithTag("h4", "", "", "Manager"))
          $("div#team").append(buildTeammateView(d[i]))
        }
      }
    })

    $.get({
      url: address + "/api/employees/patronaged",
      contentType: "application/json",
      headers: {
        session_id: session
      },
      success: function(d) {
        for(var i = 0; i < d.length; ++i) {
          $("div#patronaged").append(buildPatronagedEmployeeView(d[i]))
          var buttons = $("div#patronaged").find("button.award-button")
          var lastAddedButton = buttons[buttons.length - 1];
          $(lastAddedButton).click(function(event) {
            var parentView = $(this).closest("div.patronaged-item")[0]
            var id = parseInt($(parentView).attr("id"))
            var awardForm = $(parentView).find("div.award-form")[0];
            if ($(awardForm).css("display") == "none") {
              //$(awardForm).css("display", "block")
              $(awardForm).show(1000)
            } else {
              var awardAmountField = $(awardForm).find("input[name=\"award-amount\"]")[0];
              var awardAmountText = $(awardAmountField).val();
              var awardAmount = parseInt(awardAmountText);
              var awardDateField = $(awardForm).find("input[name=\"award-date\"]")[0];
              var awardDateText = $(awardDateField).val();
              var awardDate = toMilliseconds(awardDateText)
              $.post({
                url: address + "/api/awards/propose",
                headers: {
                  session_id: session
                },
                data: {
                  employee_id : id,
                  when : awardDate,
                  amount : awardAmount
                },
                success: function(d) {
                  $(awardAmountField).val("")
                  $(awardDateField  ).val("")
                }
              })
              //$(awardForm).css("display", "none")
              $(awardForm).hide(800)
            }
          })
        }
      }
    })
  })

  $(proposalsSection).click(function() {
    $(contentDiv).empty()

    $(homeSection).removeClass("active")
    $(stuffSection).removeClass("active")
    $(newsSection).removeClass("active")
    $(proposalsSection).addClass("active")

    $(contentDiv).append(wrapWithTag("div", "", "row cells2",
      [
        wrapWithTag("div", "inbox", "cell padding20", [wrapWithTag("h3", "", "", "INBOX")]),
        wrapWithTag("div", "outbox", "cell padding20", [wrapWithTag("h3", "", "", "OUTBOX")])
      ]))

    $.get({
      url: address + "/api/awards/pending/inbox",
      contentType: "application/json",
      headers: {
        session_id: session
      },
      success: function(d) {
        for (var i = 0; i < d.length; ++i) {
          $("div#inbox").append(buildAwardsInboxView(d[i]))

          var approveButtons = $("div#inbox").find("button.approve-button")
          var lastAddedApproveButton = approveButtons[approveButtons.length - 1];
          $(lastAddedApproveButton).click(function(event) {
            var parentView = $(this).closest("div.award-inbox-item")[0]
            var id = parseInt($(parentView).attr("id"))
            $.post({
              url: address + "/api/awards/" + id + "/approve",
              headers: {
                session_id: session
              },
              success: function(d) {
                //alert(JSON.stringify(d))
                $(parentView).remove()
              }
            })
          })

          var rejectButtons = $("div#inbox").find("button.reject-button")
          var lastAddedRejectButton = rejectButtons[rejectButtons.length - 1];
          $(lastAddedRejectButton).click(function(event) {
            var parentView = $(this).closest("div.award-inbox-item")[0]
            var id = parseInt($(parentView).attr("id"))
            $.post({
              url: address + "/api/awards/" + id + "/reject",
              headers: {
                session_id: session
              },
              success: function(d) {
                //alert(JSON.stringify(d))
                $(parentView).remove()
              }
            })
          })

        }
      }
    })

    $.get({
      url: address + "/api/awards/pending/outbox",
      headers: {
        session_id: session
      },
      success: function(d) {
        for (var i = 0; i < d.length; ++i) {
          $("div#outbox").append(buildAwardsOutboxView(d[i]))

          var cancelButtons = $("div#outbox").find("button.cancel-button")
          var lastAddedCancelButton = cancelButtons[cancelButtons.length - 1];
          $(lastAddedCancelButton).click(function(event) {
            var parentView = $(this).closest("div.award-outbox-item")[0]
            var id = parseInt($(parentView).attr("id"))
            $.ajax({
              url: address + "/api/awards/" + id + "/cancel",
              method : "DELETE",
              headers: {
                session_id: session
              },
              success: function(d) {
                //alert(JSON.stringify(d))
                $(parentView).remove()
              }
            })
          })

        }
      }
    })

    $.get({
      url: address + "/api/vocations/pending/inbox",
      headers: {
        session_id: session
      },
      success: function(d) {
        for (var i = 0; i < d.length; ++i) {
          $("div#inbox").append(buildVocationInboxView(d[i]))

          var approveButtons = $("div#inbox").find("button.approve-button")
          var lastAddedApproveButton = approveButtons[approveButtons.length - 1];
          $(lastAddedApproveButton).click(function(event) {
            var parentView = $(this).closest("div.vocation-inbox-item")[0]
            var id = parseInt($(parentView).attr("id"))
            $.post({
              url: address + "/api/vocations/" + id + "/approve",
              headers: {
                session_id: session
              },
              success: function(d) {
                //alert(JSON.stringify(d))
                $(parentView).remove()
              }
            })
          })

          var rejectButtons = $("div#inbox").find("button.reject-button")
          var lastAddedRejectButton = rejectButtons[rejectButtons.length - 1];
          $(lastAddedRejectButton).click(function(event) {
            var parentView = $(this).closest("div.vocation-inbox-item")[0]
            var id = parseInt($(parentView).attr("id"))
            $.post({
              url: address + "/api/vocations/" + id + "/reject",
              headers: {
                session_id: session
              },
              success: function(d) {
                //alert(JSON.stringify(d))
                $(parentView).remove()
              }
            })
          })

        }
      }
    })

    $.get({
      url: address + "/api/vocations/pending/outbox",
      headers: {
        session_id: session
      },
      success: function(d) {
        for (var i = 0; i < d.length; ++i) {
          $("div#outbox").append(buildVocationOutboxView(d[i]))

          var cancelButtons = $("div#outbox").find("button.cancel-button")
          var lastAddedCancelButton = cancelButtons[cancelButtons.length - 1];
          $(lastAddedCancelButton).click(function(event) {
            var parentView = $(this).closest("div.vocation-outbox-item")[0]
            var id = parseInt($(parentView).attr("id"))
            $.ajax({
              url: address + "/api/vocations/" + id + "/cancel",
              method : "DELETE",
              headers: {
                session_id: session
              },
              success: function(d) {
                //alert(JSON.stringify(d))
                $(parentView).remove()
              }
            })
          })

        }
      }
    })
  })

  $("button#propose-vocation-button").click(function() {
    var proposeVocationForm = $("div#propose-vocation-form")

    var sinceText = $("input#propose-vocation-since").val()
    var tillText = $("input#propose-vocation-till").val()

    if (sinceText != "" && tillText != "") {
      var since = toMilliseconds(sinceText)
      var till = toMilliseconds(tillText)
      var duration = till - since

      $.post({
        url: address + "/api/vocations/propose",
        headers: {
          session_id: session
        },
        data: {
          when: since,
          duration: duration
        },
        success: function(d) {
          $("div#propose-vocation-dialog").data("dialog").close()
          $("input#propose-vocation-since").val("")
          $("input#propose-vocation-till").val("")

          $.Notify({
            caption: "Notification",
            content: "Vocation proposed successfully. Wait for response from your manager.",
            type: "success",
            timeout: 5000
          });

          if ($(homeSection).hasClass("active")) {
              $(homeSection).click()
          }
        },
        error: function(xhr, textStatus, errorThrown) {
          $.Notify({
            caption: "Notification",
            content: "Fail to propose vocation. Check data input.",
            type: "alert",
            timeout: 5000
          });
        }
      })
    }
  })

  $("button#reserve-out-of-office-button").click(function() {
    var reserveOutOfOfficeForm = $("div#reserve-out-of-office-form")

    var sinceText = $("input#out-of-office-since").val()
    var tillText = $("input#out-of-office-till").val()
    var reason = $("textarea#out-of-office-reason").val()

    if (sinceText != "" && tillText != "" && reason != "") {
      var since = toMilliseconds(sinceText)
      var till = toMilliseconds(tillText) + 24 * 60 * 60 * 1000
      var duration = till - since
      $.post({
        url: address + "/api/out_of_offices/create",
        headers: {
          session_id: session
        },
        data: {
          when: since,
          duration: duration,
          reason: reason
        },
        success: function(d) {
          $("div#reserve-out-of-office-dialog").data("dialog").close()
          $("input#out-of-office-since").val("")
          $("input#out-of-office-till").val("")
          $("textarea#out-of-office-reason").val("")

          $.Notify({
            caption: "Notification",
            content: "Out of office reserved successfully.",
            type: "success",
            timeout: 5000
          });
          if ($(homeSection).hasClass("active")) {
            $(homeSection).click()
          }
        },
        error: function(xhr, textStatus, errorThrown) {
          $.Notify({
            caption: "Notification",
            content: "Fail to reserve out of office. Check data input.",
            type: "alert",
            timeout: 5000
          })
        }
      })
    }

  })

  $(newsSection).click(function() {
    $(contentDiv).empty()

    $(homeSection).removeClass("active")
    $(stuffSection).removeClass("active")
    $(proposalsSection).removeClass("active")
    $(newsSection).addClass("active")

    $(contentDiv).append(
      wrapWithTag("div", "news", "cell padding20", [wrapWithTag("h3", "", "", ["NEWS"])])
    )

    $.get({
      url: address + "/api/news",
      headers: {
        session_id: session
      },
      success: function(d) {
        //alert(JSON.stringify(d))
        for (var i = 0; i < d.news_items.length; ++i) {
          $("div#news").append(buildNewsItemView(d.news_items[i]))
        }
      }
    })

  })

})