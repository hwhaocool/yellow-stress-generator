

function log() {

    //scope
    var min = $("#min").val();
    if (! min) {
        alert("please input min");
        return;
    }
    
    min = parseInt(min, 10);
    
    var max = $("#max").val();
    if (! max) {
        alert("please input max");
        return;
    }
    max = parseInt(max, 10);
    
    if (max <= min) {
        alert("please input valid max and min");
        return;
    }
    
    var input = $("#input").val();
    if (! input) {
        alert("please input");
        return;
    }
    
    //切分并校验是否被数字
    var num_str_list = input.split("\n");
    
    if (! check(num_str_list)) {
        return;
    }
    console.log("all value is ok");
    
    //组装数字列表
    var num_list = [];
    num_str_list.forEach((a) => {
        num_list.push(parseInt(a, 10));
    });
    
    //排序
    bubbleSort(num_list)
    console.log(num_list);

    // 填充排序
    var sort_result = "";
    num_list.forEach((a) => {
        sort_result += a + "\n";
    });
    
    $("#sort").val(sort_result);
    
    // 排序和输入校验
    if(num_list[0] < min) {
        alert("min is invalid, log min is " + num_list[0]);
        return;
    }
    
    if(num_list[num_list.length-1] > max) {
        alert("max is invalid, log max is " + num_list[num_list.length-1]);
        return;
    }
    
    var gap_list = [];
    
    for(var j=0; j< num_list.length; ) {
        for(var i=min; i<max; i++) {
            var value = parseInt(num_list[j], 10);
            
            if(i != value) {
                gap_list.push(i);
            } else {
                j++;
            }
        }
    }
    
    
    var result = "";
    gap_list.forEach((a) => {
        result += a + "\n";
    });
    
    $("#result").val(result);
}


function check(num_list) {
    try {
        num_list.forEach((a) => {
            if( isNaN(a)) {
                alert("有非法值, " + a);
                throw new Error();
            }
        });
        
        return true;
    } catch(e) {
    }
    
    return false;
}

//冒泡排序
function bubbleSort(num_list) {
    var len = num_list.length;
    
    for(var j=len; j>0 ;j--) {
        for(var i=0; i<j ;i++) {
            if (num_list[i] > num_list[i+1]) {
                swap(num_list, i, i+1);
            }
        }
    }
}

function swap(num_list, i, j) {
    var a = num_list[i];
    var b = num_list[j];

    num_list[i] = b;
    num_list[j] = a;
}
