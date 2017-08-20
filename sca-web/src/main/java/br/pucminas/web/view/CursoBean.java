package br.pucminas.web.view;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netflix.hystrix.HystrixInvokableInfo;
import com.netflix.ribbon.ClientOptions;
import com.netflix.ribbon.Ribbon;
import com.netflix.ribbon.http.HttpRequestTemplate;
import com.netflix.ribbon.http.HttpResourceGroup;
import com.netflix.ribbon.hystrix.FallbackHandler;

import br.pucminas.web.consul.ConsulServices;
import br.pucminas.web.consul.ServiceDiscovery;
import br.pucminas.web.model.Curso;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import rx.Observable;
import rx.observables.BlockingObservable;

/**
 * Backing bean for Curso entities.
 */

@Named
@Stateful
@ConversationScoped
public class CursoBean implements Serializable {

	@Inject @ConsulServices ServiceDiscovery services;

	private static final long serialVersionUID = 1L;

	public static final int HTTP_CREATED = 201;
	public static final int HTTP_NOCONTENT = 204;

	/*
	 * Support creating and retrieving Curso entities
	 */

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Curso curso;

	public Curso getCurso() {
		return this.curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	@Inject
	private Conversation conversation;

	public String create() {

		this.conversation.begin();
		this.conversation.setTimeout(1800000L);
		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.conversation.isTransient()) {
			this.conversation.begin();
			this.conversation.setTimeout(1800000L);
		}

		if (this.id == null) {
			this.curso = this.example;
		} else {
			this.curso = findById(getId());
		}
	}

	public Curso findById(Long id) {

		Curso response = services
				.getCursoService()
				.path(String.valueOf(id))
				.request(MediaType.APPLICATION_JSON)
				.get(Curso.class);

		return response;
	}

	/*
	 * Support updating and deleting Curso entities
	 */

	public String update() {

		this.conversation.end();

		try {
			Response response = services
					.getCursoService()
					.path(String.valueOf(id))
					.request(MediaType.APPLICATION_JSON)
					.put(Entity.entity(this.curso, MediaType.APPLICATION_JSON));

			if(response.getStatus() == HTTP_NOCONTENT){
				if (this.id == null)
					return "search?faces-redirect=true";
				else
					return "view?faces-redirect=true&id=" + this.curso.getId();
			}
			else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Ocorreu algum erro ao incluir. "));
				return null;
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	public String insert() {

		this.conversation.end();

		try {
			Response response = services
					.getCursoService()
					.request(MediaType.APPLICATION_JSON)
					.post(Entity.entity(this.curso, MediaType.APPLICATION_JSON));
			if(response.getStatus() == HTTP_CREATED){
				if (this.id == null)
					return "search?faces-redirect=true";
				else
					return "view?faces-redirect=true&id=" + this.curso.getId();
			}
			else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Ocorreu algum erro ao incluir "));
				return null;
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	public String delete() {
		this.conversation.end();

		try {
			Response response = services
					.getCursoService()
					.path(String.valueOf(id))
					.request(MediaType.APPLICATION_JSON)
					.delete();

			if(response.getStatus() == HTTP_NOCONTENT){
				return "search?faces-redirect=true";
			}
			else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Ocorreu algum erro ao deletar " + String.valueOf(id)));
				return null;
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/*
	 * Support searching Curso entities with pagination
	 */

	private int page;
	private long count;
	private List<Curso> pageItems;

	private Curso example = new Curso();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Curso getExample() {
		return this.example;
	}

	public void setExample(Curso example) {
		this.example = example;
	}

	public String search() {
		this.page = 0;
		return null;
	}

	public void paginate() {

		HttpResourceGroup httpResourceGroup = Ribbon.createHttpResourceGroup(
				"curso-service",
				ClientOptions.create()
				.withMaxAutoRetriesNextServer(3)
				.withLoadBalancerEnabled(true)
				);

		@SuppressWarnings({ "unchecked", "rawtypes" })
		HttpRequestTemplate<ByteBuf> template = httpResourceGroup
		.newTemplateBuilder("listAll", ByteBuf.class)
		.withMethod("GET")
		.withUriTemplate("/rest/cursos")
		.withFallbackProvider(new FallbackHandler() {
			@Override
			public Observable<ByteBuf> getFallback(HystrixInvokableInfo hystrixInvokableInfo, Map map) {
				System.out.println("<< Serving fallback result list >>");
				return Observable.just(cursoCachedResults);
			}
		})
		.build();

		BlockingObservable<ByteBuf> obs = template.requestBuilder()
				.withHeader("Content-Type", "application/json; charset=utf-8")
				.withRequestProperty("start",this.page * getPageSize())
				.withRequestProperty("max", getPageSize())
				.build()
				.observe().toBlocking();

		ByteBuf responseBuffer = obs.last().copy().retain();
		Gson gson = new Gson();
		if(responseBuffer.capacity()>0) {

			String payload = responseBuffer.toString(Charset.forName("UTF-8"));
			cursoCachedResults = responseBuffer;
			this.pageItems = gson.fromJson(payload, new TypeToken<List<Curso>>(){}.getType());

		} else {
			String payload = cursoCachedResults.toString(Charset.forName("UTF-8"));
			this.pageItems = gson.fromJson(payload, new TypeToken<List<Curso>>(){}.getType());
		}
	}

	public List<Curso> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Curso entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

/*	public List<Curso> getAll() {

		CriteriaQuery<Curso> criteria = this.entityManager.getCriteriaBuilder()
				.createQuery(Curso.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(Curso.class))).getResultList();
	}*/

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final CursoBean ejbProxy = this.sessionContext
				.getBusinessObject(CursoBean.class);

		return new Converter() {

			@Override
			public Object getAsObject(FacesContext context,
					UIComponent component, String value) {

				return ejbProxy.findById(Long.valueOf(value));
			}

			@Override
			public String getAsString(FacesContext context,
					UIComponent component, Object value) {

				if (value == null) {
					return "";
				}

				return String.valueOf(((Curso) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Curso add = new Curso();

	public Curso getAdd() {
		return this.add;
	}

	public Curso getAdded() {
		Curso added = this.add;
		this.add = new Curso();
		return added;
	}
	
	private ByteBuf cursoCachedResults = Unpooled.buffer();
}
