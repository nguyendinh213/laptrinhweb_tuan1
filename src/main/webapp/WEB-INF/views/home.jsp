<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
	String base = request.getContextPath();
	String title = (String) request.getAttribute("title");
	String message = (String) request.getAttribute("message");
	String username = (String) request.getAttribute("username");
	if (title == null) title = "Home";
	if (message == null) message = "Hello";
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
		input[type=text]{flex:1;padding:10px 12px;border:1px solid var(--border);border-radius:6px}
		button{background:var(--primary);color:#fff;border:none;border-radius:6px;padding:10px 14px;cursor:pointer}
		button:hover{filter:brightness(.95)}
		.footer{text-align:center;color:var(--muted);padding:16px}
	</style>
</head>
<body>
	<header class="navbar">
		<div class="container">
			<a class="brand" href="<%= base %>/home">HelloServlet</a>
			<nav class="nav">
				<a href="<%= base %>/home">Home</a>
				<a href="<%= base %>/about">About</a>
				<a href="<%= base %>/text?value=Hello+World">Set Text</a>
				<% if (username == null) { %>
					<a href="<%= base %>/login">Login</a>
				<% } else { %>
					<span>Logged in as <strong><%= username %></strong></span> <a href="<%= base %>/logout">Logout</a>
				<% } %>
			</nav>
		</div>
	</header>
	<main class="container">
		<div class="card">
			<h1><%= message %></h1>
			<form action="<%= base %>/text" method="get" class="form-row">
				<input type="text" name="value" placeholder="Enter text" />
				<button type="submit">Set Text</button>
			</form>
		</div>
	</main>
	<footer class="footer">Basic Servlet Demo</footer>
</body>
</html> 