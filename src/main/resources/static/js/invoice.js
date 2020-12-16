$(document).ready(function() {

    // $("#btnSubmitUploadExcel").click(function (e) {
    //     // $("#excelUploadForm").submit();
    //     // location.href="/convert/excel"
    //
    //     var form = $('#fileUploadFormLenovo')[0];
    //     var data = new FormData(form);
    //     data.
    //     // var formData = new FormData($('#excelUploadForm')[0]);
    //     // var request = new XMLHttpRequest();
    //     // request.open("POST", "/convert/excel")
    //     // request.send(formData);
    // });

    // $("#btnSubmitUploadExcel").click(function (e) {
    //     //preventDefault 는 기본으로 정의된 이벤트를 작동하지 못하게 하는 메서드이다. submit을 막음
    //     e.preventDefault();
    //     var form = $('#excelUploadForm')[0];
    //     var data = new FormData(form);
    //     $.ajax({
    //         type: 'POST',
    //         enctype: 'multipart/form-data',
    //         url: '/convert/excel',
    //         data: data,
    //         processData: false,
    //         contentType: false,
    //         cache: false,
    //         success: function(data) {
    //             // debugger;
    //             window.open(data);
    //             // debugger;
    //             // var response = JSON.parse(data);
    //             // window.location = '/Report/Download?fileGuid=' + response.FileGuid
    //             //     + '&filename=' + response.FileName;
    //
    //         }
    //     });
    //
    // });

    $("#btnSubmitPdf").click(function (e) {
        var form = $('#daPdfUploadForm')[0];
        var data = new FormData(form);
        var fileName = $('#uploadDaPdf').val().replace(/.*(\/|\\)/, '');
        $("#btnSubmitPdf").prop("disabled", true);
        $.ajax({
            type: 'POST',
            enctype: 'multipart/form-data',
            url: '/api/upload/pdf/da',
            data: data,
            processData: false,
            contentType: false,
            cache: false,
            success: function(response) {
                if (response === "success") {
                    alert("성공적으로 업로드 되었습니다.");
                } else {
                    alert("업로드가 실패하였습니다.")
                }
            }
        });
        e.stopPropagation();
    });

    $("#searchPdfBtn").click(function() {
        var searchValue = $('#searchPdf').val();
        $("#searchPdfBtn").prop("disabled", true);
        $.ajax({
            type: 'GET',
            enctype: 'multipart/form-data',
            url: '/search/pdf?query=' + searchValue,
            processData: false,
            contentType: false,
            cache: false
        }).done(function(fragment) {
            debugger;
            $("#fileList").replaceWith(fragment);
        });
    });

});