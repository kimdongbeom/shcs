$(document).ready(function() {

    $("#btnSubmit").click(function (e) {

        //preventDefault 는 기본으로 정의된 이벤트를 작동하지 못하게 하는 메서드이다. submit을 막음
        e.preventDefault();
        var form = $('#fileUploadForm')[0];
        var data = new FormData(form);
        var fileName = $('input[type=file]').val().replace(/.*(\/|\\)/, '');
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
                if (data.length == 0) {
                    alert("변환된 Excel이 빈값입니다. \nPDF파일 혹은 PDF타입을 다시 확인해주세요.");
                    $("#btnSubmit").prop("disabled", false);
                } else {
                    windowOpenInPost(fileName, data)
                    $("#btnSubmit").prop("disabled", false);
                }
            }
        })

        e.stopPropagation();
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
                mapInput.value = dataList[i].invoiceOrder + "," + dataList[i].invoiceNo +"," + dataList[i].materialNo + "," +dataList[i].quantity;
                mapForm.appendChild(mapInput);

            }
            document.body.appendChild(mapForm);
        }

        map = window.open("","_self");
        if (map) {
            mapForm.submit();
        } else {
            alert('You must allow popups for this map to work.');
        }}
})