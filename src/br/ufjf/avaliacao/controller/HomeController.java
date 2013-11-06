package br.ufjf.avaliacao.controller;

import org.hibernate.HibernateException;
import org.zkoss.bind.annotation.Init;


public class HomeController extends GenericController  {
	
	
	@Init
	public void init() throws HibernateException, Exception {
		testaLogado();
	}

	
	public String teste(){
		return "teste";
	}
}

