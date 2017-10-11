package com.macys.rx;

import org.apache.commons.lang.StringUtils;

public class Person {
	String name;
	Integer age;
	public Person(String string, int i) {
		this.name = string;
		this.age = i;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return StringUtils.defaultIfBlank(name, "") + " : age : "+this.age;
	}
	
}
