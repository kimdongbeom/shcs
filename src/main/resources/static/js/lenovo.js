$(document).ready(function() {

    $("#btnSubmitLenovo").click(function (e) {
        //preventDefault 는 기본으로 정의된 이벤트를 작동하지 못하게 하는 메서드이다. submit을 막음
        debugger;
        e.preventDefault();
        var form = $('#fileUploadFormLenovo')[0];
        var data = new FormData(form);
        var fileName = $('#uploadPdfLenovo').val().replace(/.*(\/|\\)/, '');
        $("#btnSubmitLenovo").prop("disabled", true);
        $.ajax({
            type: 'POST',
            enctype: 'multipart/form-data',
            url: '/api/upload/pdf/lenovo',
            data: data,
            processData: false,
            contentType: false,
            cache: false,
            success: function(data) {
                if (data.length == 0) {
                    alert("변환된 Excel이 빈값입니다. \nPDF파일 혹은 PDF타입을 다시 확인해주세요.");
                    $("#btnSubmitLenovo").prop("disabled", false);
                } else {
                    windowOpenInPostLenovo(fileName, data)
                    $("#btnSubmitLenovo").prop("disabled", false);
                    link = window.location.href;
                    if (link.includes("=")) {
                        link = link.split("?")[0];
                        window.location.replace(link + "?param=lenovo");
                    } else {
                        window.location.replace(link + "?param=lenovo");
                    }
                }
            }
        });
        e.stopPropagation();
    })

    function windowOpenInPostLenovo(fileName, dataList)
    {
        var mapFormLenovo = document.createElement("form");
        mapFormLenovo.setAttribute("id", "formLenovo");
        mapFormLenovo.name = "formLenovoName";
        mapFormLenovo.method = "POST";
        mapFormLenovo.target ="target";
        mapFormLenovo.action = "/excel/lenovo";
        debugger;
        if (dataList.length > 0){
            var mapInput = document.createElement("input");
            mapInput.type = "hidden";
            mapInput.name = 'fileName';
            mapInput.value = fileName;
            mapFormLenovo.appendChild(mapInput);
            for (var i = 0; i < dataList.length; i++){
                var mapInput = document.createElement("input");
                mapInput.type = "hidden";
                mapInput.name = 'data';
                mapInput.value = dataList[i].htsCode + "^" + dataList[i].productIdentification +"^" + dataList[i].ctryOfOrigin + "^" +dataList[i].productDescription
                                + "^" + dataList[i].quantity + "^" + dataList[i].uom + "^" + dataList[i].unitPrice + "^" + dataList[i].amount + "^" + dataList[i].invoiceNo
                                + "^" + dataList[i].invoiceTotalAmount + "^" + dataList[i].totalGrossWeight;
                mapFormLenovo.appendChild(mapInput);

            }
            document.body.appendChild(mapFormLenovo);
        }

        mapLenovo = window.open("","_self");
        if (mapLenovo) {
            $("#formLenovo").submit();
        } else {
            alert('You must allow popups for this map to work.');
        }
    }
});