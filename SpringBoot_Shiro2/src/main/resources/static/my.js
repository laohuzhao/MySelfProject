/* 这个 url 经常要用到：add 中的 save，edit 中的 save，独立出来共享 */
var url;

/* 1、添加用户 */
function openLinkAddDialog() {
    $("#dlg").dialog("open").dialog("setTitle", "添加友情链接信息");
    url = "/user/save";
}

/* 2、修改用户 */
function openLinkModifyDialog() {
    var selectedRows = $("#dg").datagrid("getSelections");
    if(selectedRows.length != 1) {
        $.messager.alert("系统提示", "请选择一条要编辑的数据！");
        return;
    }
    var row = selectedRows[0];
    /*EasyUI 回显信息*/
    $("#fm").form("load", row);
    $("#dlg").dialog("open").dialog("setTitle", "编辑友情链接信息");
    url = "/user/save?id=" + row.id;
}

/* 3、删除用户 */
function deleteLink() {
    var selectedRows = $("#dg").datagrid("getSelections");
    if(selectedRows.length == 0) {
        $.messager.alert("系统提示", "请选择要删除的数据！");
        return;
    }
    var strIds = [];
    /*SpringBoot 小于号要求*/
    for(var i = 0; i < selectedRows.length; i++) {
        strIds.push(selectedRows[i].id);
    }
    /* 转化 "1,2,3" 这种格式  */
    var ids = strIds.join(",");
    $.messager.confirm("系统提示", "您确定要删除这<font color='red'>" + selectedRows.length + "</font>条数据吗？", function(r) {
        if(r) {
            $.post("/user/delete", {
                ids: ids
            }, function(result) {
                /* 后台传来一个true */
                if(result.success) {
                    $.messager.alert("系统提示", "数据已成功删除！");
                    /*数据变动，需要重新加载数据，作用类似刷新网页*/
                    $("#dg").datagrid("reload");
                } else {
                    $.messager.alert("系统提示", "数据删除失败，请联系管理员！");
                }
            }, "json");
        }
    });
}

/* save 保存操作，位于 add、edit 中 */
function saveLink() {
    $("#fm").form("submit", {
        url: url,
        onSubmit: function() {
            /*前端验证，再次使用EasyUI 提供的校验 class*/
            return $(this).form("validate");
        },
        success: function(result) {
            var result = eval('(' + result + ')');
            /* 后台传来一个true */
            if(result.success) {
                $.messager.alert("系统提示", "保存成功！");
                resetValue();
                $("#dlg").dialog("close");
                /*数据变动，需要重新加载数据，作用类似刷新网页*/
                $("#dg").datagrid("reload");
            }
        }
    });
}

/* 清空表单数据 */
function resetValue() {
    $("#name").val("");
    $("#word").val("");
}

/* 关闭对话框 */
function closeLinkDialog() {
    resetValue();
    $("#dlg").dialog("close");
}

/* 4、增加搜索 */
function searchWebSite() {
    $("#dg").datagrid("load", {
        "name": $("#s_name").val(),
        "word": $("#s_word").val()
    });
}