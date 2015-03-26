package br.ufjf.avaliacao.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import br.ufjf.avaliacao.persistent.impl.PerguntaDAO;
import br.ufjf.avaliacao.persistent.impl.PrazoQuestionarioDAO;

/**
 * DTO da Tabela {@code Questionario} contÃ©m os atributos e relacionamentos da
 * mesma.
 * 
 */
@Entity
@Table(name = "Questionario")
public class Questionario implements Serializable {
	public static final int COORD = 0, PROF = 1, AUTO = 2, INFRA = 3;

	private static final long serialVersionUID = 1L;

	/**
	 * Campo com ID do questionÃ¡rio. Relaciona com a coluna
	 * {@code idQuestionario} do banco e Ã© gerado por autoincrement do MySQL
	 * atravÃ©s das anotaÃ§Ãµes {@code @GeneratedValue(generator = "increment")}
	 * e {@code @GenericGenerator(name = "increment", strategy = "increment")}
	 * 
	 */
	@Id
	@Column(name = "idQuestionario", unique = true, nullable = false)
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int idQuestionario;

	@Column(name = "tituloQuestionario", length = 500, nullable = false)
	private String tituloQuestionario;

	@Column(name = "tipoQuestionario", length = 45, nullable = false)
	private Integer tipoQuestionario;

	/**
	 * Campo com a situaÃ§Ã£o do questionÃ¡rio(ativo ou inativo). Relaciona com
	 * a coluna {@code ativo} do banco atravÃ©s da anotaÃ§Ã£o
	 * {@code @Column(name = "ativo", nullable = false)}.
	 */
	@Column(name = "ativo", nullable = false)
	private boolean ativo;

	/**
	 * Relacionamento 1 para N entre questionÃ¡rio e pergunta. Mapeada em
	 * {@link Pergunta} pela variÃ¡vel {@code questionario} e retorno do tipo
	 * {@code EAGER} que indica que serÃ¡ carregado automÃ¡ticamente este dado
	 * quando retornarmos o {@link Questionario} .
	 * 
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "questionario")
	private List<Pergunta> perguntas = new ArrayList<Pergunta>();

	/**
	 * Relacionamento N para 1 entre questionario e curso. Mapeando
	 * {@link Curso} na variÃ¡vel {@code curso} e retorno do tipo {@code LAZY}
	 * que indica que nÃ£o serÃ¡ carregado automÃ¡ticamente este dado quando
	 * retornarmos o {@link Questionario}.
	 * 
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idCurso", nullable = false)
	private Curso curso;

	/**
	 * Relacionamento 1 para N entre questionario e prazoQuestionario. Mapeada
	 * em {@link PrazoQuestionario} pela variÃ¡vel {@code questionario} e
	 * retorno do tipo {@code LAZY} que indica que nÃ£o serÃ¡ carregado
	 * automÃ¡ticamente este dado quando retornarmos a {@link Questionario} .
	 * 
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "questionario")
	private List<PrazoQuestionario> prazos = new ArrayList<PrazoQuestionario>();

	@Transient
	private String nomeTipoQuestionario;

	@Transient
	private boolean editingStatus;

	@Transient
	private String status;

	public int getIdQuestionario() {
		return idQuestionario;
	}

	public void setIdQuestionario(int idQuestionario) {
		this.idQuestionario = idQuestionario;
	}

	public String getTituloQuestionario() {
		return tituloQuestionario;
	}

	public void setTituloQuestionario(String tituloQuestionario) {
		this.tituloQuestionario = tituloQuestionario;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public List<Pergunta> getPerguntas() {
		return (new PerguntaDAO().getPerguntasQuestionario(this));
	}

	public void setPerguntas(List<Pergunta> perguntas) {
		this.perguntas = perguntas;
	}

	public Integer getTipoQuestionario() {
		return tipoQuestionario;
	}

	public void setTipoQuestionario(Integer tipoQuestionario) {
		this.tipoQuestionario = tipoQuestionario;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public String getNomeTipoQuestionario() {
		if (tipoQuestionario == 0)
			return "AvaliaÃ§Ã£o de CoordenaÃ§Ã£o";
		else if (tipoQuestionario == 1)
			return "AvaliaÃ§Ã£o de Professor";
		else if (tipoQuestionario == 2)
			return "Auto-AvaliaÃ§Ã£o";
		else
			return "AvaliaÃ§Ã£o de Infraestrutura";
	}
	

	public void setNomeTipoQuestionario(String nomeTipoQuestionario) {
		this.nomeTipoQuestionario = nomeTipoQuestionario;
	}

	public boolean isEditingStatus() {
		return editingStatus;
	}

	public void setEditingStatus(boolean editingStatus) {
		this.editingStatus = editingStatus;
	}

	public String getStatus() {
		if (isAtivo()) {
			return "Ativado";
		}
		return "Desativado";
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<PrazoQuestionario> getPrazos() {
		PrazoQuestionarioDAO pqDAO = new PrazoQuestionarioDAO();
		List<PrazoQuestionario> prazos = pqDAO.getPrazos(this);
		return pqDAO.getPrazos(this);
	}

	public void setPrazos(List<PrazoQuestionario> prazos) {
		this.prazos = prazos;
	}

}
