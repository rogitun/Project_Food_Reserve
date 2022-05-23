
   function validateId(){
       let id = $('#id').val();
       let obj = new Object();
       obj.id = id;
       $.ajax({
            type : 'post',
            url : '/signup/validatingId',
            headers: { "Content-Type": "application/json" },
            data : JSON.stringify(obj),
            success : function(result){
                alert(result)
            },
            error : function(error){
                alert(error.responseText)
            },
       })
}

   function validateEmail(){
           let email = $('#email').val();
           let obj = new Object();
           obj.email = email;
           $.ajax({
                    type : 'post',
                    url : '/signup/validatingEmail',
                    headers: { "Content-Type": "application/json" },
                    data : JSON.stringify(obj),
                    success : function(result){
                        alert(result)
                    },
                    error : function(error){
                        alert(error.responseText)
                    },
               })
   }

    function student(){
        $('#basicErr').text("");
        $('#pwdErr').text("");
        let flag = true;
        let email = $('#email').val();
        let id = $('#id').val();
        let name = $('#name').val();
        let pwd = $('#pwd').val();
        let cpwd = $('#cpwd').val();
        let phone = $('#phone').val();

        if(email=='' || id == '' || name == '' || phone == '' ||
             pwd == '' || cpwd == ''){
             $('#basicErr').text("모든 항목을 입력해주세요.");
             flag = false;
        }
        if(pwd !== cpwd){
          $('#pwdErr').text('비밀번호가 일치하지 않습니다.');
          flag = false;
        }
        if(flag==true){
                        //회원가입 로직
        let user = new Object();
        user.loginId = id;
        user.email = email;
        user.name = name;
        user.phoneNumber = phone;
        user.password = pwd;


        $.ajax({
            type : 'post',
            url : '/signup-student',
            headers: { "Content-Type": "application/json" },
            data : JSON.stringify(user),
            success : function(result){
                alert(result);
                window.location.replace("/loginForm");
            },
            error : function(error){
                alert(error.responseText);
            }
        })
    }
}

   function signIn(){
            $('#basicErr').text("");
            $('#pwdErr').text("");
            $('#addrErr').text("")

            let flag = true;
            let email = $('#email').val();
            let id = $('#id').val();
            let name = $('#name').val();
            let phone = $('#phone').val();
            let pwd = $('#pwd').val();
            let cpwd = $('#confirmPwd').val();
            let companyId = $('#companyId').val();

            let zipCode = $('#postcode').val();
            let address = $('#address').val();
            let detail = $('#detailAddress').val();


            if(email=='' || id == '' || name == '' || phone == '' ||
             pwd == '' || cpwd == '' || companyId == ''){
                    $('#basicErr').text("모든 항목을 입력해주세요.");
                    flag = false;
            }
            if(pwd !== cpwd){
                $('#pwdErr').text('비밀번호가 일치하지 않습니다.');
                flag = false;
            }
            if(address =='' || zipCode == '' || detail==''){
                $('#addrErr').text("주소를 입력해주세요.")
                flag =false;
            }

            if(flag==true){
                //회원가입 로직
                let user = new Object();
                user.loginId = id;
                user.email = email;
                user.name = name;
                user.phoneNumber = phone;
                user.password = pwd;
                user.companyId = companyId;
                user.doro = address;
                user.zipCode = zipCode;
                user.doroSpec = detail;

                $.ajax({
                    type : 'post',
                    url : '/signup',
                    headers: { "Content-Type": "application/json" },
                    data : JSON.stringify(user),
                    success : function(result){
                        alert(result);
                        window.location.replace("/loginForm");
                    },
                    error : function(error){
                        alert(error.responseText);
                    }
                })
            }
   }


    function execDaumPostcode() {
        new daum.Postcode({
            oncomplete: function(data) {
                // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

                // 각 주소의 노출 규칙에 따라 주소를 조합한다.
                // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
                var addr = ''; // 주소 변수
                var extraAddr = ''; // 참고항목 변수

                //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
                if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                    addr = data.roadAddress;
                } else { // 사용자가 지번 주소를 선택했을 경우(J)
                    addr = data.jibunAddress;
                }

                // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
                if(data.userSelectedType === 'R'){
                    // 법정동명이 있을 경우 추가한다. (법정리는 제외)
                    // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
                    if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                        extraAddr += data.bname;
                    }
                    // 건물명이 있고, 공동주택일 경우 추가한다.
                    if(data.buildingName !== '' && data.apartment === 'Y'){
                        extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                    }
                    // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
                    if(extraAddr !== ''){
                        extraAddr = ' (' + extraAddr + ')';
                    }
                    // 조합된 참고항목을 해당 필드에 넣는다.
                }
                // 우편번호와 주소 정보를 해당 필드에 넣는다.
               // document.getElementById('postcode').value = data.zonecode;
                $('#postcode').val(data.zonecode);
                //document.getElementById("address").value = addr;
                $('#address').val(addr);
                // 커서를 상세주소 필드로 이동한다.
                document.getElementById("detailAddress").focus();
            }
        }).open();
    }