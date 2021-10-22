var dom_data =
  "<'row'<'col-sm-6 text-left'f><'col-sm-6 text-right'>>" +
  "<'row'<'col-sm-12'tr>> " +
  "<'row'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7 dataTables_pager'lp>>";

var table_buttons = ["print", "copyHtml5", "excelHtml5", "pdfHtml5"];

$("button.btn-verify").on("click", function (event) {
  var studyid = $(this).attr("data-studyid");
  var siteid = $(this).attr("data-siteid");
  var visit = $(this).attr("data-visit");
  var sid = $(this).attr("data-sid");
  var scrno = $(this).attr("data-scrno");
  var domain = $(this).attr("data-domain");
  var cdashig = $(this).attr("data-cdashig");

  var bdata = "verify/";
  bdata += studyid + "/";
  bdata += siteid + "/";
  bdata += scrno + "/";
  bdata += visit + "/";
  bdata += sid + "/";
  bdata += domain + "/";
  bdata += cdashig + "/";

  event.preventDefault();

  var targetButton = $(this);

  $.ajax({
    method: "post",
    dataType: "text",
    url: bdata,
    contentType: "application/json",
    data: $("#form_crf_" + domain).serialize(),
    success: function (result) {
      var parsedData = JSON.parse(result);

      Swal.mixin({
        toast: true,
        position: "top-end",
        showConfirmButton: false,
        timer: 3000,
      }).fire({
        icon: parsedData.icon_type,
        title: parsedData.msg_text,
      });

      if (parsedData.status == 1) {
        targetButton.removeClass("btn-outline-hover-success");
        targetButton.addClass("btn-success");
      } else {
        targetButton.removeClass("btn-success");
        targetButton.addClass("btn-outline-hover-success");
      }
    },
    error: function (xhr, desc, err) {
      console.log(err);
    },
  });
});

$("button.btn-review").on("click", function (event) {
  var studyid = $(this).attr("data-studyid");
  var siteid = $(this).attr("data-siteid");
  var visit = $(this).attr("data-visit");
  if (visit === undefined) visit = 0;
  var sid = $(this).attr("data-sid");
  if (sid === undefined) sid = 0;
  var scrno = $(this).attr("data-scrno");
  var domain = $(this).attr("data-domain");
  var cdashig = $(this).attr("data-cdashig");

  var bdata = "review/";
  bdata += studyid + "/";
  bdata += siteid + "/";
  bdata += scrno + "/";
  bdata += visit + "/";
  bdata += sid + "/";
  bdata += domain + "/";
  bdata += cdashig + "/";

  event.preventDefault();

  var targetButton = $(this);

  $.ajax({
    method: "post",
    dataType: "text",
    url: bdata,
    contentType: "application/json",
    data: $("#form_crf_" + domain).serialize(),
    success: function (result) {
      var parsedData = JSON.parse(result);

      Swal.mixin({
        toast: true,
        position: "top-end",
        showConfirmButton: false,
        timer: 3000,
      }).fire({
        icon: parsedData.icon_type,
        title: parsedData.msg_text,
      });

      if (parsedData.status == 1) {
        targetButton.removeClass("btn-outline-hover-success");
        targetButton.addClass("btn-success");
      } else {
        targetButton.removeClass("btn-success");
        targetButton.addClass("btn-outline-hover-success");
      }
    },
    error: function (xhr, desc, err) {
      console.log(err);
    },
  });
});

$("button.btn-query").on("click", function () {
  var button = $(this); // Button that triggered the modal
  var sid = button.data("sid") === undefined ? 0 : button.data("sid");
  var input_value;

  // ...

  $("#input_reply_" + cdashig + sid).attr("data-value", input_value);
  $("#collapse_history_" + cdashig + sid).collapse("hide");
});

$("button.btn-query-submit").on("click", function () {
  var button = $(this); // Button that triggered the modal
  var studyid = button.data("studyid"); // Extract info from data-*
  var siteid = button.data("siteid");
  var scrno = button.data("scrno");
  var visit = button.data("visit") === undefined ? 0 : button.data("visit");
  var sid = button.data("sid") === undefined ? 0 : button.data("sid");

  // query
  var q_textarea = button.closest(".form-group").find("textarea").val().trim();
  var query_data = {};

  query_data["studyid"] = studyid;
  query_data["siteid"] = siteid;
  query_data["scrno"] = scrno;
  query_data["visit"] = visit;
  query_data["sid"] = sid;
  query_data["domain"] = domain;
  query_data["cdashig"] = cdashig;
  query_data["value"] = value;
  query_data["comment"] = q_textarea;

  if (q_textarea.length == 0) {
    swal.fire({
      title: "You must enter something!",
      icon: "error",
      backdrop: false,
      confirmButtonText: "OK",
    });
    return;
  }

  // ...
});

$("button.btn-reply-submit").on("click", function () {
  var input = $(this).parents(".input-group-append").siblings("input");
  var studyid = input.data("studyid");
  var siteid = input.data("siteid");
  var scrno = input.data("scrno");
  var visit = input.data("visit");
  var sid = input.data("sid");

  // ...

  if (value.trim().length == 0) {
    swal.fire({
      title: "You must enter something!",
      icon: "error",
      backdrop: false,
      confirmButtonText: "OK",
    });

    return;
  }

  var reply_data = {};
  reply_data["studyid"] = studyid;
  reply_data["siteid"] = siteid;
  reply_data["scrno"] = scrno;
  reply_data["visit"] = visit;
  reply_data["sid"] = sid;

  // ...

  $.ajax({
    type: "POST",
    dataType: "json",
    contentType: "application/json; charset=utf-8",
    url: "/reply_submit",
    data: JSON.stringify(reply_data),
    success: function (data, textStatus, xhr) {
      KTApp.unblock(mother);

      swal
        .fire({
          title: "Reply has been sent successfully!",
          icon: "success",
          backdrop: false,
          confirmButtonText: "OK",
        })
        .then(function (result) {
          if (result.value) location.reload();
        });
    },
    error: function () {
      KTApp.unblock(mother);

      swal.fire({
        title: "Something is wrong!",
        icon: "error",
        backdrop: false,
        confirmButtonText: "OK",
      });
    },
  });
});

$("a.query-action").click(function (e) {
  var button = $(this);
  var studyid = button.data("studyid");
  var siteid = button.data("siteid");
  var scrno = button.data("scrno");
  var visit = button.data("visit");
  var sid = button.data("sid");

  // ...

  var query_data = {};
  query_data["studyid"] = studyid;
  query_data["siteid"] = siteid;
  query_data["scrno"] = scrno;
  query_data["visit"] = visit;
  query_data["sid"] = sid;

  // ...

  var mother = button.closest(".collapse");

  // ...
});
