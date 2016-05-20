<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
	<meta charset="utf-8">
	<title></title>
	<link href="./resources/css/log_style.css" type="text/css" rel="stylesheet">
</head>
<body>
	<header class="header">
		<div class="header-logo"></div>
	</header>
	<div class="mid-wrap">
		<div class="mid-container">
			<div class="mid-container-left"></div>
			<div class="mid-contanier-right">
				<form id="userMsg" method="post">
					<div class="form-title">
						Rubic会员 <a href="http://172.22.146.20/CubeApiStore/html/signUp.html" class="signup">
						<i class="signup-icon"></i>
							立即注册
							</a>
					</div>
					<div class="tips" id="tips">
						<i></i>
						<span class="tips-text"></span>
					</div>
						<div class="username">
							<i></i>
							<input type="text" id="username" class="form-weights" placeholder="邮箱/用户名/已验证的手机">
						</div>
						<div class="psw">
							<i></i>
							<input type="password" id="psw" class="form-weights" placeholder="密码">
						</div>
						<div class="quick">
							<input type="checkbox" id="rememberKey" id="remember-key"></input>
							<label for="remember">7天之内免登录</label>
							<a href="#">忘记密码</a>
						</div>
						<div class="submit-btn">
							<input type="button" id="btn" value="登  录">
						</div>
						<div class="cooperation">
							<div>使用合作网站帐号登录</div>
							<ul>
								<li class="weixin-icon"></li>
								<li class="qq-icon"></li>
								<li class="weibo-icon"></li>
							</ul>
						</div>
						<div>
						<input type="hidden" id="gotoURL">
						<input type="hidden" id="encodedticketKey">
						<input type="hidden" id="expiry">
						</div>
				</form>
			</div>	
		</div>
	</div>
	<footer>
		<div class="copyright"></div>
	</footer>
	<script type="text/javascript" src="resources/js/jquery.js"></script>
	<script type="text/javascript" src="resources/js/login.js"></script>
</body>
</html>