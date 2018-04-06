var csrf_token = getCookie('csrftoken');

function getCookie(name) {
    var cookieValue = null;
    if (document.cookie && document.cookie !== '') {
        var cookies = document.cookie.split(';');
        for (var i = 0; i < cookies.length; i++) {
            var cookie = jQuery.trim(cookies[i]);
            // Does this cookie string begin with the name we want?
            if (cookie.substring(0, name.length + 1) === (name + '=')) {
                cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                break;
            }
        }
    }
    return cookieValue;
}
function csrfSafeMethod(method) {
    // these HTTP methods do not require CSRF protection
    return (/^(GET|HEAD|OPTIONS|TRACE)$/.test(method));
}
$.ajaxSetup({
    beforeSend: function(xhr, settings) {
        if (!csrfSafeMethod(settings.type) && !this.crossDomain) {
            xhr.setRequestHeader("X-CSRFToken", csrf_token);
        }
    }
});

function viewProgram(programid)
{
    location.href = "../program/" + programid;
}

function deleteProgram(programId)
{
    var form = new FormData();
    form.append('operation', "delete")
    form.append('programId', programId)
    $.ajax({
    type:'POST',
    url:'../programs/',
    data:form,
    processData:false,  // 告诉jquery不转换数据
    contentType:false,  // 告诉jquery不设置内容格式

    success:function (arg) {
        if (arg["status"] == "1")
        {
            alert("Delete program successfully");
            window.location.href='../programs';

        }
        else
        {
            alert("Delete program failed");

        }
    }
    });
}
$(document).ready(function(){
    $("#add").click(function(){
        //调用函数居中窗口
          layer.open({
            type: 1,
            area: ['500px', '600px'],
            title: 'Create program',
            shadeClose: false, //点击遮罩关闭
            content: $('#addProgram')
          });
    });
    $("#create").click(function(){
        var form = new FormData();
        form.append("operation","create");
        form.append("programName",$("#programName").val());
        form.append("programDescription",$("#programDescription").val());
        $.ajax({
            type:'POST',
            url:'../programs/',
            data:form,
            processData:false,  // 告诉jquery不转换数据
            contentType:false,  // 告诉jquery不设置内容格式

            success:function (arg) {
                if (arg["status"] == "1")
                {
                    alert("Program created successfully");
                    window.location.href='../programs';

                }
                else
                {
                    alert("create program failed");

                }
            }
        });
    });
//    $("#search").click(function(){
//        var form = new FormData();
//        form.append("operation","search");
//        form.append("keyword",$("#keywordSearch").val());
//        $.ajax({
//            type:'POST',
//            url: '../programs/',
//            data:form,
//            processData:false,
//            contentType:false,
//
//        success:function (arg) {
//
//        }
//        })
//    })



});