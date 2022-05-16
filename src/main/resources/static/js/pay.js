var IMP = window.IMP; // 생략 가능

//parameter  = 가게이름,??id ,구매자이름, 이메일, 전화번호,
function requestPay() {
    IMP.init("imp26725094"); // 예: imp00000000
      // IMP.request_pay(param, callback) 결제창 호출
      IMP.request_pay({ // param
          pg: "kcp",
          pay_method: "card",
          merchant_uid: "ORD20180131-0000035",
          name: "테스트 메뉴",
          amount: 100,
          buyer_email: "testamil@gmail.com",
          buyer_name: "누구지",
          buyer_tel: "010-1234-1234",
          buyer_addr: "의정부시 금오동",
          buyer_postcode: "01181"
      }, function (rsp) { // callback
          if (rsp.success) {

          let payData = new Object();
          payData.imp_uid = rsp.imp_uid;
          payData.merchant_uid = rsp.merchant_uid;
            $.ajax({
                url: "/test-pay",
                method: 'post',
                headers: { "Content-Type": "application/json" },
                data: JSON.stringify(payData),
                success: function(result){
                    alert("결제 성공22");
                },
                error:function(error){
                }
            })
            console.log(rsp);
              // 결제 성공 시 로직,


          } else {

              // 결제 실패 시 로직,
            alert("결제 실패");
          }
      });
}