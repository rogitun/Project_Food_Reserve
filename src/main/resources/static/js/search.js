function category(){
    let value = $('#categoryBox option:selected').val();
    let param = "/?category="+value;
    window.location.replace(param);

}