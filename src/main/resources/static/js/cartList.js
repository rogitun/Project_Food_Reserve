//// Remove Items From Cart


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
        if(result==="Ok"){
           removeAction(mid);
            alert("삭제되었습니다.");
           }
        },
        error : function(error){
            alert("에러");
        }
    });
}

function removeAction(mid){
  event.preventDefault();
  $('#total'+mid).text(0);
  $('#wrap'+mid).hide( 400 );
  calc();
  let total = parseInt($('.totalValue').text());
  if(total===0){
    window.location.reload();
  }
}

function listConfirm(){
    //class = itemNumber, qty
     let numbers = new Array();
     let qtys = new Array();
    $('.itemNumber').each(function(){
        numbers.push($(this).text());
    });

    $('.qty').each(function(){
        qtys.push($(this).val());
    });

    let obj = new Array();
    for(let i =0;i<numbers.length;i++){
        console.log(numbers[i] + " " + qtys[i]);
        obj.push({id: parseInt(numbers[i]), quantity: parseInt(qtys[i])});
    }

    $.ajax({
        type: 'post',
        url: '/list-confirm',
        headers: { "Content-Type": "application/json" },
        data : JSON.stringify(obj),
        success: function(result){
            alert("장바구니 내역이 저장되었습니다. 결제 후 예약이 완료됩니다.");
            //alert(result);
            window.location.replace("/book/" + result + "/un-paid");
        },
        error : function(error){
            alert("실패");
        }
    })
}