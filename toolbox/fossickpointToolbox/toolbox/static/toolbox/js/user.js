$(document).ready(function(){
    $("#update").click(function(){
        var profile = "";
        $("input[name='profileCk']:checked").each(function(){
            profile = profile + this.value + ";";
        })
        var note = $("#note").val();
        var form = new FormData();
        alert(note);
        var userType = $("#userTypeSelect").find("option:selected").val();
        alert(userType);
        form.append("operation", "update");
        form.append("profile", profile);
        form.append("note", note);
        form.append("userType", userType);
        $.ajax({
            type:'POST',
            url: location.href,
            data:form,
            processData:false,  // 告诉jquery不转换数据
            contentType:false,  // 告诉jquery不设置内容格式

            success:function (arg) {
                if (arg["status"] == "1")
                {
                    alert("User information updated successfully");
                    location.reload();

                }
                else
                {
                    alert("create program failed");

                }
            }
        });
    })
    $("#back").click(function(){
        location.href="../../";
    })
});

//remove assigned program
function removeProgram(programID) {
    var form = new FormData()
    form.append("operation", "remove");
    form.append("program", programID);
    $.ajax({
           type:'POST',
           url: location.href,
           data:form,
           processData:false,  // 告诉jquery不转换数据
           contentType:false,  // 告诉jquery不设置内容格式

           success:function (arg) {
               if (arg["status"] == "1")
               {
                   alert("Program removed successfully");
                   location.reload();

               }
               else
               {
                   alert("Program removed failed");

               }
            }
        });
}

//assign program
function assignProgram(programID) {
    var form = new FormData()
    form.append("operation", "assign");
    form.append("program", programID);
    $.ajax({
           type:'POST',
           url: location.href,
           data:form,
           processData:false,  // 告诉jquery不转换数据
           contentType:false,  // 告诉jquery不设置内容格式

           success:function (arg) {
               if (arg["status"] == "1")
               {
                   alert("Program assigned successfully");
                   location.reload();

               }
               else
               {
                   alert("Program assigned failed");

               }
            }
        });
}


