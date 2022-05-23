    function best(mid){
        if(!confirm("대표 상태를 변경하시겠습니까?")){
            return;
        }
        let postUrl = "/menu/" + mid + "/status";

        $.ajax({
            type: "post",
            url : postUrl,
            headers: {'Content-Type': 'application/json'},
            data : JSON.stringify({ data : 'best'}),
            success : function(result){
                alert("대표 메뉴 설정 변경");
                window.location.reload();
            },
            error : function(error){
                alert("잠시 후 다시 시도해주세요.");
            }
        });
    }
        function stock(mid){
        if(!confirm("품절 상태를 변경하시겠습니까?")){
            return;
        }
        let postUrl = "/menu/" + mid + "/status";

        $.ajax({
            type: "post",
            url : postUrl,
            headers: {'Content-Type': 'application/json'},
            data : JSON.stringify({ data : 'stock'}),
            success : function(result){
                alert("품절 상태 변경");
                window.location.reload();
            },
            error : function(error){
                alert("잠시 후 다시 시도해주세요.");
            }
        });
    }
     function delMenu(mid){
        let postUrl = "/menu/" + mid + "/del";
        if(!confirm("해당 메뉴를 삭제하시겠습니까?")){
            return;
        }
        $.ajax({
            type : 'post',
            url : postUrl,
            success : function(result){
                alert("삭제 되었습니다.");
                window.location.reload();
            },
            error : function(error){
                alert("예기치 못한 에러, 잠시 후 다시 시도해주세요");
            }
        });
    }
