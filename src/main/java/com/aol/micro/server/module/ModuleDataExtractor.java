package com.aol.micro.server.module;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import lombok.AllArgsConstructor;

import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.aol.micro.server.auto.discovery.RestResource;
import com.aol.micro.server.servers.model.FilterData;
import com.aol.micro.server.servers.model.ServerData;
import com.aol.micro.server.servers.model.ServletData;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@AllArgsConstructor
public class ModuleDataExtractor {

	private final Module module;
	
	public  ImmutableList<RestResource> getRestResources( AnnotationConfigWebApplicationContext rootContext){
			List resources = Lists.newArrayList();
			module.getRestResourceClasses().forEach(it -> resources.addAll(rootContext.getBeansOfType(it).values()));
			module.getDefaultResources().forEach(it -> resources.addAll(rootContext.getBeansOfType(it).values()));
			return ImmutableList.copyOf(resources);
		
	}
	
	public List<FilterData> createFilteredDataList(ServerData data) {
		Map<String,Filter> filterMap = module.getFilters(data);
		return filterMap.entrySet().stream().map( e -> { 
			return new FilterData(e.getKey(), e.getValue().getClass().getName(), new DelegatingFilterProxy(e.getValue()));
		}).collect(Collectors.toList());
	}
	
	public List<ServletData> createServletDataList(ServerData data) {
		Map<String,Servlet> servletMap = module.getServlets(data);
		return servletMap.entrySet().stream().map( e -> { 
			return new ServletData( e.getValue().getClass().getName(), e.getValue().getClass(), e.getKey());
		}).collect(Collectors.toList());
	}
	
}