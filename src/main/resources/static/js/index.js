$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	// 获取数据
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();

	$.post(
	    CONTEXT_PATH + "/discuss/add",
	    {"title": title, "content": content},
	    function(data) {
	        data = $.parseJSON(data);
	        // 在提示框当中显示返回消息
	        $("#hintBody").text(data.message);
            $("#hintModal").modal("show");
            setTimeout(function(){
                $("#hintModal").modal("hide");
                //
                if (data.code == 0) {
                    window.location.reload();
                }
            }, 2000);
	    }
	);
}