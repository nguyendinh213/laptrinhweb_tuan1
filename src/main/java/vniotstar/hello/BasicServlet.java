package vniotstar.hello;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class BasicServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Map<String, String> USER_PASSWORD = new HashMap<>();
	private static final Map<String, String> USER_DISPLAY = new HashMap<>();
	static {
		USER_PASSWORD.put("admin", "123456");
		USER_DISPLAY.put("admin", "Administrator");
		USER_PASSWORD.put("dinh", "123");
		USER_DISPLAY.put("dinh", "Dinh");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		String path = normalizePath(req);

		if (path.equals("") || path.equals("/") || path.equals("/home")) {
			String username = getCurrentUsername(req);
			String message = username != null ? ("Welcome, " + username) : "Hello from Home page";
			req.setAttribute("title", "Home");
			req.setAttribute("message", message);
			req.setAttribute("username", username);
			RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/views/home.jsp");
			rd.forward(req, resp);
			return;
		}

		if ("/about".equals(path)) {
			renderHtml(req, resp, "About", "This is a basic Servlet demo", null);
			return;
		}

		if ("/text".equals(path)) {
			String value = req.getParameter("value");
			if (value == null) value = "";
			String greeting = value.isEmpty() ? "No value provided" : ("hello " + value);
			renderHtml(req, resp, "Text", greeting, null);
			return;
		}

		if ("/login".equals(path)) {
			if (getCurrentUsername(req) != null) {
				resp.sendRedirect(req.getContextPath() + "/home");
				return;
			}
			req.setAttribute("title", "Login");
			RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/views/login.jsp");
			rd.forward(req, resp);
			return;
		}

		if ("/logout".equals(path)) {
			HttpSession session = req.getSession(false);
			if (session != null) session.invalidate();
			resp.sendRedirect(req.getContextPath() + "/home");
			return;
		}

		if (path.startsWith("/api")) {
			writeEcho("GET", req, resp, false);
			return;
		}

		resp.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		req.setCharacterEncoding("UTF-8");
		String path = normalizePath(req);

		if ("/text".equals(path)) {
			String value = req.getParameter("value");
			if (value == null) value = readBody(req);
			if (value == null) value = "";
			String greeting = value.isEmpty() ? "No value provided" : ("hello " + value);
			renderHtml(req, resp, "Text", greeting, null);
			return;
		}

		if ("/login".equals(path)) {
			String username = req.getParameter("username");
			String password = req.getParameter("password");
			if (isValidUser(username, password)) {
				HttpSession session = req.getSession(true);
				session.setAttribute("user", new User(username, USER_DISPLAY.getOrDefault(username, username)));
				resp.sendRedirect(req.getContextPath() + "/home");
				return;
			} else {
				req.setAttribute("title", "Login");
				req.setAttribute("error", "Error 1");
				RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/views/login.jsp");
				rd.forward(req, resp);
				return;
			}
		}

		if (path.startsWith("/api")) {
			writeEcho("POST", req, resp, true);
			return;
		}

		resp.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String path = normalizePath(req);
		if (path.startsWith("/api")) {
			writeEcho("PUT", req, resp, true);
			return;
		}
		resp.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String path = normalizePath(req);
		if (path.startsWith("/api")) {
			writeEcho("DELETE", req, resp, false);
			return;
		}
		resp.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String path = normalizePath(req);
		if (path.startsWith("/api")) {
			String body = buildEchoBody("HEAD", req, false);
			resp.setContentType("text/plain; charset=UTF-8");
			resp.setCharacterEncoding("UTF-8");
			resp.setContentLength(body.getBytes(StandardCharsets.UTF_8).length);
			return;
		}
		resp.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setHeader("Allow", "GET,POST,PUT,DELETE,HEAD,OPTIONS,TRACE");
		String path = normalizePath(req);
		if (path.startsWith("/api")) {
			writeEcho("OPTIONS", req, resp, false);
			return;
		}
		resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	@Override
	protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("message/http");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		String method = req.getMethod();
		String requestUri = req.getRequestURI() + (req.getQueryString() == null ? "" : ("?" + req.getQueryString()));
		out.println(method + " " + requestUri + " " + req.getProtocol());
		Enumeration<String> headerNames = req.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String name = headerNames.nextElement();
			Enumeration<String> values = req.getHeaders(name);
			while (values.hasMoreElements()) {
				out.println(name + ": " + values.nextElement());
			}
		}
	}

	private String normalizePath(HttpServletRequest req) {
		String path = req.getPathInfo();
		if (path == null) path = req.getServletPath();
		if (path == null) path = "/";
		return path;
	}

	private void renderLoginForm(HttpServletRequest req, HttpServletResponse resp, String error) throws IOException {
		resp.setContentType("text/html; charset=UTF-8");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		String base = req.getContextPath();
		out.println("<!DOCTYPE html>");
		out.println("<html><head><meta charset=\"UTF-8\"><title>Login</title>");
		out.println("<style>:root{--bg:#f6f8fa;--primary:#1f6feb;--text:#24292f;--muted:#57606a;--card:#fff;--border:#d0d7de}*{box-sizing:border-box}body{margin:0;font-family:-apple-system,BlinkMacSystemFont,\"Segoe UI\",Roboto,Arial,\"Noto Sans\",\"Liberation Sans\",sans-serif;background:var(--bg);color:var(--text)}.navbar{background:#0d1117;color:#c9d1d9}.navbar .container{display:flex;align-items:center;justify-content:space-between;padding:12px 16px}.brand{color:#c9d1d9;text-decoration:none;font-weight:600}.nav a{color:#c9d1d9;text-decoration:none;margin-left:12px}.nav a:hover{text-decoration:underline}.container{max-width:900px;margin:0 auto;padding:16px}.card{background:var(--card);border:1px solid var(--border);border-radius:8px;padding:20px;box-shadow:0 1px 2px rgba(0,0,0,.04)}h1{margin-top:0}.form-row{display:flex;gap:8px;margin-top:12px}input[type=text],input[type=password]{flex:1;padding:10px 12px;border:1px solid var(--border);border-radius:6px}button{background:var(--primary);color:#fff;border:none;border-radius:6px;padding:10px 14px;cursor:pointer}button:hover{filter:brightness(.95)}.helper{color:var(--muted);font-size:.9rem}.footer{text-align:center;color:var(--muted);padding:16px}</style>");
		out.println("</head><body>");
		out.println("<header class=\"navbar\"><div class=\"container\"><a class=\"brand\" href=\"" + base + "/home\">HelloServlet</a><nav class=\"nav\"><a href=\"" + base + "/home\">Home</a><a href=\"" + base + "/about\">About</a></nav></div></header>");
		out.println("<main class=\"container\"><div class=\"card\">");
		out.println("<h1>Login</h1>");
		if (error != null && !error.isEmpty()) {
			out.println("<p style=\"color:#d73a49\">" + escape(error) + "</p>");
		}
		out.println("<form action=\"" + base + "/login\" method=\"post\">");
		out.println("<div class=\"form-row\"><input name=\"username\" placeholder=\"Username\"/></div>");
		out.println("<div class=\"form-row\"><input type=\"password\" name=\"password\" placeholder=\"Password\"/></div>");
		out.println("<div class=\"form-row\"><button type=\"submit\">Login</button></div>");
		out.println("</form>");
		out.println("<p class=\"helper\"><a href=\"" + base + "/home\">Back to Home</a></p>");
		out.println("</div></main>");
		out.println("<footer class=\"footer\">Basic Servlet Demo</footer>");
		out.println("</body></html>");
	}

	private void renderHtml(HttpServletRequest req, HttpServletResponse resp, String title, String text, String extraHtml) throws IOException {
		resp.setContentType("text/html; charset=UTF-8");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		String base = req.getContextPath();
		String username = getCurrentUsername(req);
		out.println("<!DOCTYPE html>");
		out.println("<html><head><meta charset=\"UTF-8\"><title>" + escape(title) + "</title>");
		out.println("<style>:root{--bg:#f6f8fa;--primary:#1f6feb;--text:#24292f;--muted:#57606a;--card:#fff;--border:#d0d7de}*{box-sizing:border-box}body{margin:0;font-family:-apple-system,BlinkMacSystemFont,\"Segoe UI\",Roboto,Arial,\"Noto Sans\",\"Liberation Sans\",sans-serif;background:var(--bg);color:var(--text)}.navbar{background:#0d1117;color:#c9d1d9}.navbar .container{display:flex;align-items:center;justify-content:space-between;padding:12px 16px}.brand{color:#c9d1d9;text-decoration:none;font-weight:600}.nav a{color:#c9d1d9;text-decoration:none;margin-left:12px}.nav a:hover{text-decoration:underline}.container{max-width:900px;margin:0 auto;padding:16px}.card{background:var(--card);border:1px solid var(--border);border-radius:8px;padding:20px;box-shadow:0 1px 2px rgba(0,0,0,.04)}h1{margin-top:0}.form-row{display:flex;gap:8px;margin-top:12px}input[type=text],input[type=password]{flex:1;padding:10px 12px;border:1px solid var(--border);border-radius:6px}button{background:var(--primary);color:#fff;border:none;border-radius:6px;padding:10px 14px;cursor:pointer}button:hover{filter:brightness(.95)}.helper{color:var(--muted);font-size:.9rem}.footer{text-align:center;color:var(--muted);padding:16px}</style>");
		out.println("</head><body>");
		out.println("<header class=\"navbar\"><div class=\"container\"><a class=\"brand\" href=\"" + base + "/home\">HelloServlet</a><nav class=\"nav\"><a href=\"" + base + "/home\">Home</a><a href=\"" + base + "/about\">About</a><a href=\"" + base + "/text?value=Hello+World\">Set Text</a>");
		if (username == null) {
			out.println("<a href=\"" + base + "/login\">Login</a>");
		} else {
			out.println("<span>Logged in as <strong>" + escape(username) + "</strong></span> <a href=\"" + base + "/logout\">Logout</a>");
		}
		out.println("</nav></div></header>");
		out.println("<main class=\"container\"><div class=\"card\">");
		out.println("<h1>" + escape(text) + "</h1>");
		out.println("<form action=\"" + base + "/text\" method=\"get\" class=\"form-row\">");
		out.println("<input type=\"text\" name=\"value\" placeholder=\"Enter text\"/>");
		out.println("<button type=\"submit\">Set Text</button>");
		out.println("</form>");
		if (extraHtml != null) {
			out.println(extraHtml);
		}
		out.println("</div></main>");
		out.println("<footer class=\"footer\">Basic Servlet Demo</footer>");
		out.println("</body></html>");
	}

	private String getCurrentUsername(HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		if (session == null) return null;
		Object userObj = session.getAttribute("user");
		if (userObj instanceof User) {
			return ((User) userObj).getDisplayName();
		}
		return null;
	}

	private boolean isValidUser(String username, String password) {
		if (username == null || password == null) return false;
		String expected = USER_PASSWORD.get(username);
		return expected != null && expected.equals(password);
	}

	private String escape(String s) {
		return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}

	private void writeEcho(String method, HttpServletRequest req, HttpServletResponse resp, boolean includeBody) throws IOException {
		resp.setContentType("text/plain; charset=UTF-8");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		out.print(buildEchoBody(method, req, includeBody));
	}

	private String buildEchoBody(String method, HttpServletRequest req, boolean includeBody) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("Method: ").append(method).append('\n');
		sb.append("Protocol: ").append(req.getProtocol()).append('\n');
		sb.append("RequestURI: ").append(req.getRequestURI()).append('\n');
		sb.append("ContextPath: ").append(req.getContextPath()).append('\n');
		sb.append("ServletPath: ").append(req.getServletPath()).append('\n');
		sb.append("PathInfo: ").append(req.getPathInfo()).append('\n');
		sb.append("QueryString: ").append(req.getQueryString()).append('\n');

		sb.append('\n').append("Parameters:").append('\n');
		for (Map.Entry<String, String[]> e : req.getParameterMap().entrySet()) {
			sb.append("  ").append(e.getKey()).append("=").append(Arrays.toString(e.getValue())).append('\n');
		}

		sb.append('\n').append("Headers:").append('\n');
		Enumeration<String> headerNames = req.getHeaderNames();
		while (headerNames != null && headerNames.hasMoreElements()) {
			String name = headerNames.nextElement();
			sb.append("  ").append(name).append(": ").append(Collections.list(req.getHeaders(name))).append('\n');
		}

		sb.append('\n').append("Cookies:").append('\n');
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie c : cookies) {
				sb.append("  ").append(c.getName()).append("=").append(c.getValue())
					.append("; Path=").append(c.getPath())
					.append("; HttpOnly=").append(c.isHttpOnly())
					.append('\n');
			}
		} else {
			sb.append("  none\n");
		}

		HttpSession session = req.getSession(false);
		sb.append('\n').append("Session:").append('\n');
		if (session != null) {
			sb.append("  id=").append(session.getId()).append(" isNew=").append(session.isNew()).append('\n');
		} else {
			sb.append("  none\n");
		}

		if (includeBody) {
			sb.append('\n').append("Body:").append('\n');
			sb.append(readBody(req)).append('\n');
		}
		return sb.toString();
	}

	private String readBody(HttpServletRequest req) throws IOException {
		String charset = req.getCharacterEncoding();
		if (charset == null || charset.isEmpty()) {
			charset = "UTF-8";
		}
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream(), charset))) {
			char[] buf = new char[4096];
			int r;
			while ((r = reader.read(buf)) != -1) {
				sb.append(buf, 0, r);
			}
		}
		return sb.toString();
	}
} 
