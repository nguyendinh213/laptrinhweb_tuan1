<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
	String base = request.getContextPath();
	String title = (String) request.getAttribute("title");
	String error = (String) request.getAttribute("error");
	if (title == null) title = "Login";
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8" />
	<title><%= title %></title>
	<style>
		:root{--bg:#f6f8fa;--primary:#1f6feb;--text:#24292f;--muted:#57606a;--card:#fff;--border:#d0d7de}
		*{box-sizing:border-box}
		body{margin:0;font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,Arial,"Noto Sans","Liberation Sans",sans-serif;background:var(--bg);color:var(--text)}
		.navbar{background:#0d1117;color:#c9d1d9}
		.navbar .container{display:flex;align-items:center;justify-content:space-between;padding:12px 16px}
		.brand{color:#c9d1d9;text-decoration:none;font-weight:600}
		.nav a{color:#c9d1d9;text-decoration:none;margin-left:12px}
		.nav a:hover{text-decoration:underline}
		.container{max-width:900px;margin:0 auto;padding:16px}
		.card{background:var(--card);border:1px solid var(--border);border-radius:8px;padding:20px;box-shadow:0 1px 2px rgba(0,0,0,.04)}
		h1{margin-top:0}
		.form-row{display:flex;gap:8px;margin-top:12px}
		input[type=text],input[type=password]{flex:1;padding:10px 12px;border:1px solid var(--border);border-radius:6px}
		button{background:var(--primary);color:#fff;border:none;border-radius:6px;padding:10px 14px;cursor:pointer}
		button:hover{filter:brightness(.95)}
		.footer{text-align:center;color:var(--muted);padding:16px}
		.error{color:#d73a49}
	</style>
</head>
<body>
	<header class="navbar">
		<div class="container">
			<a class="brand" href="<%= base %>/home">HelloServlet</a>
			<nav class="nav">
				<a href="<%= base %>/home">Home</a>
				<a href="<%= base %>/about">About</a>
			</nav>
		</div>
	</header>
	<main class="container">
		<div class="card">
			<h1>Login</h1>
			<% if (error != null && !error.isEmpty()) { %>
				<p class="error"><%= error %></p>
			<% } %>
			<form action="<%= base %>/login" method="post">
				<div class="form-row"><input name="username" placeholder="Username"/></div>
				<div class="form-row"><input type="password" name="password" placeholder="Password"/></div>
				<div class="form-row"><button type="submit">Login</button></div>
			</form>
			<p class="helper"><a href="<%= base %>/home">Back to Home</a></p>
		</div>
	</main>
	<footer class="footer">Basic Servlet Demo</footer>
</body>
</html> 