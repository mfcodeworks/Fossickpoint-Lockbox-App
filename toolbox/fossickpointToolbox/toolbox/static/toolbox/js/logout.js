function logout()
{
    $.ajax({
        type:'GET',
        url:  "../logout",
        processData:false,  // 告诉jquery不转换数据
        contentType:false,  // 告诉jquery不设置内容格式

        success:function (arg) {
            if (arg["status"] == "1")
            {
                location.href = "../";

            }
            else
            {
            }
        }
    });
}