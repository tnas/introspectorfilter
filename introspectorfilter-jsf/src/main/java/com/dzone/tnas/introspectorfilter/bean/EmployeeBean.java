package com.dzone.tnas.introspectorfilter.bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.dzone.tnas.introspectorfilter.DataService;
import com.dzone.tnas.introspectorfilter.Employee;


@ManagedBean
@ViewScoped
public class EmployeeBean {
	
    private List<Employee> employeeList;
    private List<Employee> filteredEmployeeList;

    @PostConstruct
    public void postConstruct() {
        employeeList = DataService.INSTANCE.getEmployeeList();
    }

    public List<Employee> getEmployeeList() {
        return employeeList;
    }

    public List<Employee> getFilteredEmployeeList() {
        return filteredEmployeeList;
    }

    public void setFilteredEmployeeList(List<Employee> filteredEmployeeList) {
        this.filteredEmployeeList = filteredEmployeeList;
    }

    public List<String> getDeptList(){
        return DataService.INSTANCE.getDepartments();
    }
}
