$(document).ready(function() {

    $("#btnSubmitBosch").click(function (e) {
        //preventDefault 는 기본으로 정의된 이벤트를 작동하지 못하게 하는 메서드이다. submit을 막음
        e.preventDefault();
        var form = $('#fileUploadFormBosch')[0];
        var data = new FormData(form);
        var fileName = $('#uploadPdfBosch').val().replace(/.*(\/|\\)/, '');
        $("#btnSubmitBosch").prop("disabled", true);
        $.ajax({
            type: 'POST',
            enctype: 'multipart/form-data',
            url: '/api/upload/pdf/bosch',
            data: data,
            processData: false,
            contentType: false,
            cache: false,
            success: function(data) {
                if (data.length == 0) {
                    alert("변환된 Excel이 빈값입니다. \nPDF파일 혹은 PDF타입을 다시 확인해주세요.");
                    $("#btnSubmitBosch").prop("disabled", false);
                } else {
                    windowOpenInPostBosch(fileName, data)
                    $("#btnSubmitBosch").prop("disabled", false);
                    link = window.location.href;
                    if (link.includes("=")) {
                        link = link.split("?")[0];
                        window.location.replace(link + "?param=bosch");
                    } else {
                        window.location.replace(link + "?param=bosch");
                    }
                }
            }
        })
        e.stopPropagation();
    })

    function windowOpenInPostBosch(fileName, dataList)
    {
        var mapFormBosch = document.createElement("form");
        mapFormBosch.setAttribute("id", "formBosch");
        mapFormBosch.name = "formBoschName";
        mapFormBosch.method = "POST";
        mapFormBosch.target= "target";
        mapFormBosch.action = "/excel/bosch";
        if (dataList.length > 0){
            var mapInput = document.createElement("input");
            mapInput.type = "hidden";
            mapInput.name = 'fileName';
            mapInput.value = fileName;
            mapFormBosch.appendChild(mapInput);
            for (var i = 0; i < dataList.length; i++){
                var mapInput = document.createElement("input");
                mapInput.type = "hidden";
                mapInput.name = 'data';
                mapInput.value = dataList[i].invoiceOrder + "^" + dataList[i].invoiceNo +"^" + dataList[i].materialNo + "^" +dataList[i].quantity;
                mapFormBosch.appendChild(mapInput);

            }
            document.body.appendChild(mapFormBosch);
        }

        mapBosch = window.open("","_self");
        if (mapBosch) {
            $("#formBosch").submit();
            // mapFormBosch.submit();
        } else {
            alert('You must allow popups for this map to work.');
        }
    }
});