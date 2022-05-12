function login(){
      let id = $('#loginId').val();
      let pwd = $('#password').val();
      var logRequest = new Object();
      logRequest.loginId = id;
      logRequest.password = pwd;

      const element = document.getElementById('err');

      if(id == "" || pwd == ""){
        alert("아이디와 비밀번호를 입력해주세요.")
      }
      else{
      $.ajax({
        type: "post",
        url : "/login",
        data : logRequest,
        success : function(result){
          window.location.replace("/");
        },
        error : function(request,status,error){
         //alert(status + ' ' + request.status);
         if(request.status === 403) {
           element.innerText = "아이디 혹은 비밀번호가 일치하지 않습니다.";
         }
         else if(request.status === 402){
           alert("잦은 로그인 시도로 계정이 잠금되었습니다. 비밀번호 찾기를 이용해주세요");
           let lt = getCookie("lockTime");
           element.innerText = lt + " 동안 계정 잠금";
          }
          else if(request.status===302){
            alert("잠금이 해제되었습니다. 다시 로그인해주세요.")
            window.location.reload();
          }
        }
      });
    }
 }

 function getCookie(key) {
     var result = null;
     var cookie = document.cookie.split(';');
     cookie.some(function (item) {
         // 공백을 제거
         item = item.replace(' ', '');

         var dic = item.split('=');

         if (key === dic[0]) {
             result = dic[1];
             return true;    // break;
         }
     });
     return result;
 }
