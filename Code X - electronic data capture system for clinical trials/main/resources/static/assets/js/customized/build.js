function getUserRole(roleID) {
  // initialize check boxes for permissions
  $.each($("input[class='chk_user_permission']:checked"), function () {
    $(this).prop("checked", false);
  });

  // set check boxes
  if (mySession["check_role" + roleID].length > 0) {
    mySession["check_role" + roleID].split(",").forEach(function (element) {
      $("input[id=" + element + "]").prop("checked", true);
    });
  }

  // set 'lbl_permission' object text and value for sending data
  $("#lbl_permission").text($("#check_role" + roleID).val());
  $("#lbl_permission").data("rid", "check_role" + roleID);
}

function init_user_role_change_listener() {
  // initiate listeners of check boxes for roles
  $.each($("input[class='chk_user_role']"), function () {
    $(this).on("change", function () {
      setCheckedRoles();
    });
  });
}

function init_user_permission_change_listener() {
  // initiate listeners of check boxes for permissions
  $.each($("input[class='chk_user_permission']"), function () {
    $(this).on("change", function () {
      setCheckedPermissions();
    });
  });
}

function init_extra_user_role_change_listener() {
  $("#input_extra_role1").on("change", function () {
    mySession["input_extra_role1"] = $("#input_extra_role1").val();
  });

  $("#input_extra_role2").on("change", function () {
    mySession["input_extra_role2"] = $("#input_extra_role2").val();
  });

  $("#input_extra_role3").on("change", function () {
    mySession["input_extra_role3"] = $("#input_extra_role3").val();
  });
}

function setCheckedRoles() {
  $.each($("input[class='chk_user_role']:checked"), function () {
    mySession[$(this).attr("name")] = 1;
  });

  $.each($("input[class='chk_user_role']:not(:checked)"), function () {
    mySession[$(this).attr("name")] = 0;
  });
}

function setCheckedPermissions() {
  var permissions = [];
  $.each($("input[class='chk_user_permission']:checked"), function () {
    permissions.push($(this).attr("id"));
  });

  if ($("#lbl_permission").data("rid") != "") {
    mySession[$("#lbl_permission").data("rid")] = permissions.join(",");
  }
}

function clear_permissions() {
  $(".chk_user_permission").each(function () {
    $(this).prop("checked", false);
  });

  $("#lbl_permission").text("Not selected...");
  $("#lbl_permission").data("rid", null);
}

$("button.check-all").on("click", function () {
  var parents = $(this).parents(".form-group:first");
  var siblings = parents.siblings(".form-group");
  siblings.find("input:checkbox").prop("checked", true);
});

$("button.clear-all").on("click", function () {
  var parents = $(this).parents(".form-group:first");
  var siblings = parents.siblings(".form-group");
  siblings.find("input:checkbox").prop("checked", false);
});

function update_session() {
  checkboxes = $("input:checkbox:checked");

  mySession["version"] = $("#input_crf_version").val();
  mySession["comment"] = $("#input_crf_comment").val();

  mySession["chosen_crf_dm"] = "";
  mySession["chosen_crf_ds"] = "";

  mySession["options_DM_AGEU"] = "";
  mySession["options_DS_DSDECOD_IC"] = "";
  mySession["options_DS_DSDECOD_IA"] = "";

  $.each(checkboxes, function () {
    // selected DM variables
    if ($(this).attr("name").startsWith("chk_DM"))
      mySession["chosen_crf_dm"] += $(this).val() + ";";

    // selected DS variables
    if ($(this).attr("name").startsWith("chk_DS"))
      mySession["chosen_crf_ds"] += $(this).val() + ";";

    // ...

    var parent = $(this).parents(".input-group-prepend");
    var sibling = parent.siblings("input:text");

    if (sibling.attr("type") == "text")
      mySession[sibling.attr("id")] = sibling.val();

    if ($(this).attr("name") == "options_DM_AGEU") {
      var org = $(this).val();
      var key = org.startsWith("_") ? "1" + org : org;

      mySession["options_DM_AGEU"] += key + ";";
    }

    // ...
  });

  var domains = [];
  // visit 1 to ...
  var visits = $("#div_plan_CRF").find("input:checkbox:checked");
  $.each(visits, function () {
    var str = $(this).val();
    domains.push(str);
  });
  mySession["visit_plan"] = domains.join(";");

  return true;
}

function jp(json, id_to_send_to) {
  json = json
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;");
  json = json.replace(
    /("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g,
    function (match) {
      var cls = "color: darkorange;";
      if (/^"/.test(match)) {
        if (/:$/.test(match)) {
          cls = "color: red;";
        } else {
          cls = "color: green;";
        }
      } else if (/true|false/.test(match)) {
        cls = "color: blue;";
      } else if (/null/.test(match)) {
        cls = "color: magenta;";
      }
      return '<span style="' + cls + '">' + match + "</span>";
    }
  );

  document.getElementById(id_to_send_to).innerHTML = json;
}
