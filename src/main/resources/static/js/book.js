    function accept(bid){
        let acceptUrl = "/book/" + bid + "/accept";
        let seller = "/profile"

        $.ajax({
            type: "post",
            url : acceptUrl,
            data: bid,
            dataType : 'text',
            success: function(result){
                alert("예약이 승인되었습니다.");
                window.location.replace(seller);
            },
            error: function(error){
                 alert("예약 처리 과정에 문제가 발생했습니다.");
                window.location.replace("/");
            }
        });
    }
