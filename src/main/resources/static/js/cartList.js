//// Remove Items From Cart
//function removeAction(){
//  event.preventDefault();
//  $( 'a.remove' ).parent().parent().hide( 400 );
//}

function plus(id){
    let strPrice = $('#price'+id).text();
    let eachPrice = parseInt(strPrice.split(" ")[1]);


    let quantity = parseInt($('#cnt'+id).val());
    if(quantity>=30 || quantity <=0){
        alert("1부터 30까지 가능합니다.")
        $('#cnt'+id).val(1);
        return;
    }

    let chgPrice = eachPrice * quantity;

    $('#total'+id).text(parseInt(chgPrice));
    calc();
}

function calc(){
 let sum = 0;
    $('.ePrice').each(function(){
        sum += parseInt($(this).text());
    });
    $('.totalValue').text(sum);

}
window.onload = calc();

function remove(mid){

    if(!confirm("해당 메뉴를 장바구니에서 삭제하시겠습니까?")){
        return;
    }


    let postUrl = "/cart-list/delete/"+mid;

    $.ajax({
        type: 'post',
        url : postUrl,
        success : function(result){
           // removeAction();
            alert("삭제되었습니다.");
           window.location.reload();
        },
        error : function(error){
            alert("에러");
        }
    });

}
