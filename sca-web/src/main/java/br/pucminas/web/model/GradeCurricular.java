package br.pucminas.web.model;

import java.io.Serializable;

public class GradeCurricular implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private int version;
	private Curriculo curriculo;
	private Long disciplina;
	private Disciplina disciplinaEntity;
	
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

	public Long getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(Long disciplina) {
		this.disciplina = disciplina;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (disciplina != null)
			result += "disciplina: " + disciplina;
		return result;
	}

	public Disciplina getDisciplinaEntity() {
		return disciplinaEntity;
	}

	public void setDisciplinaEntity(Disciplina disciplinaEntity) {
		this.disciplinaEntity = disciplinaEntity;
	}

}