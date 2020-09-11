$(document).ready(function() {
    console.log("test hahaha");

    $("#btnSubmit").click(function (e) {

        //preventDefault 는 기본으로 정의된 이벤트를 작동하지 못하게 하는 메서드이다. submit을 막음
        e.preventDefault();
        var form = $('#fileUploadForm')[0];
        var data = new FormData(form);
        var fileName = $('input[type=file]').val().replace(/.*(\/|\\)/, '');
        debugger;
        $("#btnSubmit").prop("disabled", true);
        $.ajax({
            type: 'POST',
            enctype: 'multipart/form-data',
            url: '/api/upload/pdf',
            data: data,
            processData: false,
            contentType: false,
            cache: false,
            success: function(data) {
                windowOpenInPost(fileName, data)
                $("#btnSubmit").prop("disabled", false);
            }
        })
    })

    function windowOpenInPost(fileName, dataList)
    {
        var mapForm = document.createElement("form");
        mapForm.method = "POST";
        mapForm.target="target";
        mapForm.action = "/excel";
        if (dataList.length > 0){
            var mapInput = document.createElement("input");
            mapInput.type = "hidden";
            mapInput.name = 'fileName';
            mapInput.value = fileName;
            mapForm.appendChild(mapInput);
            for (var i = 0; i < dataList.length; i++){
                var mapInput = document.createElement("input");
                mapInput.type = "hidden";
                mapInput.name = 'data';
                mapInput.value = dataList[i].materialNo + "," +dataList[i].quantity;
                mapForm.appendChild(mapInput);

            }
            document.body.appendChild(mapForm);
        }

        debugger;
        map = window.open("","_self");
        if (map) {
            mapForm.submit();
        } else {
            alert('You must allow popups for this map to work.');
        }}
})