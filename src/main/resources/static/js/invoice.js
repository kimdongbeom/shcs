$(document).ready(function() {

    $("#btnSubmitPdf").click(function (e) {
        var form = $('#daPdfUploadForm')[0];
        var data = new FormData(form);
        var fileName = $('#uploadDaPdf').val().replace(/.*(\/|\\)/, '');
        $("#btnSubmitPdf").prop("disabled", true);
        $.ajax({
            type: 'POST',
            enctype: 'multipart/form-data',
            url: '/upload/pdf/da',
            data: data,
            processData: false,
            contentType: false,
            cache: false,
        }).done(function(fragment) {
            $("#fileList").replaceWith(fragment);
            alert("성공적으로 업데이트 되었습니다.")
        });
        e.stopPropagation();
    });

    $("#searchPdfBtn").click(function() {
        var searchValue = $('#searchPdf').val();
        $.ajax({
            type: 'GET',
            enctype: 'multipart/form-data',
            url: '/search/pdf?query=' + searchValue,
            processData: false,
            contentType: false,
            cache: false
        }).done(function(fragment) {
            $("#fileList").replaceWith(fragment);
        });
    });
    
});