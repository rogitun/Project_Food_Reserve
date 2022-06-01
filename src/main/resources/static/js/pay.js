    for(let i = 9;i < 23;i++){
    let hm = i + ":00";
    let hm2 = i + ":30";
    $("#time").append("<option value=" + hm + ">" + hm + "</option>");
    $("#time").append("<option value=" + hm2 + ">" + hm2 + "</option>");
}

var IMP = window.IMP; // 생략 가능


//1. 인원, 시간, 타입 등의 정보를 보내고 book을 수정,
//2. 그 후 bookid, 가게이름, 구매자이름, 이메일, 전화번호 등을 받아와서 requestPay로 보내줌
function beforePay(){

    let typeVal = $("#visit option:selected").val();
    let visitVal = $("#visitNum").val();
    let dateVal = $("#date").val();
    let timeVal = $('#time').val();
    console.log(typeVal + " " + visitVal + " " + dateVal + " " + timeVal);

    let obj = new Object();

    obj.typeVal = typeVal;
    obj.visitVal = visitVal;
    obj.dateVal = dateVal;
    obj.timeVal = timeVal;

    $.ajax({
        type: 'post',
//        url: '/test',
        headers: { "Content-Type": "application/json" },
        data: JSON.stringify(obj),
        success: function(result){
            requestPay(result.paymentDetails);
        },
        error: function(error){
            alert(error.responseJSON.message);
        }
    })
}

//parameter  = 가게이름,bookId ,구매자이름, 이메일, 전화번호,결제금액
function requestPay(api) {
    IMP.init("imp26725094"); // 예: imp00000000
      // IMP.request_pay(param, callback) 결제창 호출
      IMP.request_pay({ // param
          pg: "kcp",
          pay_method: "card",
          merchant_uid: api.bookId,
          name: api.sellerName,
          amount: api.paidAmount,
          buyer_email: api.email,
          buyer_name: api.studentName,
          buyer_tel: api.phoneNumber,
      }, function (rsp) { // callback
          if (rsp.success) {
          let payData = new Object();
          payData.merchant_uid = rsp.merchant_uid;
          payData.buyer_name = rsp.buyer_name;
          payData.buyer_email = rsp.buyer_email;
          payData.card_name = rsp.card_name;
          payData.amount = rsp.paid_amount;
          payData.status = rsp.status;
          payData.success = rsp.success;
            $.ajax({
                url: "/payment-processing/"+api.bookId,
                method: 'post',
                headers: { "Content-Type": "application/json" },
                data: JSON.stringify(payData),
                success: function(result){
                    alert("결제 완료");
                    window.location.replace("/profile");
                },
                error:function(error){
                    alert("결제 실패2");
                }
            });
          } else {
            alert("결제 실패3");
            console.log(rsp);
          }
      });
}