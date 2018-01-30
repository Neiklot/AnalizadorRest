package com.danielbenitez.BeFeDictionary;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterNamesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created for you by Daniel Benítez García dbenitez83@live.com
 *
 */
public class App {
	public static void main(String[] args) {

		String folder="/tmp";
		String packageName="com.danielbenitez";
		try {
			File file = new File(folder + "/report2.jsp");
			StringBuffer output = new StringBuffer();

			output.append("<html ng-controller='MyController' ng-app='scanner'> "
					+ "<style>.div-table {"
					+ " display: table; "
					+ " width: auto; "
					+ " background-color: #eee; "
					+ " border: 1px solid #666666; "
					+ " width:100% !important; "
					+ " border-spacing: 5px; /* cellspacing:poor IE support for  this */ "
					+ " } "
					+ ".div-table-row { "
					+ " display: table-row; "
					+ " idth:100% !important;  "
					+ " clear: both; "
					+ " } "
					+ " .div-table-col { "
					+ " float: left; /* fix for  buggy browsers */ "
					+ " display: table-column; "
					+ " } </style>"
					+ "<script src='https://ajax.googleapis.com/ajax/libs/angularjs/1.6.7/angular.min.js'></script>"
					+ "<script >"
					+ "angular.module('scanner', [])"
					+ ".controller('MyController', ['$scope', function($scope) { "
					+ " console.log('loadded'); "
					+ " $scope.search=''; "
					+ " $scope.searchURL=''; "
					+ " }]); "
					+ "</script>"
					+ "<body> Nombre del controller: <input ng-model='search' type='text'> URL : <input ng-model='searchURL' type='text'> <form id='form1'>");

			Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(packageName)).setScanners(
					new MethodAnnotationsScanner(), new MethodParameterNamesScanner(), new TypeAnnotationsScanner(), new SubTypesScanner()));

			Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RestController.class);
			classes.addAll(reflections.getTypesAnnotatedWith(Controller.class));
			Map<String, List<String>> controllers = new HashMap<String, List<String>>();
			Map<String, List<String>> signatures = new HashMap<String, List<String>>();

			List<String> restControllers = new ArrayList<String>();

			for (Class clase : classes) {
				// System.out.println(clase.getName());
				restControllers.add(clase.getSimpleName());
				controllers.put(clase.getSimpleName(), new ArrayList());
				signatures.put(clase.getSimpleName(), new ArrayList());
			}

			Set<Method> methods = reflections.getMethodsAnnotatedWith(RequestMapping.class);
			for (Method method : methods) {
				RequestMethod[] req = method.getAnnotation(RequestMapping.class).method();
				String methodSignature = "<div class='div-table-col' style='padding-left:20px' ng-show=\"('"
						+ method.getAnnotation(RequestMapping.class).value()[0] + "'.includes(searchURL) || searchURL==='') \"><font color='red'>"
						+ method.getAnnotation(RequestMapping.class).value()[0] + " </font><font color='coral'>"
						+ (req != null && req.length > 0 ? req[0].name() : "") + "</font><br/><font color='orange'>" + method.getName()
						+ " </font></br>";
				List<String> parameterNames = reflections.getMethodParamNames(method);
				methodSignature += " <div class='div-table-col' style='padding-left:20px'><font color='Blue'>  Params: </font>" + parameterNames
						+ "</div><br/>";
				methodSignature += " <div class='div-table-col' style='padding-left:20px'><font color='blue'>  Return:</font>"
						+ method.getReturnType().getSimpleName() + "</div><br/></div>";
				if (controllers.get(method.getDeclaringClass().getSimpleName()) != null) {
					controllers.get(method.getDeclaringClass().getSimpleName()).add(methodSignature.trim());
					signatures.get(method.getDeclaringClass().getSimpleName()).add(method.getAnnotation(RequestMapping.class).value()[0].trim());
				}
			}

			for (String key : controllers.keySet()) {
				String signaturesString = signatures.get(key).toString();
				output.append("<div ng-show=\"('" + key + "'.includes(search) || search==='') && ('" + signaturesString
						+ "'.includes(searchURL) || searchURL==='') \" class='div-table'><div class='div-table-row'>"
						+ "<div class='div-table-col' ng-value='" + key + "' ><font color='green'>" + key + " </font></div></br>"
						+ "<div class='div-table-col'>");
				List<String> metodos = controllers.get(key);
				for (String metodo : metodos) {
					output.append("<div class='div-table-row' style='margin-left:10px;margin-left:50px'>" + metodo + " </div>");
				}
				output.append("</div></div></div>");
			}

			output.append("</div>" + "</div>");

			output.append("</div></form></body><html>");
			FileOutputStream fos = new FileOutputStream(file);
			byte[] bytes = output.toString().getBytes();
			fos.write(bytes);
			fos.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
