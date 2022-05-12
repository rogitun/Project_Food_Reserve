function check(){
       let id = $('#loginId').val();
       let email = $('#email').val();

       if(id===null || email === null){
       alert("아이디 혹은 이메일을 다시 확인해주세요.")
       return;
       }

       let checkData = new Object();
       checkData.id = id;
       checkData.email = email;

       $.ajax({
        type: "post",
        url : "/forget-password",
        contentType: 'application/json',
        data : JSON.stringify(checkData),
        success : function(result){
            alert("이메일이 전송되었습니다. 확인해주세요.");
            window.location.replace("/loginForm");
        },
        error : function(error){
            alert("가입된 정보가 없습니다.")
        }

       });
}