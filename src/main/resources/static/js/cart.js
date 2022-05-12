function addCart(mid,name){
    let postUrl = "/add-cart/"+mid;

    $.ajax({
       type: 'post',
       url: postUrl,
       success: function(result){
            alert("장바구니에 추가되었습니다.")
          window.location.reload();
       },
       error: function(error){
            alert("에러");

       },
    });
}

function cartCheck(mid,name){
   if(!confirm(name+"을 장바구니에 추가하시겠습니까?")){
            return;
    }

    $.ajax({
        type: 'post',
        url: '/add-cart/'+mid+"/check",
        success: function(result){
            alert(result);
            if(result==="Duplicate"){
                alert("이미 담긴 상품입니다.")
                return;
            }
            else if(result==="NotSame"){
                if(confirm("동일한 가게의 주문만 가능합니다. 기존 장바구니를 초기화하고 새로 담으시겠습니까?")){
                    addCart(mid,name);
                }
           }
           else if(result==="Same"){
                addCart(mid,name);
           }
        },
        error: function(error){
            alert(error);
        },
    });


}