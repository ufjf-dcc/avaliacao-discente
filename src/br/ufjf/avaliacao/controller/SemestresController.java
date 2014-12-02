package br.ufjf.avaliacao.controller;

import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import br.ufjf.avaliacao.business.GenericBusiness;
import br.ufjf.avaliacao.model.Semestre;
import br.ufjf.avaliacao.persistent.impl.SemestreDAO;

public class SemestresController extends GenericController {

	private SemestreDAO semestreDAO = new SemestreDAO();
	private List<Semestre> semestres = semestreDAO.getSemestresCurso(usuario.getCurso());
	private Semestre semestre = semestreDAO.getSemestreAtualCurso(usuario.getCurso());
	

	@Command
	public void addOuEditarSemestre()
	{
		Window window = (Window) Executions.createComponents(
				"/add-semestre.zul", null, null);
		window.doModal();
	}
	
	@Command
	public void exibirAddOuEdita(@BindingParam("botao")  Button botao) //edita o label do botao
	{
		SemestreDAO semestreDAO = new SemestreDAO();
		if(semestreDAO.getSemestreAtualCurso(usuario.getCurso())!=null)
			botao.setLabel("Editar nome do semestre atual");
		else
			botao.setLabel("Adicionar novo semestre");

	}

	public Semestre getSemestre()
	{
		if(semestre == null)
			semestre = new Semestre(" ",new Date(),usuario.getCurso());
		return semestre;
	}
	
	@Command
	public void verificarSemestre(@BindingParam("tb") Textbox textbox)
	{
		if(semestreDAO.getSemestreAtualCurso(usuario.getCurso()) != null)
		{
			textbox.setReadonly(true);
		}
	}
	
	@Command
	public void criarSemestre(@BindingParam("titulo") String titulo,
			@BindingParam("dataFinal") Date dataFinal)
	{
		GenericBusiness gb = new GenericBusiness();
		if(gb.campoStrValido(titulo) && semestreDAO.validacaoTituloCurso(usuario.getCurso(), titulo))
		{
			
			if(semestreDAO.getSemestreAtualCurso(usuario.getCurso()) == null)
			{
				Semestre semestre = new Semestre();
				semestre.setDataFinalSemestre(dataFinal);
				semestre.setNomeSemestre(titulo);
				semestre.setCurso(usuario.getCurso());
				SemestreDAO semestreDAO = new SemestreDAO();
				semestreDAO.salvar(semestre);
				Messagebox.show("Semestre salvo", "Concluído",
						Messagebox.OK, Messagebox.INFORMATION,
						new EventListener<Event>() {
							@Override
							public void onEvent(Event event) throws Exception {
								Executions.sendRedirect(null);
							}
						});
			
			}
			else
			{
				Semestre semestre = semestreDAO.getSemestreAtualCurso(usuario.getCurso());
				semestre.setDataFinalSemestre(dataFinal);
				semestre.setNomeSemestre(titulo);
				semestre.setCurso(usuario.getCurso());
				SemestreDAO semestreDAO = new SemestreDAO();
				semestreDAO.editar(semestre);
				Messagebox.show("Semestre salvo", "Concluído",
						Messagebox.OK, Messagebox.INFORMATION,
						new EventListener<Event>() {
							@Override
							public void onEvent(Event event) throws Exception {
								Executions.sendRedirect(null);
							}
						});
			
			}
		}
		else
		{
			Messagebox.show("Titulo do semestre inválido");
		}
	}
	


	public List<Semestre> getSemestres() {
		return semestres;
	}

	public void setSemestres(List<Semestre> semestres) {
		this.semestres = semestres;
	}
}
