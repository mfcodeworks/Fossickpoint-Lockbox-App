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

function deleteContent(id)
{
    $.post('./contents',{'operation':'delete','id':id},function (result){
        if (result["status"] == 1)
        {
            alert("delete successfully");
            window.location.href='../contents';
        }


    })
}

function updateContent()
{
    var updatefile = $("#update-file")[0].files[0];
    var updatethumbnail = $("#update-thumbnail-file")[0].files[0];
    var form = new FormData();
    form.append('operation', 'update');
    form.append('file', updatefile);
    form.append('thumbnail', updatethumbnail);
    form.append('title', $("#contentName").val());
    form.append('type', $("#contentType").val());
    form.append('tag', $("#contentTag").val());
    form.append('keyword', $("#contentDiscription").val());
    form.append('focus', $("#contentFocus").val());
    form.append('category', $("#searchCategory").html());
    var profile = "";
    $('input[type="checkbox"][name="chk"]:checked').each(
        function () {
            profile = profile + $(this).val() + ";";
        }
    );
    profile = profile.substring(0, profile.length - 1)
    form.append('profile', profile);
    $.ajax({
        type: 'POST',
        url: location.href,
        data: form,
        processData: false,  // 告诉jquery不转换数据
        contentType: false,  // 告诉jquery不设置内容格式

        success: function (arg) {
            if (arg["status"] == "1") {
                alert("Update content details successfully1");
                location.reload();
            }
            else {
                alert("Update content details failed!!");
            }
        }
    })
}

function filePreview(fileaddress) {
    var form = new FormData();
    form.append("fileaddress", fileaddress);
    $.ajax({
        type:'POST',
        url:'../preview/',
        data:form,
        processData:false,  // 告诉jquery不转换数据
        contentType:false,  // 告诉jquery不设置内容格式

        success:function (data) {
            var iframe = $('<iframe>');
            iframe.attr('src','/pdf/yourpdf.pdf?options=first&second=here');
            $('#targetDiv').append(iframe);
        }
    })   
}
function clearUploadForm() {
    $("#title").val("");
    $("#tag").val("");
    $("keyword").val("");

}

function search() {
    var focus = $("#focusSearch").val();
    var profile = $("#profileSearch").val();
    alert(profile);
    var tag = $("#tagSearch").val();
    var level = $("#levelSearch").val();
    var form = new FormData();
    form.append('operation', 'search');
    form.append('focus',focus);
    form.append('profile',profile);
    form.append('tag',tag);
    form.append('level',level);
    $.ajax({
        type:'POST',
        url:'../content',
        data:form,
        processData:false,  // 告诉jquery不转换数据
        contentType:false,  // 告诉jquery不设置内容格式

        success:function (arg) {

        }
    })
}
$(document).ready(function() {

    // $("#update").click(function(){
    //         var fileobj = $("#doc-form-file")[0].files[0];
    //         var fileobj2 = $("#doc-form-thumbnail")[0].files[0];
    //         var form = new FormData();
    //         form.append('operation', 'update');
    //         form.append('file', fileobj);
    //         form.append('thumbnail', fileobj2);
    //         form.append('title', $("#title1").val());
    //         form.append('type', $("#selectedType1").val());
    //         form.append('tag', $("#tag1").val());
    //         form.append('keyword', $("#keyword1").val());
    //         form.append('focus', $("#focus1").val());
    //         console.log("123");
    //         // form.append('category', $("#searchCategory").html());
    //         var profile = "";
    //         $('input[type="checkbox"][name="chk"]:checked').each(
    //             function () {
    //                 profile = profile + $(this).val() + ";";
    //             }
    //         );
    //         profile = profile.substring(0, profile.length - 1)
    //         form.append('profile', profile);
    //         $.ajax({
    //             type: 'POST',
    //             url: location.href,
    //             data: form,
    //             processData: false,  // 告诉jquery不转换数据
    //             contentType: false,  // 告诉jquery不设置内容格式
    //
    //             success: function (arg) {
    //                 if (arg["status"] == "1") {
    //                     alert("Update content details successfully2");
    //                     location.reload();
    //                 }
    //                 else {
    //                     alert("Update content details failed!!");
    //                 }
    //             }
    //         })
    //     });
    $("#reset").click(function () {
        clearUploadForm();
    });
    $("#add").click(function () {
        //调用函数居中窗口
        layer.open({
            type: 1,
            area: ['800px', '700px'],
            title: 'Add content',
            shadeClose: false, //点击遮罩关闭
            content: $('#addContent')
        });
    });

    $("#upload").click(function () {
        var fileobj = $("#doc-form-file")[0].files[0];
        var fileobj2 = $("#doc-form-thumbnail")[0].files[0];
        var form = new FormData();
//        var csrf_token = getCookie('csrftoken');
        form.append('operation', 'add');
        form.append('file', fileobj);
        form.append('thumbnail', fileobj2);
        form.append('title', $("#title").val());
        form.append('type', $("#selectedType").val());
        form.append('level', $("#selectedLevel").val());
        form.append('tag', $("#selectedTag").val());
        form.append('keyword', $("#keyword").val());
        form.append('focus', $("#focus").val());
        form.append('category', $("#searchCategory").html());
        var profile = "";
        $('input[type="checkbox"][name="chk"]:checked').each(
            function () {
                profile = profile + $(this).val() + ";";
            }
        );
        profile = profile.substring(0, profile.length - 1);
        form.append('profile', profile);
        $.ajax({
            type: 'POST',
            url: '../contents/',
            data: form,
            processData: false,  // 告诉jquery不转换数据
            contentType: false,  // 告诉jquery不设置内容格式

            success: function (arg) {
                if (arg["status"] == "1") {
                    alert("upload file successfully");
                    window.location.href = '../contents';
                }
                else {
                    alert("upload file failed, please finish the form");
                    clearUploadForm();
                }
            }
        })
   });
        // post /content/id update


})
