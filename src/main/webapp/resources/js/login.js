


var username = $("#username");
var psw = $("#psw");
var submitBtn = $("#btn");
var tips = $("#tips");
var rememberKey = $("#rememberKey");
var userMsg = document.getElementById("userMsg");
var flag = 0;
var addr = getUrl();

function getUrl(){
	var url = window.location.search;
	if(url.indexOf("?")!=-1){
		url = "SSOAuth?action=login" + url.substr(url.indexOf("&"));
		//url="SSOAuth?action=login"+url.substr(16);
	}
	return url;
}


function clearTips(){
	tips.css("display","none");
}
username.focus(clearTips)
psw.focus(clearTips); 

//点击按钮提交表单
submitBtn.click(go);

//按下回车键提交表单
$("body").keydown(function(e){
	e=e||window.event
	if(e.keyCode==13){
		go();
	}
});
function go(){
	if(username.val()==""||psw.val()==""){
		tips.css({"display":"block","color":"red"}).text("请确认信息是否完整！");
		return;
	}
	else{

		if(rememberKey.is(":checked")){
			flag = 1;
		}
	
		var data = {
			"protocol_id":"A-7-2-1-request",
			"email":username.val(),
			"password":psw.val(),
			"autoLoginInWeek":flag
		}

		$.ajax({
		type:"post",
		//从地址栏获取参数拼接登录请求的url
		url:addr,
		
		//写死的url
		//url:"SSOAuth?action=login&setCookieURL=http://localhost:8080/CubeApiStore/setCookie&gotoURL=http://localhost:8080/CubeApiStore/user/loginCheck",
		data: data,
		dataType:"json",
		beforeSend:function(){
			submitBtn.css({"background":"#f3f3f3","color":"black","cursor":"not-allowed"});
			submitBtn.val("登陆中...");
		},
		success:function(data){	
			if(data.state=="1"){
				tips.css({"display":"block"}).text(data.msg);
				submitBtn.css({"background":"#1aa9d2","cursor":"pointer"}).val("登录");
			}else{
				
				userMsg.action=data.gotoURL;

				userMsg.submit();
				
			}
		},
		error:function(){
			tips.text("连接失败，请稍后重试！");
			tips.css({"display":"block","color":"red"});
			submitBtn.css({"background":"#1aa9d2","cursor":"pointer","color":"white"}).val("登 录");
		}
	})
	}
}

