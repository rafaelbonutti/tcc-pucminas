package br.pucminas.web.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GradeCurricular implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private int version;
	private Curriculo curriculo;
	private Disciplina disciplina;
	private Integer periodo;
	private Classificacao classificacao;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof GradeCurricular)) {
			return false;
		}
		GradeCurricular other = (GradeCurricular) obj;
		if (id != null) {
			if (!id.equals(other.id)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public Curriculo getCurriculo() {
		return this.curriculo;
	}

	public void setCurriculo(final Curriculo curriculo) {
		this.curriculo = curriculo;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Classificacao getClassificacao() {
		return classificacao;
	}

	public void setClassificacao(Classificacao classificacao) {
		this.classificacao = classificacao;
	}

	@Override
	public String toString() {
		return "GradeCurricular [" + (id != null ? "id=" + id + ", " : "") + "version=" + version + ", "
				+ (curriculo != null ? "curriculo=" + curriculo + ", " : "")
				+ (disciplina != null ? "disciplina=" + disciplina + ", " : "")
				+ (periodo != null ? "periodo=" + periodo + ", " : "")
				+ (classificacao != null ? "classificacao=" + classificacao : "") + "]";
	}

	public Disciplina getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(Disciplina disciplina) {
		this.disciplina = disciplina;
	}

}