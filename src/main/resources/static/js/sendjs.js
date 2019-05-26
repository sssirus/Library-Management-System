/**
 * Created by hyh on 2017/8/14.
 */
function createXMLHttpRequest()
{
    //判断浏览器是否支持ActiveXObject对象
    if(window.ActiveXObject)
    {
        xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    //判断浏览器是否支持XMLHttpRequest对象
    else if (window.XMLHttpRequest)
    {
        xmlHttp = new XMLHttpRequest();

    }
}

function handleStateChange() {
    if (xmlHttp.readyState == 4) {
        if (xmlHttp.status == 200) {

            $('#search_output').html(xmlHttp.response);
            $("#search_result").show();
            $("#loading").hide();
        }
    }
}

function sendquestion() {

    createXMLHttpRequest();
    //设置在请求结束后调用handleStateChange函数
    xmlHttp.onreadystatechange = handleStateChange;
    //用get方法请求服务器端的simple.xml
    xmlHttp.open("POST","question",true);

    var question=eval(document.getElementById('search_input')).value;
    //发送请求
    var form=new FormData();
    form.append("question",question);
    console.log(form);
    xmlHttp.send(form);
}