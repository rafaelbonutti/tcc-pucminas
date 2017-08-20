package br.pucminas.web.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


public class Curriculo implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private int version;
	private Integer ano;
	private Integer semestre;
	private Curso curso;
	private Set<GradeCurricular> gradeCurricular = new HashSet<GradeCurricular>();

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
		if (!(obj instanceof Curriculo)) {
			return false;
		}
		Curriculo other = (Curriculo) obj;
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

	public Integer getSemestre() {
		return semestre;
	}

	public void setSemestre(Integer semestre) {
		this.semestre = semestre;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (semestre != null)
			result += "semestre: " + semestre;
		return result;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Curso getCurso() {
		return this.curso;
	}

	public void setCurso(final Curso curso) {
		this.curso = curso;
	}

	public Set<GradeCurricular> getGradeCurricular() {
		return this.gradeCurricular;
	}

	public void setGradeCurricular(final Set<GradeCurricular> gradeCurricular) {
		this.gradeCurricular = gradeCurricular;
	}

}