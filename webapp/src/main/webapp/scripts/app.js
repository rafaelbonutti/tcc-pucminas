'use strict';

angular.module('professor-service',['ngRoute','ngResource'])
  .config(['$routeProvider', function($routeProvider) {
    $routeProvider
      .when('/',{templateUrl:'views/landing.html',controller:'LandingPageController'})
      .when('/Professors',{templateUrl:'views/Professor/search.html',controller:'SearchProfessorController'})
      .when('/Professors/new',{templateUrl:'views/Professor/detail.html',controller:'NewProfessorController'})
      .when('/Professors/edit/:ProfessorId',{templateUrl:'views/Professor/detail.html',controller:'EditProfessorController'})
      .otherwise({
        redirectTo: '/'
      });
  }])
  .controller('LandingPageController', function LandingPageController() {
  })
  .controller('NavController', function NavController($scope, $location) {
    $scope.matchesRoute = function(route) {
        var path = $location.path();
        return (path === ("/" + route) || path.indexOf("/" + route + "/") == 0);
    };
  });
