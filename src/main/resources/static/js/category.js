function del(cid){
    if(!confirm("정말 삭제하시겠습니까?")) return;

    let url = "/admin/" + cid +"/del-category/";
    $.ajax({
        type: 'post',
        url : url,
        success: function(result){
            alert("삭제되었습니다.");
            window.location.reload();
        },
        error: function(error){
            alert("삭제 실패");
        }
    })
}

function NoticeDel(nid){
    if(!confirm("삭제하시겠습니까?")){
        return;
    }
    let url = "/admin/" + nid + "/notice-del";

    $.ajax({
        type: 'post',
        url : url,
        success: function(result){
            alert("삭제되었습니다.");
            window.location.reload();
        },
        error: function(error){
            alert("실패");
        }
    })

}