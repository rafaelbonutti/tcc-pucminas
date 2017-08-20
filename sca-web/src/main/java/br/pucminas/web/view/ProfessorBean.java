package br.pucminas.web.view;

import java.io.Serializable;
import java.net.URI;
import java.net.URL;
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
import javax.naming.NamingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.Service;
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
import br.pucminas.web.model.Professor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import rx.Observable;
import rx.observables.BlockingObservable;

/**
 * Backing bean for Professor entities.
 */

@Named
@Stateful
@ConversationScoped
public class ProfessorBean implements Serializable {

	@Inject @ConsulServices ServiceDiscovery services;

	private static final long serialVersionUID = 1L;

	public static final int HTTP_CREATED = 201;
	public static final int HTTP_NOCONTENT = 204;

	private Long id;

	private Professor professor;

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
			this.professor = this.example;
		} else {
			this.professor = findById(getId());
		}
	}

	public Professor findById(Long id) {

		Professor response = services
				.getProfessorService()
				.path(String.valueOf(id))
				.request(MediaType.APPLICATION_JSON)
				.get(Professor.class);

		return response;
	}


	/*
	 * Support updating and deleting Professor entities
	 */

	public String update() {

		this.conversation.end();

		try {
			Response response = services
					.getProfessorService()
					.path(String.valueOf(id))
					.request(MediaType.APPLICATION_JSON)
					.put(Entity.entity(this.professor, MediaType.APPLICATION_JSON));

			if(response.getStatus() == HTTP_NOCONTENT){
				if (this.id == null)
					return "search?faces-redirect=true";
				else
					return "view?faces-redirect=true&id=" + this.professor.getId();
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
					.getProfessorService()
					.request(MediaType.APPLICATION_JSON)
					.post(Entity.entity(this.professor, MediaType.APPLICATION_JSON));
			if(response.getStatus() == HTTP_CREATED){
				if (this.id == null)
					return "search?faces-redirect=true";
				else
					return "view?faces-redirect=true&id=" + this.professor.getId();
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
					.getProfessorService()
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
	 * Support searching Professor entities with pagination
	 */

	private int page;
	private long count;
	private List<Professor> pageItems;

	private Professor example = new Professor();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Professor getExample() {
		return this.example;
	}

	public void setExample(Professor example) {
		this.example = example;
	}

	public String search() {
		this.page = 0;
		return null;
	}

	public void paginate() throws NamingException {

		HttpResourceGroup httpResourceGroup = Ribbon.createHttpResourceGroup(
				"professor-service",
				ClientOptions.create()
				.withMaxAutoRetriesNextServer(3)
				.withLoadBalancerEnabled(true)
				);

		@SuppressWarnings({ "unchecked", "rawtypes" })
		HttpRequestTemplate<ByteBuf> template = httpResourceGroup
		.newTemplateBuilder("listAll", ByteBuf.class)
		.withMethod("GET")
		.withUriTemplate("/rest/professores")
		.withFallbackProvider(new FallbackHandler() {
			@Override
			public Observable<ByteBuf> getFallback(HystrixInvokableInfo hystrixInvokableInfo, Map map) {
				System.out.println("<< Serving fallback result list >>");
				return Observable.just(professorCachedResults);
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
			professorCachedResults = responseBuffer;
			this.pageItems = gson.fromJson(payload, new TypeToken<List<Professor>>(){}.getType());

		} else {
			String payload = professorCachedResults.toString(Charset.forName("UTF-8"));
			this.pageItems = gson.fromJson(payload, new TypeToken<List<Professor>>(){}.getType());
		}


		WebTarget professorService = ClientBuilder
				.newClient()
				.target(
						UriBuilder.fromUri(URI.create(discoverServiceURI("professor-service")))
						.path("/rest/professores")
						.build()
						);

		Professor pr = professorService.path("1").request().get(Professor.class);
		System.out.println(pr.getNome());

		Response response =  services
				.getProfessorService()
				.queryParam("start",this.page * getPageSize())
				.queryParam("max", getPageSize())
				.request(MediaType.APPLICATION_JSON)
				.get(Response.class);

		this.pageItems = response.readEntity(new GenericType<List<Professor>>() {
		});

		this.count = Long.valueOf(response.getHeaderString("total"));
	}

	public String discoverServiceURI(String name) {

		ConsulClient client = getConsulClient();
		Map<String, Service> agentServices = client.getAgentServices().getValue();

		Service match = null;

		for (Map.Entry<String, Service> entry : agentServices.entrySet()) {
			if(entry.getValue().getService().equals(name)) {
				match = entry.getValue();
				break;
			}
		}

		if(null==match)
			throw new RuntimeException("Service '"+name+"' cannot be found!");

		try {
			URL url = new URL("http://"+match.getAddress()+":"+match.getPort());
			return url.toExternalForm();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private ConsulClient getConsulClient() {
		String consulHost = System.getProperty("consul.host", "127.0.0.1"); // DOCKER
		ConsulClient client = new ConsulClient(consulHost);
		return client;
	}

	public List<Professor> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Professor entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	/*	public List<Professor> getAll() {

		CriteriaQuery<Professor> criteria = this.entityManager
				.getCriteriaBuilder().createQuery(Professor.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(Professor.class)))
				.getResultList();
	}*/

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final ProfessorBean ejbProxy = this.sessionContext
				.getBusinessObject(ProfessorBean.class);

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

				return String.valueOf(((Professor) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Professor add = new Professor();

	public Professor getAdd() {
		return this.add;
	}

	public Professor getAdded() {
		Professor added = this.add;
		this.add = new Professor();
		return added;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Professor getProfessor() {
		return professor;
	}

	public void setProfessor(Professor professor) {
		this.professor = professor;
	}

	private ByteBuf professorCachedResults = Unpooled.buffer();
}
