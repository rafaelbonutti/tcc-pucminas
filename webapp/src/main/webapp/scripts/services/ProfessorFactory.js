angular.module('professor-service').factory('ProfessorResource', function($resource){
    var resource = $resource('http://localhost:8080/rest/professores/:ProfessorId',{ProfessorId:'@id'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
    return resource;
});