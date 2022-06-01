    function check(bid){
        let cancelUrl = "/book/" + bid + "/cancel";
        console.log("test1");
        let flag = "no";
        if(!confirm("정말 취소하시겠어요?")){
            return;
        }
        $.ajax({
           type: "post",
           url : cancelUrl,
           success: function(result){
                alert("예약이 취소되었습니다."),
                window.location.replace("/profile");
           },
           error : function(error){
                alert("잠시 후에 다시 시도해주세요.");
           }
         });
    }

    function cancelRequest(bid){
    let url = "book/" + bid + "/cancelRequest";
        $.ajax({
            type : 'post',
            url : url,
            success: function(result){
                  alert("취소 요청이 전송되었습니다. 확실한 취소를 위해 가게에 전화 부탁드립니다.");
                  window.location.reload();
            },
            error: function(error){
                alert("취소 요청에 실패했습니다. 가게에 전화 부탁드립니다.");
            }
        });
    }