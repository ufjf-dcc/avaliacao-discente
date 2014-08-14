package br.ufjf.avaliacao.controller;

import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

public class HomeController extends GenericController {

	
	@Command
	public void semestres()
	{
		Window window = (Window) Executions.createComponents(
				"/semestres.zul", null, null);
		window.doModal();
	}
}
