angular.module('professor-service').factory('ProfessorResource', function($resource){
	var resource = $resource('http://192.168.25.86:8085/rest/professores/:ProfessorId',{ProfessorId:'@id'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
	var Topology = topology();
	Topology.ajax("professor-service","/rest/professores");
	return resource;
});