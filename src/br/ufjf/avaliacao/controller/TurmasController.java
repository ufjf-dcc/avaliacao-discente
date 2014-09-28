package br.ufjf.avaliacao.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.record.formula.functions.Rows;
import org.hibernate.HibernateException;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.Media;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zhtml.Object;
import org.zkoss.zhtml.S;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox.ClickEvent;

import br.ufjf.avaliacao.business.GenericBusiness;
import br.ufjf.avaliacao.business.TurmaBusiness;
import br.ufjf.avaliacao.model.Curso;
import br.ufjf.avaliacao.model.Disciplina;
import br.ufjf.avaliacao.model.Semestre;
import br.ufjf.avaliacao.model.Turma;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.impl.AvaliacaoDAO;
import br.ufjf.avaliacao.persistent.impl.CursoDAO;
import br.ufjf.avaliacao.persistent.impl.DisciplinaDAO;
import br.ufjf.avaliacao.persistent.impl.SemestreDAO;
import br.ufjf.avaliacao.persistent.impl.TurmaDAO;
import br.ufjf.avaliacao.persistent.impl.UsuarioDAO;

public class TurmasController extends GenericController {

	private TurmaDAO turmaDAO = new TurmaDAO();
	private Turma turma = new Turma();
	private List<Turma> turmas = (List<Turma>) turmaDAO.getTodasTurmas();
	private DisciplinaDAO disciplinaDAO = new DisciplinaDAO();
	private Disciplina disciplina = new Disciplina();
	private List<Curso> cursos = new ArrayList<Curso>(); 
	private List<Disciplina> disciplinas = new ArrayList<Disciplina>();
	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private List<Usuario> professores = new ArrayList<Usuario>();
	private Usuario professor = new Usuario();

	@Init
	public void init() throws HibernateException, Exception {
		testaPermissaoAdmin();
	}

	@Command
	public void teste() {// 

		UsuarioDAO usuarioDAO = new UsuarioDAO();
		List<Usuario> usuariosTurma = new ArrayList<Usuario>();
		usuariosTurma = usuarioDAO.retornaUsuariosTurma(((Turma)session.getAttribute("turma_escolhida")));

	}
	
	@Command
	public void abrirCadastroTurmas() {// abre a janela de cadastrar turmas

		session.setAttribute("curso",null);
		session.setAttribute("letraTurma", "");
		session.setAttribute("semestre","");

		Window window = (Window) Executions.createComponents(
				"/cadastrarTurma.zul", null, null);
		window.doModal();
	}

	
	@Command
	public void letraTurma(@BindingParam("letraTurma") String letraTurma)
	{
		session.setAttribute("letraTurma", letraTurma);
	}
	
	@Command//seleciona o curso da turma nova para filtro
	public void  cursoSelecionado(@BindingParam("indiceCurso") int indice,
			@BindingParam("curso") Combobox curso,
			@BindingParam("semestre") Textbox semestre,
			@BindingParam("disciplina") Combobox disciplina)
	{
		SemestreDAO semestreDAO = new SemestreDAO();
		
		session.setAttribute("curso",cursos.get(indice));
		
		if(semestreDAO.getSemestreAtualCurso(cursos.get(indice))!=null)
		{
			semestre.setValue(semestreDAO.getSemestreAtualCurso(cursos.get(indice)).getNomeSemestre());
			session.setAttribute("semestre", semestreDAO.getSemestreAtualCurso(cursos.get(indice)).getNomeSemestre());
		}
		else
		{
			Messagebox.show("Ainda não existe um semestre ativo para este curso");
			curso.setValue(" ");
			semestre.setValue(" ");
		}
		disciplina.setValue(" ");

	}
	
	@Command//seleciona o curso da turma nova para filtro
	public void  disciplinaSelecionada(@BindingParam("indiceDisciplina") int indice)
	{
		session.setAttribute("disciplina",disciplinas.get(indice));
	}
	
	public Semestre getSemestreAtual()//retorna o semestre atual para a criação de turmas
	{
		return null;
	}

	@Command
	public void salvarTurma()// salva uma turma com as informações guardadas em sessão
	{
		GenericBusiness gb = new GenericBusiness();
		if(gb.campoStrValido(((String)session.getAttribute("letraTurma")))
				&& gb.campoStrValido(((String)session.getAttribute("semestre")))
				&& ((Disciplina)session.getAttribute("disciplina"))!=null)
		{
			Turma turma = new Turma();
			turma.setLetraTurma(((String)session.getAttribute("letraTurma")));
			turma.setDisciplina(((Disciplina)session.getAttribute("disciplina")));
			turma.setSemestre(((String)session.getAttribute("semestre")));
		
			TurmaDAO turmaDAO  = new TurmaDAO();
			if(turmaDAO.salvar(turma));
			Messagebox.show("Turma cadastrada com sucesso", null,
					new org.zkoss.zk.ui.event.EventListener<ClickEvent>() {
						public void onEvent(ClickEvent e) {
							if (e.getButton() == Messagebox.Button.OK)
								Executions.sendRedirect(null);
							else
								Executions.sendRedirect(null);
						}
					});
		
		}
		else
			Messagebox.show("As informações a respeito da turma não foram preenchidas corretamente ");
		
	}
	
	@Command // verifica qual turma foi a escolhida e abre a janela de usuarios dessa turma
	public void usuariosTurma(@BindingParam("botao") Button botao)
	{
		
		Component rows = botao.getParent().getParent().getParent();
		for(int i=0;i<rows.getChildren().size();i++)
		{
			if(rows.getChildren().get(i) == botao.getParent().getParent())
			{
				turma = turmas.get(i);
				break;
			}
		}
		
		session.setAttribute("turma_escolhida", turma);
		
		Window window = (Window) Executions.createComponents(
				"/turma.zul", null, null);
		window.doModal();
	}
	
	@Command
	public void excluirUsuarioTurma(@BindingParam("usuario") Usuario usuario)
	{
		System.out.println(usuario);
	}
	
	@Command
	public void adicionaUsuarioTurma()//abre a janela de adição de usuarios a turma
	{
		Window window = (Window) Executions.createComponents(
				"/usuariosTurma.zul", null, null);
		window.doModal();
	}
	
	
	@Command //verifica a disponibilidade para ad
	public void jaEstaAdicionado(@BindingParam("usuario") Usuario usuario,
			@BindingParam("botao") Button botao)
	{
		UsuarioDAO usuarioDAO = new UsuarioDAO();
		
		botao.setVisible(false);
		
		List<Usuario> usuariosTurma = usuarioDAO.retornaUsuariosTurma(((Turma)session.getAttribute("turma_escolhida")));
		
		for(int i=0;i<usuariosTurma.size();i++)
		{
			if(usuariosTurma.get(i).getIdUsuario() == usuario.getIdUsuario())
			{
				botao.getParent().getChildren().get(0).setVisible(false);
				botao.setVisible(true);
			}
		}
		
	}
	
	@Command
	public void abreCadastroTurma() {//para o CSV
		Window window = (Window) Executions.createComponents(
				"/cadastrarTurmaCsv.zul", null, null);
		window.doModal();
	}

	@Command("uploadTurmas")
	// FORMATO DE ENTRADA: código da disciplina, nome da
	// disciplina,turma,semestre, nome do prof,
	// email,senha do professor;
	public void uploadTurmas(@BindingParam("evt") UploadEvent evt)
			throws IOException {
		Media media = evt.getMedia();
		if (!media.getName().contains(".csv")) {
			Messagebox
					.show("Este não é um arquivo válido! Apenas CSV são aceitos.");
			return;
		}

		try {

			BufferedReader in = new BufferedReader(media.getReaderData());
			String linha;
			Turma turma;
			Usuario professor;
			Disciplina d;
			List<Turma> nturmas = new ArrayList<Turma>();

			while ((linha = in.readLine()) != null) {

				String conteudo[] = linha.split(";");
				// Verifica se possui professor
				if (usuarioDAO.retornaUsuario(conteudo[4]) != null) {
					professor = usuarioDAO.retornaUsuario(conteudo[4]);
				} else {
					professor = new Usuario(conteudo[4], conteudo[5],
							conteudo[6]);
					usuarioDAO.salvar(professor);
				}
				// Verifica se possui a disciplina
				if (disciplinaDAO.retornaDisciplinaCod(conteudo[0]) != null) {
					d = disciplinaDAO.retornaDisciplinaCod(conteudo[0]);
				} else {
					d = new Disciplina(conteudo[0], conteudo[1]);
					disciplinaDAO.salvar(d);
				}

				if (turmaDAO.retornaTurma(conteudo[2], conteudo[3],
						disciplinaDAO.retornaDisciplinaCod(conteudo[0])) == null) {
					turma = new Turma(
							disciplinaDAO.retornaDisciplinaCod(conteudo[0]),
							conteudo[2], conteudo[3]);
					List<Usuario> users = new ArrayList<Usuario>();
					users.add(professor);
					turma.setUsuarios(users);
					nturmas.add(turma);

				}
			}

			if (turmaDAO.salvarLista(nturmas))
				Messagebox.show("Usuarios cadastrados com sucesso", null,
						new org.zkoss.zk.ui.event.EventListener<ClickEvent>() {
							public void onEvent(ClickEvent e) {
								if (e.getButton() == Messagebox.Button.OK)
									Executions.sendRedirect(null);
								else
									Executions.sendRedirect(null);
							}
						});
		}

		catch (IllegalStateException e) {
			String csv = new String(media.getByteData());
			String linhas[] = csv.split("\\r?\\n");
			Turma turma;
			Usuario professor;
			Disciplina d;
			List<Turma> nturmas = new ArrayList<Turma>();

			for (String linha : linhas) {
				String conteudo[] = linha.split(";");
				// Verifica se possui professor
				if (usuarioDAO.retornaUsuario(conteudo[4]) != null) {
					professor = usuarioDAO.retornaUsuario(conteudo[4]);
				} else {
					professor = new Usuario(conteudo[4], conteudo[5],
							conteudo[6]);
					usuarioDAO.salvaOuEdita(professor);
				}
				// Verifica se possui a disciplina
				if (disciplinaDAO.retornaDisciplinaCod(conteudo[0]) != null) {
					d = disciplinaDAO.retornaDisciplinaCod(conteudo[0]);
				} else {
					d = new Disciplina(conteudo[0], conteudo[1]);
					disciplinaDAO.salvar(d);
				}

				if (turmaDAO.retornaTurma(conteudo[2], conteudo[3],
						disciplinaDAO.retornaDisciplinaCod(conteudo[0])) == null) {
					turma = new Turma(
							disciplinaDAO.retornaDisciplinaCod(conteudo[0]),
							conteudo[2], conteudo[3]);

					List<Usuario> users = new ArrayList<Usuario>();
					users.add(professor);
					turma.setUsuarios(users);

					nturmas.add(turma);

				}
			}

			if (turmaDAO.salvarLista(nturmas))
				Messagebox.show("Usuarios cadastrados com sucesso", null,
						new org.zkoss.zk.ui.event.EventListener<ClickEvent>() {
							public void onEvent(ClickEvent e) {
								if (e.getButton() == Messagebox.Button.OK)
									Executions.sendRedirect(null);
								else
									Executions.sendRedirect(null);
							}
						});

		} catch (Exception e) {
			Messagebox.show("Erro");

		}
	}

	@Command("uploadAlunos")
	// FORMATO DE ENTRADA:nome, email, senha, tipo de usuario, id curso, código
	// da disciplina,letra da turma, semestre
	public void uploadAlunos(@BindingParam("evt") UploadEvent evt)
			throws IOException {
		Media media = evt.getMedia();
		if (!media.getName().contains(".csv")) {
			Messagebox
					.show("Este não é um arquivo válido! Apenas CSV são aceitos.");
			return;
		}
		boolean operacao = false;

		try {

			// Leitura com o BufferReader que e mais rapido

			BufferedReader in = new BufferedReader(media.getReaderData());
			String linha;
			Usuario nusuario;
			Curso curso;
			CursoDAO cursoDAO = new CursoDAO();
			while ((linha = in.readLine()) != null) {
				String conteudo[] = linha.split(";");

				// verifica se o curso esta cadastrado, se nao ele cria
				if (cursoDAO.getCursoNome(conteudo[4]) != null) {
					curso = cursoDAO.getCursoNome(conteudo[4]);
				} else {
					curso = new Curso(conteudo[4]);
					cursoDAO.salvar(curso);
				}
				if (usuarioDAO.retornaUsuario(conteudo[0]) != null) {
					nusuario = usuarioDAO.retornaUsuario(conteudo[0]);
				} else {
					nusuario = new Usuario(conteudo[0], conteudo[1],
							conteudo[2], cursoDAO.getCursoNome(conteudo[4]),
							Integer.parseInt(conteudo[3]));
					usuarioDAO.salvar(nusuario);
				}

				turma = turmaDAO.retornaTurma(conteudo[6], conteudo[7],
						disciplinaDAO.retornaDisciplinaCod(conteudo[5]));

				turma = turmaDAO.retornaTurma(conteudo[6], conteudo[7],
						disciplinaDAO.retornaDisciplinaCod(conteudo[5]));

				if (usuarioDAO.retornaUsuario(conteudo[0]) == null) {
					List<Usuario> users = usuarioDAO.retornaAlunosTurma(turma);
					users.addAll(usuarioDAO.retornaProfessoresTurma(turma));
					users.add(nusuario);
					turma.setUsuarios(users);
					operacao = turmaDAO.editar(turma);
				}

			}

			if (operacao)
				Messagebox.show(
						"Usuarios cadastrados em suas turmas com sucesso",
						null,
						new org.zkoss.zk.ui.event.EventListener<ClickEvent>() {
							public void onEvent(ClickEvent e) {
								if (e.getButton() == Messagebox.Button.OK)
									Executions.sendRedirect(null);
								else
									Executions.sendRedirect(null);
							}
						});

		} catch (IllegalStateException e) {
			String csv = new String(media.getByteData());
			String linhas[] = csv.split("\\r?\\n");
			Usuario nusuario;
			Curso curso;
			Turma turma;
			CursoDAO cursoDAO = new CursoDAO();

			for (String linha : linhas) {
				String conteudo[] = linha.split(";");
				// verifica se o curso está cadastrado, se não ele cria
				if (cursoDAO.getCursoNome(conteudo[4]) != null) {
					curso = cursoDAO.getCursoNome(conteudo[4]);
				} else {
					curso = new Curso(conteudo[4]);
					cursoDAO.salvar(curso);
				}
				if (usuarioDAO.retornaUsuario(conteudo[0]) != null) {
					nusuario = usuarioDAO.retornaUsuario(conteudo[0]);
				} else {
					nusuario = new Usuario(conteudo[0], conteudo[1],
							conteudo[2], cursoDAO.getCursoNome(conteudo[4]),
							Integer.parseInt(conteudo[3]));
					usuarioDAO.salvar(nusuario);
				}

				turma = turmaDAO.retornaTurma(conteudo[6], conteudo[7],
						disciplinaDAO.retornaDisciplinaCod(conteudo[5]));

				if (usuarioDAO.retornaUsuario(conteudo[0]) == null) {
					List<Usuario> users = usuarioDAO.retornaAlunosTurma(turma);
					users.addAll(usuarioDAO.retornaProfessoresTurma(turma));
					users.add(nusuario);
					turma.setUsuarios(users);
					operacao = turmaDAO.editar(turma);
				}

			}
			if (operacao)
				Messagebox.show("Usuarios cadastrados com sucesso", null,
						new org.zkoss.zk.ui.event.EventListener<ClickEvent>() {
							public void onEvent(ClickEvent e) {
								if (e.getButton() == Messagebox.Button.OK)
									Executions.sendRedirect(null);
								else
									Executions.sendRedirect(null);
							}
						});

		}
	}

	@Command
	@NotifyChange("turmas")//exclui turmas que ainda não foram avaliadas e que não possuem usuarios cadastrados
	public void exclui(@BindingParam("turma") Turma turma) {
		AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
		if(avaliacaoDAO.jaAvaliouTurma(turma))
		{
			Messagebox.show("Turma não pode ser excluida.");
		}
		else
		{
			turmaDAO.exclui(turma);
			turmas.remove(turma);
			Messagebox.show("Turma excluida com sucesso!");
		}
	}

	@Command
	@NotifyChange({ "turmas", "turma" })
	public void cadastra() throws HibernateException, Exception {
		TurmaBusiness turmaBusiness = new TurmaBusiness();
		if (!turmaBusiness.cadastroValido(turma.getLetraTurma(),
				turma.getSemestre())) {
			Messagebox.show("Preencha todos os campos!");
		} else if (turmaBusiness.cadastrado(turma.getLetraTurma(),
				turma.getSemestre(), turma.getDisciplina())) {
			Messagebox.show("Turma já cadastrada!");
			turma = new Turma();
		} else if (turmaDAO.salvar(turma)) {
			turmas.add(turma);
			turma = new Turma();
			Messagebox.show("Turma cadastrada com sucesso!");
		}

	}

	@Command
	public void changeEditableStatus(@BindingParam("turma") Turma turma) {
		turma.setEditingStatus(!turma.isEditingStatus());
		refreshRowTemplate(turma);
	}

	@Command
	public void confirm(@BindingParam("turma") Turma turma)
	throws HibernateException, Exception {
		
		changeEditableStatus(turma);
		if(turmaDAO.retornaTurma(turma.getLetraTurma(), turma.getSemestre(), turma.getDisciplina())!=null)
		{
			Messagebox.show("Turma ja cadastrada.", null,
					new org.zkoss.zk.ui.event.EventListener<ClickEvent>() {
						public void onEvent(ClickEvent e) {
							if (e.getButton() == Messagebox.Button.OK)
								Executions.sendRedirect(null);
							else
								Executions.sendRedirect(null);
						}
					});
		}
		else
		{
			TurmaDAO turmaDAO = new TurmaDAO();
			turmaDAO.editar(turma);
			refreshRowTemplate(turma);
			Messagebox.show("Turma salva!");
		}
	}

	public void refreshRowTemplate(Turma turma) {
		BindUtils.postNotifyChange(null, null, turma, "editingStatus");
	}

	public List<Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(List<Turma> turmas) {
		this.turmas = turmas;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public Disciplina getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(Disciplina disciplina) {
		this.disciplina = disciplina;
	}

	public List<Usuario> getProfessoresCurso() {
		
		if(((Curso)session.getAttribute("curso"))!=null)
		{
			UsuarioDAO usuarioDAO = new UsuarioDAO();
			List<Usuario> profs = usuarioDAO.retornaProfessores();
			for(int i=0;i<profs.size();i++)
			{
				if(profs.get(i).getCurso().getIdCurso()!=((Curso)session.getAttribute("curso")).getIdCurso())
				{
					profs.remove(i);
					i--;
				}
			}
			professores = profs;
		}
		return professores;
	}

	public void setProfessores(List<Usuario> professores) {
		this.professores = professores;
	}
	
	public List<Curso> getCursos() {
		CursoDAO cursoDAO = new CursoDAO();
		cursos = cursoDAO.getTodosCursos();
		return cursos;
	} 

	public List<Disciplina> getDisciplinas() {
		DisciplinaDAO disciplinaDAO = new DisciplinaDAO();
		disciplinas = disciplinaDAO.getTodasDisciplinas();
		return disciplinas;
	}

	public void setDisciplinas(List<Disciplina> disciplinas) {
		this.disciplinas = disciplinas;
	}

	@Command
	public void getProfessores(@BindingParam("turma") Turma turma,
			@BindingParam("label") Label label) {
		List<Usuario> professores = (List<Usuario>) usuarioDAO
				.retornaProfessoresTurma(turma);
		String profs = "";
		if (!professores.isEmpty()) {
			for (Usuario prof : professores) {
				profs = profs.concat(prof.getNome() + ", ");
			}
			label.setValue(profs.substring(0, profs.length() - 2));
		} else {
			label.setValue("Sem professor");
		}

	}

	public Usuario getProfessor() {
		return professor;
	}

	public void setProfessor(Usuario professor) {
		this.professor = professor;
	}

	public List<Usuario> getUsuariosTurma()
	{
		if(turma!=null)
		{
			UsuarioDAO usuarioDAO = new UsuarioDAO();
			List<Usuario> usuariosTurma = new ArrayList<Usuario>();
			usuariosTurma = usuarioDAO.retornaUsuariosTurma(((Turma)session.getAttribute("turma_escolhida")));
			return usuariosTurma;
		}
		return new ArrayList<Usuario>();
	}
	
	public List<Usuario> getTodosUsuarios()
	{
		UsuarioDAO usuarioDAO = new UsuarioDAO();
		return usuarioDAO.getTodosUsuarios();
	}
}
