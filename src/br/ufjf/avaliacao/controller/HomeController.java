package br.ufjf.avaliacao.controller;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.persistent.impl.AvaliacaoDAO;
import br.ufjf.avaliacao.persistent.impl.SemestreDAO;

public class HomeController extends GenericController {

	@Command
	public void addEditaSemestres() { // adiciona se não houver semestre na data atual ou edita se houver(apenas o titulo)
		
			Window window = (Window) Executions.createComponents(
					"/semestres.zul", null, null);
			window.doModal();
	}
	

}

