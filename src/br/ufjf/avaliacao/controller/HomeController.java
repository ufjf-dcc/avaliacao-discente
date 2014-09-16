package br.ufjf.avaliacao.controller;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.persistent.impl.AvaliacaoDAO;
import br.ufjf.avaliacao.persistent.impl.SemestreDAO;

public class HomeController extends GenericController {

	@Command
	public void addEditaSemestres() { // adiciona se não houver semestre na data atual ou edita se houver(apenas o titulo)
		SemestreDAO semestreDAO = new SemestreDAO();
		if(semestreDAO.getSemestresCurso(usuario.getCurso()).size() > 0)
		{
			Window window = (Window) Executions.createComponents(
					"/semestres.zul", null, null);
			window.doModal();
		}
		else
		{
			Messagebox.show("Não existem semestre ainda");
			Window window = (Window) Executions.createComponents(
					"/semestres.zul", null, null);
			window.doModal();
		
		}
	}
	

}

