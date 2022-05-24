    function accept(bid){
        let id = bid;
        let acceptUrl = "/book/" + id + "/accept";
        let seller = "/profile"

        console.log(id);
        $.ajax({
            type: "post",
            url : acceptUrl,
            data: id,
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
