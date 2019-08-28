package cn.wishhust.framework;

import cn.wishhust.framework.bean.Data;
import cn.wishhust.framework.bean.Handler;
import cn.wishhust.framework.bean.Param;
import cn.wishhust.framework.bean.View;
import cn.wishhust.framework.helper.*;
import cn.wishhust.framework.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


/**
 * 请求转发器
 *
 * 从Servlet3.0开始，配置Servlet支持注解方式
 *
 * @WebServlet
 * 属性
 * urlPatterns/value  指定Servlet处理的url， /*指任意路径
 * loadOnStartup      标记容器是否在应用启动时就加载这个Servlet，默认不配置或数值为负数时表示客户端第一次请求Servlet时再加载；0或正数表示启动应用就加载，正数情况下，数值越小，加载该Servlet的优先级越高；
 *
 *
 */
@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        // 初始化相关Helper类
        HelperLoader.init();
        // 获取ServletContext对象（用于注册Servlet）
        // ServletContext用来存放全局变量，每个Java虚拟机每个Web项目只有一个ServletContext,这个ServletContext是由Web服务器创建的，来保证它的唯一性。
        ServletContext servletContext = servletConfig.getServletContext();
        // 注册处理Jsp的servlet
        // 两个 getServletRegistration() 方法主要用于动态为 Servlet 增加映射信息，这等价于在 web.xml( 抑或 web-fragment.xml) 中使用 <servlet-mapping> 标签为存在的 Servlet 增加映射信息。
        // getServletRegistration() 根据servlet名称查找其注册信息，即ServletRegistration实例。
        ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
        jspServlet.addMapping(ConfigHelper.getAppJspPath() + "*");
        // 注册处理静态资源的默认Servlet
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        defaultServlet.addMapping(ConfigHelper.getAppAssetPath() + "*");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletHelper.init(req, resp);
        try {
            // 获取请求方法和请求路径
            String requestMethod = req.getMethod().toLowerCase();
            String requestPath = req.getPathInfo();
            if (requestPath.equals("/favicon.ico")) {
                return;
            }

            //获取Action处理器
            Handler handler = ControllerHelper.getHandler(requestMethod, requestPath);

            if (handler != null) {
                // 获取Controller类及其Bean实例
                Class<?> controllerClass = handler.getControllerClass();
                Object controllerBean = BeanHelper.getBean(controllerClass);
//            // 创建请求参数对象
//            HashMap<String, Object> paramMap = new HashMap<String, Object>();
//            Enumeration<String> paramNames = req.getParameterNames();
//            while (paramNames.hasMoreElements()) {
//                String paramName = paramNames.nextElement();
//                String paramValue = req.getParameter(paramName);
//                paramMap.put(paramName, paramValue);
//            }
//            String body = CodecUtil.decodeURL(StreamUtil.getString(req.getInputStream()));
//            if (StringUtil.isNotEmpty(body)) {
//                String[] params = StringUtil.splitString(body, "&");
//                if (ArrayUtil.isNotEmpty(params)) {
//                    for(String param : params) {
//                        String [] array = StringUtil.splitString(param, "=");
//                        if (ArrayUtil.isNotEmpty(array) && array.length == 2) {
//                            String paramName = array[0];
//                            String paramValue = array[1];
//                            paramMap.put(paramName, paramValue);
//                        }
//                    }
//                }
//            }
//            Param param = new Param(paramMap);
                Param param;
                if (UploadHelper.isMultipart(req)) {
                    param = UploadHelper.createParam(req);
                } else {
                    param = RequestHelper.createParam(req);
                }

                // 调用Action方法
                Method actionMethod = handler.getActionMethod();
                Object result;

                if (param.isEmpty()) {
                    result = ReflectionUtil.invokeMethod(controllerBean, actionMethod);
                } else {
                    result = ReflectionUtil.invokeMethod(controllerBean, actionMethod, param);
                }


                //处理Action方法返回值
                if (result instanceof View) {
                    // 返回 JSP 页面
//                View view = (View) result;
//                String path = view.getPath();
//                if (StringUtil.isNotEmpty(path)) {
//                    if (path.startsWith("/")) {
//                        resp.sendRedirect(req.getContextPath() + path);
//                    } else {
//                        Map<String, Object> model = view.getModel();
//                        for (Map.Entry<String, Object> entry : model.entrySet()) {
//                            req.setAttribute(entry.getKey(), entry.getValue());
//                        }
//                        req.getRequestDispatcher(ConfigHelper.getAppJspPath() + path).forward(req, resp);
//                    }
//                }
                    handleViewResult((View) result, req, resp);
                } else if (result instanceof Data) {
                    // 返回 JSON 数据
//                Data data = (Data) result;
//                Object model = data.getModel();
//                if (model != null) {
//                    resp.setContentType("application/json");
//                    resp.setCharacterEncoding("UTF-8");
//                    PrintWriter writer = resp.getWriter();
//                    String json = JsonUtil.toJson(model);
//                    writer.write(json);
//                    writer.flush();
//                    writer.close();
//                }
                    handleDataResult((Data) result, resp);
                }
            }
        } finally {
            ServletHelper.destroy();
        }

    }

    private void handleViewResult(View view, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String path = view.getPath();
        if (StringUtil.isNotEmpty(path)) {
            if (path.startsWith("/")) {
                // HttpServletResponse.sendRedirect()将请求转发到另一个servlet
                // sendRedirect()将请求转发到另一个servlet
                response.sendRedirect(request.getContextPath() + path);
            } else {
                Map<String, Object> model = view.getModel();
                for (Map.Entry<String, Object> entry : model.entrySet()) {
                    request.setAttribute(entry.getKey(), entry.getValue());
                }
                // RequestDispatcher.forward()将请求内部转发到另一个servlet
                // getRequestDispatcher ()方法获得RequestDispatcher对象，这个对象可以被用来内部转发。
                // getRequestDispatcher ()调用方法时，传递一个字符串包含要把请求的Servlet的名字。
                // 通过将HttpServletRequest和HttpServletResponse传给RequestDispatcher的对象，然后调用forword()方法。然后内部转发到另一个Servlet
                request.getRequestDispatcher(ConfigHelper.getAppJspPath()+path).forward(request,response);
            }
        }
    }

    private void handleDataResult(Data data, HttpServletResponse response) throws IOException {
        Object model = data.getModel();
        if (model != null) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            String json = JsonUtil.toJson(model);
            writer.write(json);
            writer.flush();
            writer.close();
        }
    }

}
