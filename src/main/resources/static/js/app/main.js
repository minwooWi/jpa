var main = {
    init : function () {
        var _this = this;
        $('#btn-save').on('click', function () {
            _this.save();
        });
        $('#btn-update').on('click', function () {
            _this.update();
        });
        $('#btn-delete').on('click', function () {
            _this.delete();
        });
    },
    save : function () {
        var data = {
            title: $('#title').val(),
            author: $('#author').val(),
            content: $('#content').val()
        };

        $.ajax({
            type: 'POST',
            url: '/posts',
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function() {
            alert('글이 등록되었습니다.');
            window.location.href = '/';
            // location.reload();
        }).fail(function (error) {
            alert(error);
        });
    },

    update : function () {
        var seq = $('#id').val();
        var data = {
            title: $('#title').val(),
            content: $('#content').val()
        };

        $.ajax({
            type: 'PUT',
            url: '/posts/'+seq,
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function() {
            alert('글이 수정 되었습니다.');
            window.location.href = '/';
            // location.reload();
        }).fail(function (error) {
            alert(error);
        });
    },

    delete : function () {
        var seq = $('#id').val();

        $.ajax({
            type: 'DELETE',
            url: '/posts/'+seq,
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
        }).done(function() {
            alert('글이 삭제 되었습니다.');
            window.location.href = '/';
            // location.reload();
        }).fail(function (error) {
            alert(error);
        });
    }
};

main.init();