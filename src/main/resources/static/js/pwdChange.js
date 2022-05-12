function change(id){
    let p1 = $('#pwd1').val();
    let p2 = $('#pwd2').val();

    console.log(p1 + " " + p2);

    if(p1 !== p2){
        alert("비밀번호 불일치");
        return;
    }

    let pwd = new Object();
    pwd.one = p1;
    pwd.two = p2;

    $.ajax({
        type: 'post',
        url: "/reset-password/" + id,
        contentType: 'application/json',
        data: JSON.stringify(pwd),
        success : function(result){
            alert("비밀번호가 변경되었습니다. 다시 로그인해주세요.");
            window.location.replace("/loginForm");
        },
        error : function(error){
            alert("잘못된 접근입니다.");
            window.location.reload();
        },
    });
}