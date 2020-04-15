
function getQueryString(name) {
    var result = window.location.search.match(new RegExp("[\?\&]" + name + "=([^\&]+)", "i"));
    if (result == null || result.length < 1) {
        return "";
    }
    return result[1];
}

function sendRequest() {

    var data = {};

    // payload
    var payload = $("#payload").val();

    if (! payload) {
        alert("payload is required!")
        return;
    }
    data.payload = payload;

    //count
    var count = $("#count").val();
    if (! count) {
        count = 1;
    }
    data.count = count;

    //concurrent
    var concurrent = $("#concurrent").val();
    if (! concurrent) {
        concurrent = 1;
    }
    data.concurrent = concurrent;

    //type
    var type = "async";
    if ($("#type1")[0].checked) {
        type = $("#type1").val();
    } else if ($("#type2")[0].checked) {
        type = $("#type2").val();
    }
    data.type = type;

    console.log(data);

    $("#result").val("sending... waiting");
    
    $.ajax({
        type: "put",
        async: false,            //异步请求（同步请求将会锁住浏览器，用户其他操作必须等待请求完成才可以执行）
        url: "/stress/send",    //请求发送到TestServlet处
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(data),
        dataType: "text",        //返回数据形式为json
        success: function (result) {
            //请求成功时执行该函数内容，result即为服务器返回的json对象
            if (result) {

                console.log(result);
                
                if (result) {
                    var resJson = JSON.parse(result);
                    $("#cost").val(resJson.cost);
                    $("#rate").val(resJson.rate);
                    $("#status_code").val(resJson.statusCode);
                    
                    var haha = JSON.stringify(JSON.parse(resJson.message),null,4);
                    $("#result").val(haha);
                }
            }
        },
        error: function (errorMsg) {
            //请求失败时执行该函数

            $("#result").val("Failed.....");
            console.log(errorMsg);
        }
    });
}

